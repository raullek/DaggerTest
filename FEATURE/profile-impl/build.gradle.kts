plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Compose-рендер виджета-вклада в чат (ProfileCardComposer).
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "az.less.feature.profile.impl"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        viewBinding = true
        compose = true
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
    // api: ProfileFeatureApi виден агрегатору холдеров (app) — на него ссылается @ClassKey.
    api(project(":feature-profile-api"))
    implementation(project(":core-di"))
    implementation(project(":core-network-api")) // NetworkCoreApi + тип Retrofit
    // Вклад фичи в ядро диплинков (ProfileDeeplinkModule, @IntoSet).
    implementation(project(":core-deeplink-api"))
    // Запуск server-driven флоу «редактирование профиля».
    implementation(project(":core-workflow-api"))
    // Запуск демо BDUI-флоу на Compose.
    implementation(project(":core-workflow-compose-api"))
    // Запуск целиком-SDUI фичи перевода (зависимость только на её :api).
    implementation(project(":feature-transfer-api"))
    // Переход в соседнюю фичу — зависимость только на её :api (не :impl).
    implementation(project(":feature-catalog-api"))
    // Вклад виджета PROFILE_CARD и быстрого действия в чат (контракты + запуск чата).
    implementation(project(":feature-chat-api"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)

    // Compose — только для Compose-рендера виджета-вклада в чат.
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}
