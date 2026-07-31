# Вариантность дженериков (out / in / *) — учебные демо

Мини-копия DI-слоя проекта (`FeatureHolder` / `ReleasableApi` / `releaseFeature`),
чтобы пощупать `Class<out ...>`, `out`/`in` и звёздную проекцию `<*>`.

- `demo_errors.kt` — 4 НАМЕРЕННЫЕ ошибки вариантности (не компилируется — в этом и смысл).
- `demo_ok.kt` — рабочий пример с `main()`: out/in в деле + жизненный цикл холдера (build → cache → release → rebuild).

> Папка `scratch/` не входит ни в один Gradle-модуль и не участвует в сборке проекта.

## Запуск (Windows PowerShell)

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$kotlinc = "C:\Program Files\Android\Android Studio\plugins\Kotlin\kotlinc\bin\kotlinc.bat"

# 1) Увидеть ошибки вариантности своими глазами:
& $kotlinc .\demo_errors.kt -d .\err_out.jar

# 2) Скомпилировать и запустить рабочий пример:
& $kotlinc .\demo_ok.kt -include-runtime -d .\ok.jar
& "$env:JAVA_HOME\bin\java.exe" -jar .\ok.jar
```

## Правило памяти

- тип только ВОЗВРАЩАЕТСЯ (producer) → `out` (`Class`, read-only `List`, `FeatureHolder`)
- тип только ПРИНИМАЕТСЯ (consumer) → `in` (`Logger`, `Comparator`, `Validator`)
- и туда, и сюда → без модификатора, инвариант (`MutableList`, `Box`, ключ `Map`)
- тип неизвестен/разнороден → `<*>` (звёздная проекция; напр. `Map<Class<*>, FeatureHolder<*>>`)
