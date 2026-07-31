package az.less.core.designsystem

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Типизированный доступ к под-вьюхам компонентов ДС по стабильным id — публичный контракт
 * биндинга (инфлейт по ключу + доступ через эти хелперы), чтобы потребитель не лазил в `R.id` ДС.
 * Хелпер вызывается только для компонента, где соответствующая вьюха есть.
 */
fun View.dsLabel(): TextView = findViewById(R.id.ds_label)
fun View.dsInput(): EditText = findViewById(R.id.ds_input)
fun View.dsError(): TextView = findViewById(R.id.ds_error)
fun View.dsValue(): TextView = findViewById(R.id.ds_value)
fun View.dsCheckbox(): CheckBox = findViewById(R.id.ds_checkbox)
fun View.dsSuffix(): TextView = findViewById(R.id.ds_suffix)
fun View.dsTitle(): TextView = findViewById(R.id.ds_title)
fun View.dsSubtitle(): TextView = findViewById(R.id.ds_subtitle)
fun View.dsAction(): Button = findViewById(R.id.ds_action)
fun View.dsContainer(): ViewGroup = findViewById(R.id.ds_container)
fun View.dsRadioGroup(): RadioGroup = findViewById(R.id.ds_radio_group)
fun View.dsSwitch(): CompoundButton = findViewById(R.id.ds_switch)
fun View.dsSwitchText(): TextView = findViewById(R.id.ds_switch_text)
fun View.dsSummaryLabel(): TextView = findViewById(R.id.ds_summary_label)
fun View.dsSummaryValue(): TextView = findViewById(R.id.ds_summary_value)
fun View.dsBannerText(): TextView = findViewById(R.id.ds_banner)
fun View.dsStepper(): LinearLayout = findViewById(R.id.ds_stepper)

/**
 * Создать стилизованный [RadioButton] ДС (фабрика для радиогруппы — чтобы цвет/текст шли из ДС).
 */
fun ViewGroup.dsCreateRadioButton(id: Int, text: String): RadioButton =
    RadioButton(context).apply {
        this.id = id
        this.text = text
        setTextColor(ContextCompat.getColor(context, R.color.ds_on_surface))
    }

/**
 * Заполнить [dsStepper] точками: [steps] всего, [activeIndex] — активная (подсвечена primary).
 * `R` остаётся внутри ДС — потребитель не знает про drawables.
 */
fun LinearLayout.dsRenderStepperDots(steps: Int, activeIndex: Int) {
    removeAllViews()
    val size = resources.getDimensionPixelSize(R.dimen.ds_dot)
    val gap = resources.getDimensionPixelSize(R.dimen.ds_space_s)
    for (i in 0 until steps) {
        val dot = View(context).apply {
            setBackgroundResource(if (i == activeIndex) R.drawable.ds_dot_active else R.drawable.ds_dot)
        }
        val lp = LinearLayout.LayoutParams(size, size).apply { marginStart = gap; marginEnd = gap }
        addView(dot, lp)
    }
}
