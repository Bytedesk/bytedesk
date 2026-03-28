package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event for appending audio data to the input audio buffer. This event allows
 * streaming audio data to the server for processing.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InputAudioBufferAppend extends RealtimeClientEvent {

	/**
	 * Base64-encoded audio data to append to the input buffer.
	 */
	@JsonProperty("audio")
	private String audio;

	public InputAudioBufferAppend() {
		super();
		this.setType("input_audio_buffer.append");
		this.audio = "";
	}

}
