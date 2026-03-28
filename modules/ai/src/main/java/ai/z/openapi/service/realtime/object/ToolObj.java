package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a tool object that can be used by the AI model during conversations. Tools
 * are functions that the model can call to perform specific tasks or retrieve
 * information.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ToolObj {

	/**
	 * The type of the tool (typically "function").
	 */
	@JsonProperty("type")
	private String type;

	/**
	 * The name of the function that can be called.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * A description of what the function does and when to use it.
	 */
	@JsonProperty("description")
	private String description;

	/**
	 * The parameters schema for the function, defining the expected input format.
	 */
	@JsonProperty("parameters")
	private ParameterObj parameters;

	public ToolObj() {
		this.type = "function";
		this.name = "search_engine";
		this.description = "Perform general search based on given query";
		this.parameters = new ParameterObj();
	}

}
