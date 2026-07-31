package az.less.core.workflow.api.model

/**
 * Один server-driven экран с тремя регионами (header/main/footer):
 *
 * - [header] — виджеты закреплённой «шапки» (под заголовком/степпером), не скроллятся;
 * - [widgets] — основное тело (main), скроллится в RecyclerView;
 * - [footer] — виджеты закреплённого «подвала» (над кнопками-событиями), не скроллятся.
 *
 * Кнопки событий (`events`) живут на уровне [WorkflowResponse] и рисуются в footer-регионе.
 */
data class WorkflowScreen(
    val title: String,
    val description: String? = null,
    val header: List<WorkflowWidget> = emptyList(),
    val widgets: List<WorkflowWidget> = emptyList(),
    val footer: List<WorkflowWidget> = emptyList(),
    val properties: Map<String, String> = emptyMap(),
) {
    /** Все поля экрана (header + body + footer) — для сбора значений и валидации. */
    val fields: List<WorkflowField>
        get() = (header + widgets + footer).flatMap { it.fields }
}
