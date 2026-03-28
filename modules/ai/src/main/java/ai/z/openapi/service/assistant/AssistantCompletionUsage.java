package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the usage statistics for a completion.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantCompletionUsage {

	/**
	 * Number of tokens in the input (prompt).
	 */
	@JsonProperty("prompt_tokens")
	private int promptTokens;

	/**
	 * Number of tokens in the output (completion).
	 */
	@JsonProperty("completion_tokens")
	private int completionTokens;

	/**
	 * Total number of tokens used.
	 */
	@JsonProperty("total_tokens")
	private int totalTokens;

}
