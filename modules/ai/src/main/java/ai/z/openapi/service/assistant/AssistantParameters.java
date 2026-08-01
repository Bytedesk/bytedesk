package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * This class represents the parameters for an assistant, including optional fields.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantParameters extends CommonRequest implements ClientRequest<AssistantParameters> {

	/**
	 * The ID of the assistant.
	 */
	@JsonProperty("assistant_id")
	private String assistantId;

	/**
	 * The conversation ID. If not provided, a new conversation is created.
	 */
	@JsonProperty("conversation_id")
	private String conversationId;

	/**
	 * The name of the model, default is 'GLM-4-Assistant'.
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * Whether to support streaming SSE, should be set to True.
	 */
	@JsonProperty("stream")
	private boolean stream;

	/**
	 * The list of conversation messages.
	 */
	@JsonProperty("messages")
	private List<AssistantConversationMessage> messages;

	/**
	 * The list of file attachments for the conversation, optional.
	 */
	@JsonProperty("attachments")
	private List<AssistantAttachments> attachments;

	/**
	 * Metadata or additional fields, optional.
	 */
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	@JsonProperty("extra_parameters")
	private AssistantExtraParameters extraParameters;

}
