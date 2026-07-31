package az.less.core.workflow.impl.dto

import org.json.JSONObject

/**
 * Граница десериализации: JSON-строка сервера → [ResponseDto]. Разбираем встроенным
 * `org.json` (без внешних зависимостей).
 */
object WorkflowJsonParser {

    fun parse(json: String): ResponseDto {
        val root = JSONObject(json)
        val body = root.optJSONObject("body") ?: return ResponseDto(body = null)
        return ResponseDto(body = parseBody(body))
    }

    private fun parseBody(o: JSONObject): BodyDto = BodyDto(
        result = o.optStringOrNull("result"),
        pid = o.optStringOrNull("pid"),
        flow = o.optStringOrNull("flow"),
        state = o.optStringOrNull("state"),
        screen = o.optJSONObject("screen")?.let(::parseScreen),
        events = o.optJSONArray("events").mapObjects(::parseEvent),
        references = parseReferences(o.optJSONObject("references")),
        fieldMessages = parseFieldMessages(o.optJSONObject("fieldMessages")),
        messages = o.optJSONArray("messages").mapObjects(::parseMessage),
        exitUri = o.optStringOrNull("exitUri"),
    )

    private fun parseScreen(o: JSONObject): ScreenDto = ScreenDto(
        title = o.optStringOrNull("title"),
        description = o.optStringOrNull("description"),
        header = o.optJSONArray("header").mapObjects(::parseWidget),
        widgets = o.optJSONArray("widgets").mapObjects(::parseWidget),
        footer = o.optJSONArray("footer").mapObjects(::parseWidget),
        properties = parseStringMap(o.optJSONObject("properties")),
    )

    private fun parseWidget(o: JSONObject): WidgetDto = WidgetDto(
        type = o.optStringOrNull("type"),
        title = o.optStringOrNull("title"),
        description = o.optStringOrNull("description"),
        fields = o.optJSONArray("fields").mapObjects(::parseField),
        properties = parseStringMap(o.optJSONObject("properties")),
    )

    private fun parseField(o: JSONObject): FieldDto = FieldDto(
        id = o.optStringOrNull("id"),
        type = o.optStringOrNull("type"),
        title = o.optStringOrNull("title"),
        value = o.optStringOrNull("value"),
        description = o.optStringOrNull("description"),
        referenceId = o.optStringOrNull("referenceId"),
        style = o.optStringOrNull("style"),
        readonly = o.optBoolean("readonly", false),
        masked = o.optBoolean("masked", false),
        validators = o.optJSONArray("validators").mapObjects(::parseValidator),
        properties = parseStringMap(o.optJSONObject("properties")),
    )

    private fun parseValidator(o: JSONObject): ValidatorDto = ValidatorDto(
        type = o.optStringOrNull("type"),
        value = o.optStringOrNull("value"),
        message = o.optStringOrNull("message"),
    )

    private fun parseEvent(o: JSONObject): EventDto = EventDto(
        name = o.optStringOrNull("name"),
        title = o.optStringOrNull("title"),
        type = o.optStringOrNull("type"),
        hidden = o.optBoolean("hidden", false),
    )

    private fun parseMessage(o: JSONObject): MessageDto = MessageDto(
        type = o.optStringOrNull("type"),
        code = o.optStringOrNull("code"),
        text = o.optStringOrNull("text"),
    )

    private fun parseReferences(o: JSONObject?): Map<String, List<ReferenceItemDto>> {
        if (o == null) return emptyMap()
        val result = LinkedHashMap<String, List<ReferenceItemDto>>()
        for (key in o.keys()) {
            result[key] = o.optJSONArray(key).mapObjects { item ->
                ReferenceItemDto(item.optStringOrNull("id"), item.optStringOrNull("text"))
            }
        }
        return result
    }

    private fun parseFieldMessages(o: JSONObject?): Map<String, MessageDto> {
        if (o == null) return emptyMap()
        val result = LinkedHashMap<String, MessageDto>()
        for (key in o.keys()) {
            o.optJSONObject(key)?.let { result[key] = parseMessage(it) }
        }
        return result
    }

    private fun parseStringMap(o: JSONObject?): Map<String, String> {
        if (o == null) return emptyMap()
        val result = LinkedHashMap<String, String>()
        for (key in o.keys()) result[key] = o.optString(key)
        return result
    }

    // --- мелкие хелперы над org.json (null вместо "null"/пустых, безопасный обход массива) ---

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name) || !has(name)) null else optString(name)

    private inline fun <T> org.json.JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        val list = ArrayList<T>(length())
        for (i in 0 until length()) optJSONObject(i)?.let { list += transform(it) }
        return list
    }
}
