package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a web search message containing role and tool call information. This class
 * is used for web search operations and tool interactions.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSearchMessage {

	/**
	 * Role in the conversation.
	 */
	@JsonProperty("role")
	private String role;

	/**
	 * List of tool calls.
	 */
	@JsonProperty("tool_calls")
	private List<WebSearchMessageToolCall> toolCalls;

}
