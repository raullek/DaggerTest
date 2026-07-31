pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DaggerTest"

// Подключение модуля с плоским именем из произвольной директории
fun includeModule(name: String, path: String) {
    include(":$name")
    project(":$name").projectDir = File(rootDir, path)
}

// Подключение пары Api/Impl модулей: директории <dirPrefix>-api / <dirPrefix>-impl
fun includeApiImplModules(namePrefix: String, dirPrefix: String) {
    includeModule("$namePrefix-api", "$dirPrefix-api")
    includeModule("$namePrefix-impl", "$dirPrefix-impl")
}

include(":app")     // только DI-сборка (Application + холдеры), без логики
include(":entry")   // UI-хост: MainActivity, стартует первую фичу через её лаунчер

//region CORE
includeModule("core-di", "CORE/di")
includeModule("core-designsystem", "CORE/designsystem")
includeApiImplModules("core-network", "CORE/network")
includeApiImplModules("core-deeplink", "CORE/deeplink")
includeApiImplModules("core-workflow", "CORE/workflow")
// BDUI-движок на Jetpack Compose (параллельное поколение, self-contained, см. BDUI-COMPOSE.md).
includeApiImplModules("core-workflow-compose", "CORE/workflow-compose")
//endregion

//region FEATURE
includeApiImplModules("feature-profile", "FEATURE/profile")
includeApiImplModules("feature-catalog", "FEATURE/catalog")
includeApiImplModules("feature-settings", "FEATURE/settings")
includeApiImplModules("feature-transfer", "FEATURE/transfer")
// Чат с ассистентом: экран-агрегатор, куда остальные фичи вкладывают свои виджеты и быстрые
// действия через @IntoMap/@IntoSet (см. CHAT.md).
includeApiImplModules("feature-chat", "FEATURE/chat")
//endregion
