package ai.z.openapi.service.assistant.message.tools.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the output of a function tool, containing the generated content.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantFunctionToolOutput {

	/**
	 * The generated content as a string.
	 */
	@JsonProperty("content")
	private String content;

}
