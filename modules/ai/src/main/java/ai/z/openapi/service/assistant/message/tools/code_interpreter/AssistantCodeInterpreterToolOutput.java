package ai.z.openapi.service.assistant.message.tools.code_interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the output result of a code tool.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantCodeInterpreterToolOutput {

	/**
	 * The type of output, currently only "logs".
	 */
	@JsonProperty("type")
	private String type;

	/**
	 * The log results from the code execution.
	 */
	@JsonProperty("logs")
	private String logs;

	/**
	 * Error message if any occurred during code execution.
	 */
	@JsonProperty("error_msg")
	private String errorMsg;

}
