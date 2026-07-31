plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Compose-рендер виджета-вклада в чат (TransferActionComposer).
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "az.less.feature.transfer.impl"
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
    api(project(":feature-transfer-api"))
    implementation(project(":core-di"))
    // Запуск SDUI-флоу + регистрация кастомного виджета (WidgetViewHolderFactory, WorkflowFlows).
    implementation(project(":core-workflow-api"))
    // Кастомный виджет «квитанция» инфлейтит компоненты дизайн-системы.
    implementation(project(":core-designsystem"))
    // Вклад виджета TRANSFER_ACTION и быстрого действия в чат.
    implementation(project(":feature-chat-api"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)

    // Compose — только для Compose-рендера виджета-вклада в чат.
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}
