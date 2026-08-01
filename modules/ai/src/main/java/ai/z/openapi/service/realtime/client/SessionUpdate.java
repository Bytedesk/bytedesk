package ai.z.openapi.service.realtime.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeClientEvent;
import ai.z.openapi.service.realtime.object.SessionObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Client event for updating session configuration. This event allows modifying session
 * settings such as instructions, tools, voice, and other parameters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SessionUpdate extends RealtimeClientEvent {

	/**
	 * The session configuration object containing the updated settings.
	 */
	@JsonProperty("session")
	private SessionObj session;

	public SessionUpdate() {
		super();
		this.setType("session.update");
		this.session = new SessionObj();
	}

}
