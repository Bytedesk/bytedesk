package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import ai.z.openapi.service.realtime.object.ResponseObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event to trigger the server to create a response through model inference. This
 * event instructs the server to generate a response based on the current conversation
 * context.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseCreate extends RealtimeClientEvent {

	/**
	 * The response configuration object specifying how the response should be generated.
	 */
	@JsonProperty("response")
	private ResponseObj response;

	public ResponseCreate() {
		super();
		this.setType("response.create");
		this.response = new ResponseObj();
	}

}
