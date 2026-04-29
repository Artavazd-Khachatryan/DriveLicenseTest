plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
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
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = System.getenv("KEYSTORE_PATH")?.let { file(it) }
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
    debugImplementation(libs.compose.ui.tooling)
}
