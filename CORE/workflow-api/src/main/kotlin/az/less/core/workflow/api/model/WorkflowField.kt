package az.less.core.workflow.api.model

/**
 * Атомарная единица ввода server-driven экрана.
 *
 * @param id уникальный ключ поля — под ним значение уходит на сервер.
 * @param type тип ([FieldType]) — определяет рендер, валидаторы и форматтеры.
 * @param value текущее значение «как на сервере» (server-формат).
 * @param referenceId ссылка на список опций в [WorkflowReferences] (для SELECT).
 * @param style визуальная подсказка (необязательная).
 * @param readonly поле только для чтения.
 * @param masked значение замаскировано сервером (например, телефон `+7•••`).
 * @param validators скомпилированные клиентские валидаторы.
 */
data class WorkflowField(
    val id: String,
    val type: FieldType,
    val title: String,
    val value: String = "",
    val description: String? = null,
    val referenceId: String? = null,
    val style: String? = null,
    val readonly: Boolean = false,
    val masked: Boolean = false,
    val validators: List<FieldValidator> = emptyList(),
    val properties: Map<String, String> = emptyMap(),
)
