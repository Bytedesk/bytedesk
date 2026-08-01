package ai.z.openapi.service.realtime.client;

import ai.z.openapi.service.realtime.RealtimeClientEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InputAudioBufferCommit extends RealtimeClientEvent {

	public InputAudioBufferCommit() {
		super();
		this.setType("input_audio_buffer.commit");
	}

}
