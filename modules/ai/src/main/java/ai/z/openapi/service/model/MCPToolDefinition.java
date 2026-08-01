package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCPToolDefinition implements Serializable {

	private static final long serialVersionUID = -3960033025319205212L;

	// Tool name
	private String name;

	// Tool description
	private String description;

	private Object annotations;

	// Tool input parameter specification
	private InputSchema input_schema;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InputSchema implements Serializable {

		private static final long serialVersionUID = 4723599134723995986L;

		// Fixed value "object"
		private String type;

		// Parameter properties definition
		private Map<String, Object> properties;

		// List of required properties
		private List<String> required;

		// Whether additional properties are allowed
		private Boolean additionalProperties;

	}

}
