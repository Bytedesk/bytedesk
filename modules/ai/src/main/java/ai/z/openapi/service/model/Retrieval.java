package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Retrieval {

	/**
	 * When involving knowledge base ID, please go to the knowledge base module of the
	 * open platform to create or obtain it.
	 */
	@JsonProperty("knowledge_id")
	private String knowledgeId;

	/**
	 * Knowledge base template when requesting the model, default template: Find the
	 * answer to the question """ {{question}} """ from the document """ {{ knowledge}}
	 * """ If an answer is found, use only document statements to answer the question. If
	 * no answer is found, use your own knowledge to answer and tell the user that the
	 * information is not from the document. Don't repeat the question, start answering
	 * directly
	 *
	 * Note: When users customize templates, the knowledge base content placeholder and
	 * user-side question placeholder must be {{ knowledge}} and {{question}}, other
	 * template content can be defined by users according to actual scenarios
	 */
	@JsonProperty("prompt_template")
	private String promptTemplate;

}
