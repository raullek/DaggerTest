package az.less.core.workflow.impl.mapper

import az.less.core.workflow.api.model.FieldType
import az.less.core.workflow.api.model.FieldValidator
import az.less.core.workflow.api.model.ReferenceItem
import az.less.core.workflow.api.model.WorkflowEvent
import az.less.core.workflow.api.model.WorkflowField
import az.less.core.workflow.api.model.WorkflowMessage
import az.less.core.workflow.api.model.WorkflowReferences
import az.less.core.workflow.api.model.WorkflowResponse
import az.less.core.workflow.api.model.WorkflowResult
import az.less.core.workflow.api.model.WorkflowScreen
import az.less.core.workflow.api.model.WorkflowWidget
import az.less.core.workflow.api.validation.ValidatorCompiler
import az.less.core.workflow.impl.dto.BodyDto
import az.less.core.workflow.impl.dto.EventDto
import az.less.core.workflow.impl.dto.FieldDto
import az.less.core.workflow.impl.dto.MessageDto
import az.less.core.workflow.impl.dto.ResponseDto
import az.less.core.workflow.impl.dto.ScreenDto
import az.less.core.workflow.impl.dto.ValidatorDto
import az.less.core.workflow.impl.dto.WidgetDto
import javax.inject.Inject

/**
 * DTO → доменные бины. Компиляция валидаторов делегируется
 * РЕЕСТРУ [ValidatorCompiler] (`validator.type -> компилятор`), собранному мультибиндингами —
 * новый тип валидатора добавляется без правок маппера.
 */
class WorkflowResponseMapper @Inject constructor(
    private val validatorCompilers: Map<String, @JvmSuppressWildcards ValidatorCompiler>,
) {

    fun map(dto: ResponseDto): WorkflowResponse {
        val body = dto.body ?: return WorkflowResponse(result = WorkflowResult.UNKNOWN)
        return WorkflowResponse(
            result = WorkflowResult.from(body.result),
            pid = body.pid,
            flow = body.flow,
            state = body.state,
            screen = body.screen?.let(::mapScreen),
            events = body.events.map(::mapEvent),
            references = mapReferences(body),
            fieldMessages = body.fieldMessages.mapValues { mapMessage(it.value) },
            messages = body.messages.map(::mapMessage),
            exitUri = body.exitUri,
        )
    }

    private fun mapScreen(dto: ScreenDto): WorkflowScreen = WorkflowScreen(
        title = dto.title.orEmpty(),
        description = dto.description,
        header = dto.header.map(::mapWidget),
        widgets = dto.widgets.map(::mapWidget),
        footer = dto.footer.map(::mapWidget),
        properties = dto.properties,
    )

    private fun mapWidget(dto: WidgetDto): WorkflowWidget = WorkflowWidget(
        type = dto.type ?: "FIELDSET",
        title = dto.title,
        description = dto.description,
        fields = dto.fields.map(::mapField),
        properties = dto.properties,
    )

    private fun mapField(dto: FieldDto): WorkflowField {
        val type = FieldType.from(dto.type)
        return WorkflowField(
            id = dto.id.orEmpty(),
            type = type,
            title = dto.title.orEmpty(),
            value = dto.value.orEmpty(),
            description = dto.description,
            referenceId = dto.referenceId,
            style = dto.style,
            readonly = dto.readonly,
            masked = dto.masked,
            validators = dto.validators.mapNotNull { compileValidator(it, type) },
            properties = dto.properties,
        )
    }

    /** Сырой валидатор + тип поля → типизированный через реестр компиляторов. */
    private fun compileValidator(dto: ValidatorDto, fieldType: FieldType): FieldValidator? {
        val compiler = validatorCompilers[dto.type?.uppercase()] ?: return null
        return compiler.compile(dto.value.orEmpty(), dto.message.orEmpty(), fieldType)
    }

    private fun mapEvent(dto: EventDto): WorkflowEvent = WorkflowEvent(
        name = dto.name.orEmpty(),
        title = dto.title,
        type = dto.type,
        hidden = dto.hidden,
    )

    private fun mapReferences(body: BodyDto): WorkflowReferences = WorkflowReferences(
        map = body.references.mapValues { (_, items) ->
            items.map { ReferenceItem(it.id.orEmpty(), it.text.orEmpty()) }
        },
    )

    private fun mapMessage(dto: MessageDto): WorkflowMessage = WorkflowMessage(
        text = dto.text.orEmpty(),
        type = when (dto.type?.uppercase()) {
            "ERROR" -> WorkflowMessage.Type.ERROR
            "FATAL" -> WorkflowMessage.Type.FATAL
            else -> WorkflowMessage.Type.INFO
        },
        code = dto.code,
    )
}
