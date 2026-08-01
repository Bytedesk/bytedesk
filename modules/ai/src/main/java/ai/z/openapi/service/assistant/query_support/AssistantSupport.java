package ai.z.openapi.service.assistant.query_support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * This class represents the details of an assistant.
 */
@Data
public class AssistantSupport {

	/**
	 * The Assistant ID, used for assistant conversations.
	 */
	@JsonProperty("assistant_id")
	private String assistantId;

	/**
	 * The creation time of the assistant.
	 */
	@JsonProperty("created_at")
	private String createdAt;

	/**
	 * The last update time of the assistant.
	 */
	@JsonProperty("updated_at")
	private String updatedAt;

	/**
	 * The name of the assistant.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * The avatar of the assistant.
	 */
	@JsonProperty("avatar")
	private String avatar;

	/**
	 * The description of the assistant.
	 */
	@JsonProperty("description")
	private String description;

	/**
	 * The status of the assistant, currently only "publish".
	 */
	@JsonProperty("status")
	private String status;

	/**
	 * The list of tools supported by the assistant.
	 */
	@JsonProperty("tools")
	private List<String> tools;

	/**
	 * The list of recommended prompts to start the assistant.
	 */
	@JsonProperty("starter_prompts")
	private List<String> starterPrompts;

}