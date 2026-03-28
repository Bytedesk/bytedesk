package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that model-generated audio is complete. This event is sent when
 * the audio generation for a response has finished.
 */
@Getter
@Setter
public class ResponseAudioDone extends RealtimeServerEvent {

	/**
	 * The ID of the response that generated this audio.
	 */
	@JsonProperty("response_id")
	private String responseId;

	/**
	 * The ID of the item that contains this audio content.
	 */
	@JsonProperty("item_id")
	private String itemId;

	/**
	 * The index of the output in the response.
	 */
	@JsonProperty("output_index")
	private Integer outputIndex;

	/**
	 * The index of the content part within the item.
	 */
	@JsonProperty("content_index")
	private Integer contentIndex;

	public ResponseAudioDone() {
		super.setType("response.audio.done");
		this.responseId = "";
		this.itemId = "";
		this.outputIndex = 0;
		this.contentIndex = 0;
	}

}
