import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.drive.license.test"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    val versionProperties = Properties().also { props ->
        rootProject.file("gradle/version.properties").inputStream().use { props.load(it) }
    }
    val appVersionMajor = versionProperties.getProperty("APP_VERSION_MAJOR", "1")
    val appVersionMinor = versionProperties.getProperty("APP_VERSION_MINOR", "0")
    val appVersionPatch = versionProperties.getProperty("APP_VERSION_PATCH", "0")
    val localBuildNumber = run {
        try {
            ProcessBuilder("git", "rev-list", "--count", "HEAD")
                .directory(rootProject.projectDir)
                .redirectErrorStream(true)
                .start()
                .inputStream.bufferedReader()
                .readText()
                .trim()
                .toInt()
        } catch (_: Exception) {
            0
        }
    }
    // Play Store uploads: set CI_BUILD_NUMBER in GitHub Actions (android-release.yml).
    // Local builds use git commit count — not for Play upload.
    val buildNumber = System.getenv("CI_BUILD_NUMBER")?.toIntOrNull() ?: localBuildNumber
    val appVersionName = "$appVersionMajor.$appVersionMinor.$appVersionPatch.$buildNumber"

    defaultConfig {
        applicationId = "com.drive.license.test"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        // X.Y.Z.build — X.Y.Z in gradle/version.properties; build from CI or git commit count locally
        versionName = appVersionName
        versionCode = buildNumber

        buildConfigField("String", "VERSION_NAME", "\"$appVersionName\"")
        buildConfigField(
            "String",
            "ANTHROPIC_API_KEY",
            "\"${System.getenv("ANTHROPIC_API_KEY") ?: ""}\""
        )
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = System.getenv("GOOGLE_MAPS_API_KEY") ?: ""
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    val signingPropertiesFile = System.getenv("SIGNING_PROPERTIES")
        ?.let { file(it) }
        ?.takeIf { it.exists() }
    val signingProperties = signingPropertiesFile?.let { propsFile ->
        Properties().also { props ->
            propsFile.inputStream().use { stream -> props.load(stream) }
        }
    }

    val keystorePath = System.getenv("KEYSTORE_PATH")
        ?: signingProperties?.getProperty("storeFile")
    val releaseStorePassword = System.getenv("STORE_PASSWORD")
        ?: signingProperties?.getProperty("storePassword")
    val releaseKeyAlias = System.getenv("KEY_ALIAS")
        ?: signingProperties?.getProperty("keyAlias")
    val releaseKeyPassword = System.getenv("KEY_PASSWORD")
        ?: signingProperties?.getProperty("keyPassword")

    val releaseSigningConfig = if (!keystorePath.isNullOrBlank()) {
        signingConfigs.create("release") {
            storeFile = file(keystorePath)
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    } else {
        null
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = releaseSigningConfig ?: signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":database"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.core)
    implementation(libs.compose.ui.tooling.preview)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    debugImplementation(libs.compose.ui.tooling)
}
