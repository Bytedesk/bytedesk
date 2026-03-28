package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the parameter schema for a function tool. Defines the structure and
 * validation rules for function parameters using JSON Schema format.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ParameterObj {

	/**
	 * The type of the parameter schema (typically "object").
	 */
	@JsonProperty("type")
	private String type;

	/**
	 * Map of property names to their schema definitions.
	 */
	@JsonProperty("properties")
	private Map<String, PropertyItem> properties;

	/**
	 * List of required property names.
	 */
	@JsonProperty("required")
	private List<String> required;

	public ParameterObj() {
		this.type = "object";
		this.properties = new HashMap<>();
		this.required = new ArrayList<>();
	}

	/**
	 * Represents a single property definition in the parameter schema.
	 */
	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class PropertyItem {

		/**
		 * The data type of this property (e.g., "string", "number", "boolean").
		 */
		@JsonProperty("type")
		private String type;

		/**
		 * Description of what this property represents and how it should be used.
		 */
		@JsonProperty("description")
		private String description;

		public PropertyItem() {
			this.type = "string";
			this.description = "";
		}

	}

}
