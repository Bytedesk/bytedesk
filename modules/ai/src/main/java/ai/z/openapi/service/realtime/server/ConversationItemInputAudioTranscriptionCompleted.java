package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ConversationItemInputAudioTranscriptionCompleted extends RealtimeServerEvent {

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("content_index")
	private int contentIndex;

	@JsonProperty("transcript")
	private String transcript;

	public ConversationItemInputAudioTranscriptionCompleted() {
		super.setType("conversation.item.input_audio_transcription.completed");
		this.itemId = "";
		this.contentIndex = 0;
		this.transcript = "";
	}

}
