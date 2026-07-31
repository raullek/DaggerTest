package az.less.core.workflow.api.model

/** Один пункт справочника (опция выпадающего списка). */
data class ReferenceItem(
    val id: String,
    val text: String,
)

/**
 * Все справочники экрана: `referenceId -> список опций`.
 *
 * Поле SELECT хранит `referenceId`, по нему рендерер достаёт список опций
 * через [itemsFor], а также подпись выбранного значения через [titleOf].
 */
data class WorkflowReferences(
    val map: Map<String, List<ReferenceItem>> = emptyMap(),
) {
    fun itemsFor(referenceId: String?): List<ReferenceItem> =
        referenceId?.let { map[it] } ?: emptyList()

    /** Подпись опции по её id внутри справочника (для отображения выбранного). */
    fun titleOf(referenceId: String?, itemId: String): String =
        itemsFor(referenceId).firstOrNull { it.id == itemId }?.text ?: itemId

    companion object {
        val EMPTY = WorkflowReferences()
    }
}
