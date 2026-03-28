package ai.z.openapi.service.model;

import ai.z.openapi.service.agents.AgentMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Choice {

	@JsonProperty("finish_reason")
	private String finishReason;

	@JsonProperty("index")
	private Long index;

	/**
	 * Reasoning content, supports by GLM-4.5 series.
	 */
	@JsonProperty("message")
	private ChatMessage message;

	/**
	 * for agent message
	 */
	@JsonProperty("messages")
	private List<AgentMessage> messages;

	@JsonProperty("delta")
	private Delta delta;

}
