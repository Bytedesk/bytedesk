package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationItemTruncate extends RealtimeClientEvent {

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("content_index")
	private Integer contentIndex;

	@JsonProperty("audio_end_ms")
	private Integer audioEndMs;

	public ConversationItemTruncate() {
		super();
		super.setType("conversation.item.truncate");
		this.itemId = "";
		this.contentIndex = 0;
		this.audioEndMs = 0;
	}

}
