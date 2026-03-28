package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event for deleting an item from the conversation history. This event removes a
 * specific conversation item by its ID.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationItemDelete extends RealtimeClientEvent {

	/**
	 * The ID of the conversation item to delete.
	 */
	@JsonProperty("item_id")
	private String itemId;

	public ConversationItemDelete() {
		super();
		super.setType("conversation.item.delete");
		this.itemId = "";
	}

}
