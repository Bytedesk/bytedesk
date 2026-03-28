package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class RateLimitObj {

	@JsonProperty("name")
	private String name;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("remaining")
	private Integer remaining;

	@JsonProperty("reset_seconds")
	private Long resetSeconds;

	public RateLimitObj() {
		this.limit = 10;
		this.name = "requests";
		this.remaining = 10;
		this.resetSeconds = 60L;
	}

}
