package ai.z.openapi.service.assistant.message.tools.drawing_tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block of drawing tool data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantDrawingToolBlock extends AssistantToolsType {

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
	 * The drawing tool object that contains input and outputs.
	 */
	@JsonProperty("drawing_tool")
	private AssistantDrawingTool drawingTool;

	/**
	 * The type of tool being called, always "drawing_tool".
	 */
	@JsonProperty("type")
	@Builder.Default
	private String type = "drawing_tool";

}
