plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "az.less.feature.settings.impl"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        viewBinding = true
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
    api(project(":feature-settings-api"))
    implementation(project(":core-di"))
    implementation(project(":core-network-api"))
    // Вклад фичи в ядро диплинков (SettingsDeeplinkModule, @IntoSet).
    implementation(project(":core-deeplink-api"))
    // Запуск демо server-driven флоу через ядро workflow.
    implementation(project(":core-workflow-api"))
    // Запуск демо BDUI-флоу на Compose.
    implementation(project(":core-workflow-compose-api"))
    // Переход в соседнюю фичу — зависимость только на её :api.
    implementation(project(":feature-profile-api"))
    // Вклад быстрого действия (@IntoSet) в чат — виджета нет, Compose не нужен.
    implementation(project(":feature-chat-api"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}
