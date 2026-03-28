package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.SessionObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that the session configuration has been updated. This event is
 * sent when the client successfully updates session settings.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SessionUpdated extends RealtimeServerEvent {

	/**
	 * The updated session configuration object.
	 */
	@JsonProperty("session")
	private SessionObj session;

	public SessionUpdated() {
		super.setType("session.updated");
		this.session = new SessionObj();
	}

}
