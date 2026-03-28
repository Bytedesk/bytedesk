package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the completion data returned by an assistant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistantCompletion {

	/**
	 * Request ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Conversation ID
	 */
	@JsonProperty("conversation_id")
	private String conversationId;

	/**
	 * Assistant ID
	 */
	@JsonProperty("assistant_id")
	private String assistantId;

	/**
	 * Request creation time, Unix timestamp
	 */
	@JsonProperty("created")
	private Long created;

	/**
	 * Return status, including: `completed` indicates generation finished, `in_progress`
	 * indicates generating, `failed` indicates generation exception
	 */
	@JsonProperty("status")
	private String status;

	/**
	 * Error information
	 */
	@JsonProperty("last_error")
	private AssistantErrorInfo lastError;

	/**
	 * Incremental return information
	 */
	@JsonProperty("choices")
	private List<AssistantChoice> choices;

	/**
	 * Metadata, extension field
	 */
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	/**
	 * Token count statistics
	 */
	@JsonProperty("usage")
	private AssistantCompletionUsage usage;

}
