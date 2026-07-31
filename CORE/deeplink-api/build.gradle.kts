plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "az.less.core.deeplink.api"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
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
    // Контракт фичи виден всем (как core-network-api): тип FeatureApi + DI-хелпер.
    api(project(":core-di"))
    // FragmentManager/FragmentActivity нужны контрактам шагов навигации.
    api(libs.androidx.fragment.ktx)
    // Конвейер шагов — на корутинах (в проекте нет RxJava).
    api(libs.kotlinx.coroutines.android)
}
