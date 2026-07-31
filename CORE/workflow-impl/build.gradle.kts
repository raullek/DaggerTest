plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "az.less.core.workflow.impl"
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
    api(project(":core-workflow-api"))
    implementation(project(":core-di"))
    // Рендереры инфлейтят компоненты дизайн-системы (вместо «сырых» View).
    implementation(project(":core-designsystem"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // JSON разбираем встроенным org.json (фейковый сервер шлёт JSON-строки) — без Gson/Retrofit.
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}
