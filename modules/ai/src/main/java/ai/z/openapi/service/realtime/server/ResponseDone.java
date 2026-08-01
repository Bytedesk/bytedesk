package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.ResponseObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that response generation is complete. This is the final event
 * in the response stream, marking the end of response generation.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseDone extends RealtimeServerEvent {

	/**
	 * The completed response object with final status and content.
	 */
	@JsonProperty("response")
	private ResponseObj response;

	public ResponseDone() {
		super.setType("response.done");
		this.response = new ResponseObj();
	}

}
