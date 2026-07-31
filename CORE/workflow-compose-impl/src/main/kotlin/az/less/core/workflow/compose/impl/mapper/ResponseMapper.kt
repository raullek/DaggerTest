package az.less.core.workflow.compose.impl.mapper

import az.less.core.workflow.compose.api.model.EventType
import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.model.FieldValidator
import az.less.core.workflow.compose.api.model.MessageType
import az.less.core.workflow.compose.api.model.StrategyDescriptor
import az.less.core.workflow.compose.api.model.WfEvent
import az.less.core.workflow.compose.api.model.WfField
import az.less.core.workflow.compose.api.model.WfMessage
import az.less.core.workflow.compose.api.model.WfProperties
import az.less.core.workflow.compose.api.model.WfReference
import az.less.core.workflow.compose.api.model.WfReferenceItem
import az.less.core.workflow.compose.api.model.WfReferences
import az.less.core.workflow.compose.api.model.WfScreen
import az.less.core.workflow.compose.api.model.WfWidget
import az.less.core.workflow.compose.api.model.WorkflowResponse
import az.less.core.workflow.compose.api.model.WorkflowResult
import az.less.core.workflow.compose.api.validation.ValidatorCompiler
import az.less.core.workflow.compose.impl.dto.BodyDto
import az.less.core.workflow.compose.impl.dto.EventDto
import az.less.core.workflow.compose.impl.dto.FieldDto
import az.less.core.workflow.compose.impl.dto.MessageDto
import az.less.core.workflow.compose.impl.dto.ResponseDto
import az.less.core.workflow.compose.impl.dto.ScreenDto
import az.less.core.workflow.compose.impl.dto.StrategyDto
import az.less.core.workflow.compose.impl.dto.ValidatorDto
import az.less.core.workflow.compose.impl.dto.WidgetDto
import javax.inject.Inject

/**
 * DTO → доменные бины. Компиляция валидаторов делегируется реестру
 * [ValidatorCompiler] (`validator.type -> компилятор`) — double-dispatch по типу поля, новый тип
 * добавляется без правок маппера. Дополнительно мапит секцию `strategies`.
 */
class ResponseMapper @Inject constructor(
    private val validatorCompilers: Map<String, @JvmSuppressWildcards ValidatorCompiler>,
) {

    fun map(dto: ResponseDto): WorkflowResponse {
        val body = dto.body
            ?: return WorkflowResponse(WorkflowResult.UNKNOWN, null, "", "", null)
        return WorkflowResponse(
            result = mapResult(body.result),
            pid = body.pid,
            flow = body.flow.orEmpty(),
            state = body.state.orEmpty(),
            screen = body.screen?.let(::mapScreen),
            events = body.events.map(::mapEvent),
            references = mapReferences(body),
            messages = body.messages.map(::mapMessage),
            exitUri = body.exitUri,
        )
    }

    private fun mapResult(raw: String?): WorkflowResult = when (raw?.uppercase()) {
        "SCREEN" -> WorkflowResult.SCREEN
        "END" -> WorkflowResult.END
        else -> WorkflowResult.UNKNOWN
    }

    private fun mapScreen(dto: ScreenDto): WfScreen = WfScreen(
        title = dto.title.orEmpty(),
        description = dto.description,
        header = dto.header.map(::mapWidget),
        widgets = dto.widgets.map(::mapWidget),
        footer = dto.footer.map(::mapWidget),
        strategies = dto.strategies.mapNotNull(::mapStrategy),
        properties = WfProperties(dto.properties),
    )

    private fun mapWidget(dto: WidgetDto): WfWidget = WfWidget(
        type = dto.type?.uppercase() ?: "FIELDSET",
        title = dto.title,
        description = dto.description,
        fields = dto.fields.map(::mapField),
        properties = WfProperties(dto.properties),
    )

    private fun mapField(dto: FieldDto): WfField {
        val type = FieldType.from(dto.type)
        return WfField(
            id = dto.id.orEmpty(),
            type = type,
            title = dto.title.orEmpty(),
            value = dto.value.orEmpty(),
            description = dto.description,
            referenceId = dto.referenceId,
            style = dto.style,
            readonly = dto.readonly,
            masked = dto.masked,
            visible = dto.visible,
            validators = dto.validators.mapNotNull { compileValidator(it, type) },
            properties = WfProperties(dto.properties),
        )
    }

    private fun compileValidator(dto: ValidatorDto, fieldType: FieldType): FieldValidator? {
        val compiler = validatorCompilers[dto.type?.uppercase()] ?: return null
        return compiler.compile(dto.value, dto.message.orEmpty(), fieldType)
    }

    private fun mapEvent(dto: EventDto): WfEvent = WfEvent(
        name = dto.name.orEmpty(),
        title = dto.title.orEmpty(),
        type = if (dto.type.equals("ROLLBACK", ignoreCase = true)) EventType.ROLLBACK else EventType.SUBMIT,
        hidden = dto.hidden,
    )

    private fun mapStrategy(dto: StrategyDto): StrategyDescriptor? {
        val type = dto.type ?: return null
        val looking = dto.lookingKey ?: return null
        val lookUp = dto.lookUpKey ?: return null
        return StrategyDescriptor(type, looking, lookUp, WfProperties(dto.config))
    }

    private fun mapReferences(body: BodyDto): WfReferences = WfReferences(
        map = body.references.mapValues { (key, items) ->
            WfReference(
                id = key,
                items = items.map {
                    WfReferenceItem(it.id.orEmpty(), it.text.orEmpty(), it.description, it.style)
                },
            )
        },
    )

    private fun mapMessage(dto: MessageDto): WfMessage = WfMessage(
        type = when (dto.type?.uppercase()) {
            "ERROR" -> MessageType.ERROR
            "FATAL" -> MessageType.FATAL
            "WARNING" -> MessageType.WARNING
            else -> MessageType.INFO
        },
        text = dto.text.orEmpty(),
        fieldId = dto.fieldId,
    )
}
