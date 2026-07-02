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

    val ciBuildNumber = System.getenv("CI_BUILD_NUMBER")?.toIntOrNull()

    defaultConfig {
        applicationId = "com.drive.license.test"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        // versionName is manual (edit here for releases)
        versionName = "1.0.0"

        // versionCode: use CI build number if present, fall back locally
        versionCode = ciBuildNumber ?: 1

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
