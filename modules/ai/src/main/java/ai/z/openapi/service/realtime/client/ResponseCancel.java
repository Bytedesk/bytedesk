package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event for canceling an ongoing response generation. This event stops the server
 * from continuing to generate a response.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseCancel extends RealtimeClientEvent {

	/**
	 * The ID of the response to cancel.
	 */
	@JsonProperty("response_id")
	private String responseId;

	public ResponseCancel() {
		super();
		this.setType("response.cancel");
		this.responseId = "";
	}

}
