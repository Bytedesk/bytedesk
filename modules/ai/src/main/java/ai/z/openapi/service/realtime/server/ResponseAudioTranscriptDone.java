package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseAudioTranscriptDone extends RealtimeServerEvent {

	@JsonProperty("response_id")
	private String responseId;

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("output_index")
	private Integer outputIndex;

	@JsonProperty("content_index")
	private Integer contentIndex;

	@JsonProperty("transcript")
	private String transcript;

	public ResponseAudioTranscriptDone() {
		super.setType("response.audio_transcript.done");
		this.responseId = "";
		this.itemId = "";
		this.outputIndex = 0;
		this.contentIndex = 0;
		this.transcript = "";
	}

}
