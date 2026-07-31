plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "az.less.core.workflow.api"
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
    // Контракт фичи виден всем: тип FeatureApi + DI-хелпер.
    api(project(":core-di"))
    // Лаунчер запускает хост-экран workflow через FragmentManager.
    api(libs.androidx.fragment.ktx)
    // Движок — на корутинах (StateFlow состояния), как и весь проект.
    api(libs.kotlinx.coroutines.android)
    // Базовый WidgetViewHolder — на RecyclerView (рендер экрана как список).
    api(libs.androidx.recyclerview)
    // @MapKey/@IntoMap: фичи регистрируют свои виджеты в реестр (мультибиндинги).
    api(libs.dagger)
}
