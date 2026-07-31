plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Контракт Compose-рендера виджета (@Composable в ChatWidgetComposer) требует compose-компилятор.
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "az.less.feature.chat.api"
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
    implementation(project(":core-di")) // маркер FeatureApi

    // FragmentManager/FragmentActivity в сигнатурах лаунчера и быстрых действий.
    implementation(libs.androidx.fragment.ktx)
    // javax.inject.Qualifier для @ChatWidgets.
    implementation(libs.dagger)

    // Только runtime: в контракте лишь @Composable-сигнатура, без UI-артефактов.
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.runtime)
}
