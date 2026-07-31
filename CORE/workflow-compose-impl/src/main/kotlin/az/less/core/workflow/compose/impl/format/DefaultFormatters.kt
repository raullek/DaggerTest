package az.less.core.workflow.compose.impl.format

import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.format.ServerValueFormatter
import az.less.core.workflow.compose.api.format.UiValueFormatter
import az.less.core.workflow.compose.api.format.ValueFormatter
import az.less.core.workflow.compose.api.model.FieldType
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Реестр форматтеров по типу поля. server-формат —
 * локаль-нейтральный без группировки, ui-формат — человекочитаемый.
 */
class DefaultFormatterRegistry @Inject constructor() : FormatterRegistry {

    private val byType: Map<FieldType, ValueFormatter> = mapOf(
        FieldType.MONEY to MoneyFormatter,
        FieldType.PHONE to PhoneFormatter,
        FieldType.DATE to DateFormatter,
    )

    override fun formatterFor(type: FieldType): ValueFormatter =
        byType[type] ?: ValueFormatter.IDENTITY
}

/** MONEY: server «12345.50» ↔ ui «12 345,50 ₽» (группировка тысяч, запятая). */
private val MoneyFormatter = ValueFormatter(
    ui = UiValueFormatter { server ->
        val number = server.replace(",", ".").toBigDecimalOrNull() ?: return@UiValueFormatter server
        val grouped = number.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
        val (int, frac) = grouped.split(".").let { it[0] to it.getOrElse(1) { "00" } }
        val sign = if (int.startsWith("-")) "-" else ""
        val digits = int.removePrefix("-").reversed().chunked(3).joinToString(" ").reversed()
        "$sign$digits,$frac ₽"
    },
    server = ServerValueFormatter { ui ->
        ui.replace("₽", "").replace(" ", "").replace(",", ".").trim()
    },
)

/** PHONE: server «79990000000» ↔ ui «+7 999 000-00-00». */
private val PhoneFormatter = ValueFormatter(
    ui = UiValueFormatter { server ->
        val digits = server.filter(Char::isDigit)
        if (digits.length != 11) return@UiValueFormatter server
        val d = digits
        "+${d[0]} ${d.substring(1, 4)} ${d.substring(4, 7)}-${d.substring(7, 9)}-${d.substring(9, 11)}"
    },
    server = ServerValueFormatter { ui -> ui.filter(Char::isDigit) },
)

/** DATE: транзит «как есть» (демо принимает dd.MM.yyyy и на ui, и на сервере). */
private val DateFormatter = ValueFormatter.IDENTITY

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (_: NumberFormatException) {
    null
}
