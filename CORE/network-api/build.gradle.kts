plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "az.less.core.network.api"
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
    // api: FeatureApi — часть публичной сигнатуры NetworkCoreApi (наследование).
    api(project(":core-di"))
    api(libs.retrofit) // тип Retrofit виден зависимым impl-модулям
}
