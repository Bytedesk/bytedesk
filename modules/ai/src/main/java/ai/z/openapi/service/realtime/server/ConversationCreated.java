package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.ConversationObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that a new conversation has been created. This event is sent
 * immediately after session creation to establish the conversation context.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationCreated extends RealtimeServerEvent {

	/**
	 * The conversation object containing the conversation details and configuration.
	 */
	@JsonProperty("conversation")
	private ConversationObj conversation;

	public ConversationCreated() {
		super.setType("conversation.created");
	}

}
