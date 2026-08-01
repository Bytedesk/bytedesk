package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseFunctionCallArgumentsDelta extends RealtimeServerEvent {

	@JsonProperty("response_id")
	private String responseId;

	@JsonProperty("item_id")
	private String itemId;

	@JsonProperty("output_index")
	private int outputIndex;

	@JsonProperty("call_id")
	private String callId;

	@JsonProperty("delta")
	private String delta;

	public ResponseFunctionCallArgumentsDelta() {
		super.setType("response.function_call_arguments.delta");
		this.responseId = "";
		this.itemId = "";
		this.outputIndex = 0;
		this.callId = "";
		this.delta = "";
	}

}
