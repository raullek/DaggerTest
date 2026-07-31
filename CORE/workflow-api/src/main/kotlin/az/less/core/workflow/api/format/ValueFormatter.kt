package az.less.core.workflow.api.format

/**
 * Форматтер «значение сервера → строка для UI».
 * Пример: ISO-дата `2026-06-21` → `21.06.2026`, копейки `150000` → `1 500,00 ₽`.
 */
fun interface UiValueFormatter {
    fun toUi(serverValue: String): String
}

/**
 * Форматтер «ввод UI → строка для сервера».
 * Пример: `21.06.2026` → `2026-06-21`, `+7 (900) 000` → `79000000000`.
 */
fun interface ServerValueFormatter {
    fun toServer(uiValue: String): String
}

/** Пара форматтеров одного типа поля (обе стороны конвертации). */
data class ValueFormatter(
    val ui: UiValueFormatter,
    val server: ServerValueFormatter,
) {
    companion object {
        /** Тождественный форматтер: значение не меняется ни в одну сторону. */
        val IDENTITY = ValueFormatter({ it }, { it })
    }
}
