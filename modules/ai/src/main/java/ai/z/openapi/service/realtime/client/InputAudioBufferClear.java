package ai.z.openapi.service.realtime.client;

import ai.z.openapi.service.realtime.RealtimeClientEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event for clearing the input audio buffer. This event removes all audio data
 * currently stored in the input buffer.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InputAudioBufferClear extends RealtimeClientEvent {

	public InputAudioBufferClear() {
		super();
		this.setType("input_audio_buffer.clear");
	}

}
