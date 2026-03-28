package ai.z.openapi.service.assistant.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import ai.z.openapi.service.deserialize.JsonTypeField;

import java.util.List;

/**
 * This class represents a block of tool call data in a conversation.
 */
@JsonTypeField("tool_calls")
public class AssistantToolsDeltaBlock extends AssistantMessageContent {

	/**
	 * A list of tool call types.
	 */
	@JsonProperty("tool_calls")
	private List<AssistantToolsType> toolCalls;

	/**
	 * The role of the speaker, default is "tool".
	 */
	@JsonProperty("role")
	private String role = "tool";

	/**
	 * Default constructor.
	 */
	public AssistantToolsDeltaBlock() {
	}

	// Getters and Setters

	public List<AssistantToolsType> getToolCalls() {
		return toolCalls;
	}

	public void setToolCalls(List<AssistantToolsType> toolCalls) {
		this.toolCalls = toolCalls;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
