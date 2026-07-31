plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "az.less.daggertest"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "az.less.daggertest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // app = только DI-сборка: каркас + UI-хост + impl-модули (их холдеры). Логики нет.
    implementation(project(":core-di"))
    implementation(project(":entry"))
    implementation(project(":core-network-impl"))
    // Ядро диплинков: холдер + DeeplinkActivity (манифест мерджится сюда).
    implementation(project(":core-deeplink-impl"))
    // Ядро server-driven UI: его холдер (WorkflowHolderModule) в общем графе.
    implementation(project(":core-workflow-impl"))
    // BDUI-движок на Compose (параллельное поколение): его холдер + реестры в общем графе.
    implementation(project(":core-workflow-compose-impl"))
    implementation(project(":feature-profile-impl"))
    implementation(project(":feature-catalog-impl"))
    implementation(project(":feature-settings-impl"))
    // Фича перевода — целиком server-driven (её холдер + кастомный виджет в общий граф).
    implementation(project(":feature-transfer-impl"))
    // Чат с ассистентом: холдер + агрегация вкладов фич (CompoundChatWidgetsModule).
    implementation(project(":feature-chat-impl"))

    implementation(libs.material) // тема приложения

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
