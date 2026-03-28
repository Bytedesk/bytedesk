package ai.z.openapi.service.assistant.message.tools.retrieval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents the outputs of a retrieval tool.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssistantRetrievalTool {

	/**
	 * A list of text snippets and their respective document names retrieved from the
	 * knowledge base.
	 */
	@JsonProperty("outputs")
	private List<AssistantRetrievalToolOutput> outputs;

}
