package az.less.core.workflow.impl.format

import az.less.core.workflow.api.format.FormatterRegistry
import az.less.core.workflow.api.format.ServerValueFormatter
import az.less.core.workflow.api.format.UiValueFormatter
import az.less.core.workflow.api.format.ValueFormatter
import az.less.core.workflow.api.model.FieldType
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

/**
 * Реестр форматтеров поверх мультибиндинга `Map<String, ValueFormatter>` (ключ — имя [FieldType]).
 * Форматтеры регистрируются `@IntoMap` (см. WorkflowFormattersModule), фича может добавить свой.
 */
class DefaultFormatterRegistry @Inject constructor(
    private val formatters: Map<String, @JvmSuppressWildcards ValueFormatter>,
) : FormatterRegistry {

    override fun formatterFor(type: FieldType): ValueFormatter =
        formatters[type.name] ?: ValueFormatter.IDENTITY
}

// --- Готовые пары форматтеров ядра (обе стороны server↔ui). ---

internal val MoneyValueFormatter = ValueFormatter(MoneyUiFormatter, MoneyServerFormatter)
internal val DateValueFormatter = ValueFormatter(DateUiFormatter, DateServerFormatter)
internal val PhoneValueFormatter = ValueFormatter(PhoneUiFormatter, PhoneServerFormatter)

// Деньги: сервер хранит копейки строкой "150000", UI показывает "1 500,00 ₽".
private object MoneyUiFormatter : UiValueFormatter {
    override fun toUi(serverValue: String): String {
        val kopecks = serverValue.toBigDecimalOrNull() ?: return serverValue
        return moneyFormat.format(kopecks.movePointLeft(2)) + " ₽"
    }
}

private object MoneyServerFormatter : ServerValueFormatter {
    override fun toServer(uiValue: String): String {
        val rubles = uiValue.replace("\\s|₽|,".toRegex()) { if (it.value == ",") "." else "" }
            .toBigDecimalOrNull() ?: return uiValue
        return rubles.movePointRight(2).toBigInteger().toString()
    }
}

private val moneyFormat: DecimalFormat = DecimalFormat(
    "#,##0.00",
    DecimalFormatSymbols(Locale("ru")).apply { groupingSeparator = ' '; decimalSeparator = ',' },
)

// Дата: сервер хранит ISO "2026-06-21", UI показывает "21.06.2026".
private object DateUiFormatter : UiValueFormatter {
    override fun toUi(serverValue: String): String {
        val p = serverValue.split("-")
        return if (p.size == 3) "${p[2]}.${p[1]}.${p[0]}" else serverValue
    }
}

private object DateServerFormatter : ServerValueFormatter {
    override fun toServer(uiValue: String): String {
        val p = uiValue.split(".")
        return if (p.size == 3) "${p[2]}-${p[1]}-${p[0]}" else uiValue
    }
}

// Телефон: сервер хранит "79000000000", UI показывает "+7 900 000-00-00".
private object PhoneUiFormatter : UiValueFormatter {
    override fun toUi(serverValue: String): String {
        val d = serverValue.filter { it.isDigit() }
        if (d.length != 11) return serverValue
        return "+${d[0]} ${d.substring(1, 4)} ${d.substring(4, 7)}-${d.substring(7, 9)}-${d.substring(9, 11)}"
    }
}

private object PhoneServerFormatter : ServerValueFormatter {
    override fun toServer(uiValue: String): String = uiValue.filter { it.isDigit() }
}

private fun String.toBigDecimalOrNull(): BigDecimal? =
    try { BigDecimal(trim()) } catch (e: NumberFormatException) { null }
