package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationItemDeleted extends RealtimeServerEvent {

	@JsonProperty("item_id")
	private String itemId;

	public ConversationItemDeleted() {
		super.setType("conversation.item.deleted");
		this.itemId = "";
	}

}
