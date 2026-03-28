package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseTextDone extends RealtimeServerEvent {

	@JsonProperty("response_id")
	private String responseId;

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("output_index")
	private Integer outputIndex;

	@JsonProperty("content_index")
	private Integer contentIndex;

	@JsonProperty("text")
	private String text;

	public ResponseTextDone() {
		super.setType("response.text.done");
		this.responseId = "";
		this.itemId = "";
		this.outputIndex = 0;
		this.contentIndex = 0;
		this.text = "";
	}

}
