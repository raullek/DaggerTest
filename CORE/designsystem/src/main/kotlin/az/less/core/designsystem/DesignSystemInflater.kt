package az.less.core.designsystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Инфлейтер дизайн-системы: по [DsComponent] отдаёт готовую View компонента из XML.
 * Единственная точка, где ключ компонента превращается в конкретный лейаут.
 *
 * Без состояния — `Context` передаётся на каждый вызов. Потребитель (рендереры/вьюхолдеры
 * server-driven UI) инфлейтит компонент и биндит его через хелперы [DsViews].
 */
object DesignSystemInflater {

    @LayoutRes
    fun layoutOf(component: DsComponent): Int = when (component) {
        DsComponent.TEXT_FIELD -> R.layout.ds_text_field
        DsComponent.MONEY_FIELD -> R.layout.ds_money_field
        DsComponent.AMOUNT_FIELD -> R.layout.ds_amount_field
        // Дата использует текстовый компонент (ввод в формате дд.мм.гггг).
        DsComponent.DATE_FIELD -> R.layout.ds_text_field
        DsComponent.SELECT_FIELD -> R.layout.ds_select_field
        DsComponent.CHECKBOX_FIELD -> R.layout.ds_checkbox_field
        DsComponent.RADIO_FIELD -> R.layout.ds_radio_field
        DsComponent.SWITCH_FIELD -> R.layout.ds_switch_field
        DsComponent.SUMMARY_ROW -> R.layout.ds_summary_row
        DsComponent.BANNER -> R.layout.ds_banner
        DsComponent.BANNER_SUCCESS -> R.layout.ds_banner_success
        DsComponent.STEPPER -> R.layout.ds_stepper
        DsComponent.BUTTON -> R.layout.ds_button
        DsComponent.BUTTON_SECONDARY -> R.layout.ds_button_secondary
        DsComponent.SECTION_HEADER -> R.layout.ds_section_header
        DsComponent.CARD -> R.layout.ds_card
    }

    fun inflate(
        context: Context,
        component: DsComponent,
        parent: ViewGroup? = null,
        attachToParent: Boolean = false,
    ): View = LayoutInflater.from(context).inflate(layoutOf(component), parent, attachToParent)
}
