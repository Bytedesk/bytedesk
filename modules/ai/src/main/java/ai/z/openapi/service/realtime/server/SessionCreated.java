package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.SessionObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that a new session has been created. This is the first event
 * sent by the server when establishing a new realtime connection.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SessionCreated extends RealtimeServerEvent {

	/**
	 * The session object containing the initial session configuration.
	 */
	@JsonProperty("session")
	private SessionObj session;

	public SessionCreated() {
		super();
		this.setType("session.created");
		this.session = new SessionObj();
	}

}
