package ai.z.openapi.service.realtime.server;

import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RealtimeHeartBeat extends RealtimeServerEvent {

	public RealtimeHeartBeat() {
		super.setType("heartbeat");
	}

}
