package ai.z.openapi.service.assistant.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.deserialize.JsonTypeField;

/**
 * This class represents a block of text content in a conversation.
 */
@JsonTypeField("content")
public class AssistantTextContentBlock extends AssistantMessageContent {

	/**
	 * The content of the text block.
	 */
	@JsonProperty("content")
	private String content;

	/**
	 * The role of the speaker, default is "assistant".
	 */
	@JsonProperty("role")
	private String role = "assistant";

	/**
	 * Default constructor.
	 */
	public AssistantTextContentBlock() {
	}

	// Getters and Setters

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
