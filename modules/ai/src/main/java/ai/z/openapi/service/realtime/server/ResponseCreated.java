package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.ResponseObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that a new response has been created. This is the first event
 * in response generation, with the response in initial "in_progress" state.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseCreated extends RealtimeServerEvent {

	/**
	 * The response object that was created.
	 */
	@JsonProperty("response")
	private ResponseObj response;

	public ResponseCreated() {
		super.setType("response.created");
		this.response = new ResponseObj();
	}

}
