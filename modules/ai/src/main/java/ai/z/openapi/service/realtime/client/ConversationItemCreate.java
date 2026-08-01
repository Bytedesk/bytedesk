package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import ai.z.openapi.service.realtime.object.ItemObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event for creating a new item in the conversation. This event allows adding
 * messages, function calls, or function responses to the conversation context.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationItemCreate extends RealtimeClientEvent {

	/**
	 * The ID of the item after which the new item should be inserted. If null, the item
	 * will be appended to the end of the conversation.
	 */
	@JsonProperty("previous_item_id")
	private String previousItemId;

	/**
	 * The item to be added to the conversation.
	 */
	@JsonProperty("item")
	private ItemObj item;

	public ConversationItemCreate() {
		super();
		super.setType("conversation.item.create");
		this.previousItemId = "";
		this.item = new ItemObj();
	}

}
