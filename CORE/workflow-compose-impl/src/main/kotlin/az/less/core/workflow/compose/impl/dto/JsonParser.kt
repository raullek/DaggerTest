package az.less.core.workflow.compose.impl.dto

import org.json.JSONArray
import org.json.JSONObject

/**
 * Граница десериализации: JSON-строка → [ResponseDto] на встроенном `org.json` (без внешних
 * зависимостей). Включая парсинг секции `strategies`.
 */
object JsonParser {

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
        messages = o.optJSONArray("messages").mapObjects(::parseMessage),
        exitUri = o.optStringOrNull("exitUri"),
    )

    private fun parseScreen(o: JSONObject): ScreenDto = ScreenDto(
        title = o.optStringOrNull("title"),
        description = o.optStringOrNull("description"),
        header = o.optJSONArray("header").mapObjects(::parseWidget),
        widgets = o.optJSONArray("widgets").mapObjects(::parseWidget),
        footer = o.optJSONArray("footer").mapObjects(::parseWidget),
        strategies = o.optJSONArray("strategies").mapObjects(::parseStrategy),
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
        visible = o.optBoolean("visible", true),
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
        text = o.optStringOrNull("text"),
        fieldId = o.optStringOrNull("fieldId"),
    )

    private fun parseStrategy(o: JSONObject): StrategyDto = StrategyDto(
        type = o.optStringOrNull("type"),
        lookingKey = o.optStringOrNull("lookingKey"),
        lookUpKey = o.optStringOrNull("lookUpKey"),
        config = parseStringMap(o.optJSONObject("config")),
    )

    private fun parseReferences(o: JSONObject?): Map<String, List<ReferenceItemDto>> {
        if (o == null) return emptyMap()
        val result = LinkedHashMap<String, List<ReferenceItemDto>>()
        for (key in o.keys()) {
            result[key] = o.optJSONArray(key).mapObjects { item ->
                ReferenceItemDto(
                    id = item.optStringOrNull("id"),
                    text = item.optStringOrNull("text"),
                    description = item.optStringOrNull("description"),
                    style = item.optStringOrNull("style"),
                )
            }
        }
        return result
    }

    private fun parseStringMap(o: JSONObject?): Map<String, String> {
        if (o == null) return emptyMap()
        val result = LinkedHashMap<String, String>()
        for (key in o.keys()) result[key] = o.optString(key)
        return result
    }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name) || !has(name)) null else optString(name)

    private inline fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        val list = ArrayList<T>(length())
        for (i in 0 until length()) optJSONObject(i)?.let { list += transform(it) }
        return list
    }
}
