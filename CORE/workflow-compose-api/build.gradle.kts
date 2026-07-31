plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // typealias WidgetComposable хранит @Composable-лямбды → нужен compose-компилятор.
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "az.less.core.workflow.compose.api"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
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
    // Контракт фичи виден всем: тип FeatureApi + DI-хелпер.
    api(project(":core-di"))
    // Лаунчер запускает Compose-хост через FragmentManager.
    api(libs.androidx.fragment.ktx)
    // Движок — на корутинах (StateFlow состояния).
    api(libs.kotlinx.coroutines.android)
    // @MapKey/@IntoMap: фичи регистрируют свои виджеты/стратегии в реестры (мультибиндинги).
    api(libs.dagger)

    // Compose: рендер-контракты (WidgetComposable typealias, WorkflowInteraction).
    val composeBom = platform(libs.androidx.compose.bom)
    api(composeBom)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
}
