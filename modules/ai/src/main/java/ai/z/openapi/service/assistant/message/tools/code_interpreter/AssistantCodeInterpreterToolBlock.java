package ai.z.openapi.service.assistant.message.tools.code_interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block of code tool data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantCodeInterpreterToolBlock extends AssistantToolsType {

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
	 * The code interpreter object.
	 */
	@JsonProperty("code_interpreter")
	private AssistantCodeInterpreter codeInterpreter;

	/**
	 * The type of tool being called, always "code_interpreter".
	 */
	@JsonProperty("type")
	@Builder.Default
	private String type = "code_interpreter";

}
