package ai.z.openapi.service.assistant.message.tools.code_interpreter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents a code interpreter that executes code and returns the results.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantCodeInterpreter {

	/**
	 * The generated code snippet that is input to the code sandbox.
	 */
	@JsonProperty("input")
	private String input;

	/**
	 * The output results after the code execution.
	 */
	@JsonProperty("outputs")
	private List<AssistantCodeInterpreterToolOutput> outputs;

}
