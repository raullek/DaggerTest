plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Compose-рендер виджета-вклада в чат (CatalogShowcaseComposer).
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "az.less.feature.catalog.impl"
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
    api(project(":feature-catalog-api"))
    implementation(project(":core-di"))
    implementation(project(":core-network-api"))
    // Вклад фичи в ядро диплинков (CatalogDeeplinkModule, @IntoSet).
    implementation(project(":core-deeplink-api"))
    // Запуск server-driven флоу «оставить отзыв».
    implementation(project(":core-workflow-api"))
    // Переход в соседнюю фичу — зависимость только на её :api.
    implementation(project(":feature-settings-api"))
    // Вклад виджета CATALOG_SHOWCASE и быстрого действия в чат.
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
