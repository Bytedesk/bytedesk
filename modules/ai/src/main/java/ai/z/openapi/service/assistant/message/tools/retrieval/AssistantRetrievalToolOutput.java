package ai.z.openapi.service.assistant.message.tools.retrieval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the output of a retrieval tool.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssistantRetrievalToolOutput {

	/**
	 * The text snippet retrieved from the knowledge base.
	 */
	@JsonProperty("text")
	private String text;

	/**
	 * The name of the document from which the text snippet was retrieved, returned only
	 * in intelligent configuration.
	 */
	@JsonProperty("document")
	private String document;

}
