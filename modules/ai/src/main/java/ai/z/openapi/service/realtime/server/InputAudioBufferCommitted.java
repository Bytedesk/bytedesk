package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class InputAudioBufferCommitted extends RealtimeServerEvent {

	@JsonProperty("previous_item_id")
	private String previousItemId;

	@JsonProperty("item_id")
	private String itemId;

	public InputAudioBufferCommitted() {
		super.setType("input_audio_buffer.committed");
		this.previousItemId = "";
		this.itemId = "";
	}

}
