package ai.z.openapi.service.assistant.message.tools.retrieval;

import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantRetrievalToolBlock extends AssistantToolsType {

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
	 * An instance of the RetrievalTool class containing the retrieval outputs.
	 */
	@JsonProperty("retrieval")
	private AssistantRetrievalTool retrieval;

	/**
	 * The type of tool being used, always set to "retrieval".
	 */
	@JsonProperty("type")
	@Builder.Default
	private String type = "retrieval";

}
