package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InputAudioBufferSpeechStopped extends RealtimeServerEvent {

	@JsonProperty("audio_end_ms")
	private Integer audioEndMs;

	@JsonProperty("item_id")
	private String itemId;

	public InputAudioBufferSpeechStopped() {
		super.setType("input_audio_buffer.speech_stopped");
		this.audioEndMs = 0;
		this.itemId = "";
	}

}
