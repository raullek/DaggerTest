package az.less.core.workflow.compose.impl.dto

/**
 * Транспортные DTO ответа BDUI-сервера. Заполняются [JsonParser],
 * переводятся в домен [az.less.core.workflow.compose.impl.mapper.ResponseMapper].
 */
data class ResponseDto(val body: BodyDto?)

data class BodyDto(
    val result: String?,
    val pid: String?,
    val flow: String?,
    val state: String?,
    val screen: ScreenDto?,
    val events: List<EventDto>,
    val references: Map<String, List<ReferenceItemDto>>,
    val messages: List<MessageDto>,
    val exitUri: String?,
)

data class ScreenDto(
    val title: String?,
    val description: String?,
    val header: List<WidgetDto>,
    val widgets: List<WidgetDto>,
    val footer: List<WidgetDto>,
    val strategies: List<StrategyDto>,
    val properties: Map<String, String>,
)

data class WidgetDto(
    val type: String?,
    val title: String?,
    val description: String?,
    val fields: List<FieldDto>,
    val properties: Map<String, String>,
)

data class FieldDto(
    val id: String?,
    val type: String?,
    val title: String?,
    val value: String?,
    val description: String?,
    val referenceId: String?,
    val style: String?,
    val readonly: Boolean,
    val masked: Boolean,
    val visible: Boolean,
    val validators: List<ValidatorDto>,
    val properties: Map<String, String>,
)

data class ValidatorDto(val type: String?, val value: String?, val message: String?)

data class EventDto(val name: String?, val title: String?, val type: String?, val hidden: Boolean)

data class ReferenceItemDto(
    val id: String?,
    val text: String?,
    val description: String?,
    val style: String?,
)

data class MessageDto(val type: String?, val text: String?, val fieldId: String?)

/** Описание межвиджетной стратегии из JSON (новое относительно XML-движка). */
data class StrategyDto(
    val type: String?,
    val lookingKey: String?,
    val lookUpKey: String?,
    val config: Map<String, String>,
)
