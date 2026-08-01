package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.RateLimitObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Server event indicating that rate limits have been updated. This event is sent at the
 * start of a response to inform the client of current rate limit status.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RateLimitsUpdated extends RealtimeServerEvent {

	/**
	 * List of rate limit objects containing current usage and limit information.
	 */
	@JsonProperty("rate_limits")
	private List<RateLimitObj> rateLimits;

	public RateLimitsUpdated() {
		super.setType("rate_limits.updated");
		this.rateLimits = new ArrayList<>();
	}

}
