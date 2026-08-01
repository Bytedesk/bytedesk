package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.OutputItemObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseOutputItemDone extends RealtimeServerEvent {

	@JsonProperty("response_id")
	private String responseId;

	@JsonProperty("output_index")
	private String outputIndex;

	@JsonProperty("item")
	private OutputItemObj item;

	public ResponseOutputItemDone() {
		super.setType("response.output_item.done");
		this.responseId = "";
		this.outputIndex = "";
		this.item = new OutputItemObj();
	}

}
