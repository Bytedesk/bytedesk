package ai.z.openapi.service.realtime.server;

import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InputAudioBufferCleared extends RealtimeServerEvent {

	public InputAudioBufferCleared() {
		super.setType("input_audio_buffer.cleared");
	}

}
