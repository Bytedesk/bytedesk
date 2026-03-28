package ai.z.openapi.service.assistant.message.tools.drawing_tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the output of a drawing tool, containing the generated image.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantDrawingToolOutput {

	/**
	 * The generated image in a string format.
	 */
	@JsonProperty("image")
	private String image;

}
