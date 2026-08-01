package ai.z.openapi.service.assistant.message.tools.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block of function tool data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantFunctionToolBlock extends AssistantToolsType {

	/**
	 * The index of the tool call.
	 */
	@JsonProperty("index")
	private Integer index;

	/**
	 * The unique identifier of the tool call.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * The function tool object that contains the name, arguments, and outputs.
	 */
	@JsonProperty("function")
	private AssistantFunctionTool function;

	/**
	 * The type of tool being called, always "function".
	 */
	@JsonProperty("type")
	@Builder.Default
	private String type = "function";

}
