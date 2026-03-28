package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationItemTruncated extends RealtimeServerEvent {

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("content_index")
	private Integer contentIndex;

	@JsonProperty("audio_end_ms")
	private Integer audioEndMs;

	public ConversationItemTruncated() {
		super.setType("conversation.item.truncated");
		this.itemId = "";
		this.contentIndex = 0;
		this.audioEndMs = 0;
	}

}
