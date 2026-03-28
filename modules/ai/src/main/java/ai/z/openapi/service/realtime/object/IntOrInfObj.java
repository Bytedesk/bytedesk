package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
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
 * Represents a value that can be either an integer or "inf" (infinity). Used for
 * configuration values that can have unlimited or specific numeric limits.
 */
@Getter
@Setter
@JsonSerialize(using = IntOrInfObj.ConfigIntOrInfSerializer.class)
@JsonDeserialize(using = IntOrInfObj.ConfigIntOrInfDeserializer.class)
public class IntOrInfObj {

	/**
	 * The integer value when not infinite.
	 */
	private final Integer value;

	/**
	 * Flag indicating whether this represents infinity.
	 */
	private final Boolean isInf;

	private IntOrInfObj(Integer value, Boolean isInf) {
		this.value = value;
		this.isInf = isInf;
	}

	/**
	 * Creates an IntOrInfObj representing infinity.
	 * @return IntOrInfObj instance representing infinity
	 */
	public static IntOrInfObj inf() {
		return new IntOrInfObj(null, true);
	}

	/**
	 * Creates an IntOrInfObj with a specific integer value. Values outside the range [1,
	 * 262144] will be treated as infinity.
	 * @param value The integer value (1 to 262,144)
	 * @return IntOrInfObj instance with the specified value or infinity if out of range
	 */
	public static IntOrInfObj of(int value) throws IllegalArgumentException {
		// 256K = 256 * 1024 = 262,144
		if (value < 1 || value > 262_144) {
			return new IntOrInfObj(null, true);
		}
		return new IntOrInfObj(value, false);
	}

	@Override
	public String toString() {
		return isInf ? "inf" : value.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		IntOrInfObj that = (IntOrInfObj) o;
		return isInf == that.isInf && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, isInf);
	}

	public static class ConfigIntOrInfSerializer extends JsonSerializer<IntOrInfObj> {

		@Override
		public void serialize(IntOrInfObj value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			if (value.isInf != null && value.getIsInf()) {
				gen.writeString("inf");
			}
			else {
				gen.writeNumber(value.getValue());
			}
		}

	}

	public static class ConfigIntOrInfDeserializer extends JsonDeserializer<IntOrInfObj> {

		@Override
		public IntOrInfObj deserialize(JsonParser p, DeserializationContext context) throws IOException {
			String text = p.getText();
			if (text == null) {
				return null;
			}
			if ("inf".equalsIgnoreCase(text)) {
				return IntOrInfObj.inf();
			}
			else {
				try {
					int intValue = Integer.parseInt(text);
					return IntOrInfObj.of(intValue);
				}
				catch (NumberFormatException e) {
					return IntOrInfObj.inf();
				}
			}
		}

	}

}
