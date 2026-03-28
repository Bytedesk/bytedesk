package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents tool choice configuration for controlling which tools the model can use. Can
 * be either a string value ("none", "auto", "required") or a specific tool object.
 */
@Getter
@Setter
@JsonSerialize(using = ToolChoiceObj.ToolChoiceObjSerializer.class)
@JsonDeserialize(using = ToolChoiceObj.ToolChoiceObjDeserializer.class)
public class ToolChoiceObj {

	/**
	 * String option for tool choice ("none", "auto", "required").
	 */
	private final String optStr;

	/**
	 * Specific tool object when forcing the use of a particular tool.
	 */
	private final ToolObj optObj;

	private ToolChoiceObj(String optStr, ToolObj optObj) {
		this.optStr = optStr;
		this.optObj = optObj;
	}

	/**
	 * Creates a ToolChoiceObj with string mode.
	 * @param strValue The string value ("none", "auto", "required")
	 * @return ToolChoiceObj instance or null if invalid
	 */
	public static ToolChoiceObj of(String strValue) {
		if (strValue != null && !"none".equalsIgnoreCase(strValue) && !"auto".equalsIgnoreCase(strValue)
				&& !"required".equalsIgnoreCase(strValue)) {
			return null;
		}
		return new ToolChoiceObj(strValue, null);
	}

	/**
	 * Creates a ToolChoiceObj with object mode for forcing a specific tool.
	 * @param objValue The specific tool object to use
	 * @return ToolChoiceObj instance or null if invalid
	 */
	public static ToolChoiceObj of(ToolObj objValue) {
		if (objValue != null && objValue.getType() == null) {
			return null;
		}
		return new ToolChoiceObj(null, objValue);
	}

	@Override
	public String toString() {
		if (optStr != null) {
			return optStr;
		}
		else if (optObj != null) {
			return optObj.toString();
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ToolChoiceObj that = (ToolChoiceObj) o;
		return Objects.equals(optStr, that.optStr) && Objects.equals(optObj, that.optObj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(optStr, optObj);
	}

	/**
	 * Represents a specific tool that can be forced for use.
	 */
	@Getter
	@Setter
	public static class ToolObj {

		/**
		 * The type of the tool (typically "function").
		 */
		private String type;

		/**
		 * The name of the function when type is "function".
		 */
		private String functionName;

		@Override
		public String toString() {
			if ("function".equalsIgnoreCase(type)) {
				return "{\"type\": \"function\", \"function\": {\"name\": \"" + functionName + "\"}}";
			}
			else {
				return "{\"type\": \"" + type + "\"}";
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			ToolObj tool = (ToolObj) o;
			return Objects.equals(type, tool.type) && Objects.equals(functionName, tool.functionName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, functionName);
		}

	}

	// Serializer
	public static class ToolChoiceObjSerializer extends JsonSerializer<ToolChoiceObj> {

		@Override
		public void serialize(ToolChoiceObj value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException {
			if (value.getOptStr() != null) {
				gen.writeString(value.getOptStr());
			}
			else if (value.getOptObj() != null) {
				gen.writeRawValue(value.getOptObj().toString());
			}
			else {
				// value == null || (value.getOptStr() == null && value.getOptObj() ==
				// null
				gen.writeNull();
			}
		}

	}

	// Deserializer
	public static class ToolChoiceObjDeserializer extends JsonDeserializer<ToolChoiceObj> {

		@Override
		public ToolChoiceObj deserialize(JsonParser p, DeserializationContext context) {
			JsonToken token = p.currentToken();
			if (token == JsonToken.VALUE_NULL) {
				return null;
			}
			try {
				// Handle string format
				if (token == JsonToken.VALUE_STRING) {
					String text = p.getText().toLowerCase();
					if ("none".equals(text) || "auto".equals(text) || "required".equals(text)) {
						return ToolChoiceObj.of(text);
					}
					return null;
				}

				// Handle object format
				if (token == JsonToken.START_OBJECT) {
					ToolObj tool = new ToolObj();
					while (p.nextToken() != JsonToken.END_OBJECT) {
						String fieldName = p.currentName();
						p.nextToken();
						if ("type".equals(fieldName)) {
							tool.setType(p.getText());
						}
						else if ("function".equals(fieldName) && p.currentToken() == JsonToken.START_OBJECT) {
							tool.setType("function");
							while (p.nextToken() != JsonToken.END_OBJECT) {
								if ("name".equals(p.currentName())) {
									p.nextToken();
									tool.setFunctionName(p.getText());
								}
							}
						}
					}
					return tool.getType() != null ? ToolChoiceObj.of(tool) : null;
				}
			}
			catch (Exception e) {
				return null;
			}
			return null;
		}

	}

}
