package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class TurnDetectionObj {

	@JsonProperty("type")
	private String type;

	@JsonProperty("threshold")
	private Double threshold;

	@JsonProperty("prefix_padding_ms")
	private Integer prefixPaddingMs;

	@JsonProperty("silence_duration_ms")
	private Integer silenceDurationMs;

	@JsonProperty("create_response")
	private Boolean createResponse;

	@JsonProperty("interrupt_response")
	private Boolean interruptResponse;

	public TurnDetectionObj() {
		this.type = "server_vad";
		this.threshold = null;
		this.prefixPaddingMs = null;
		this.silenceDurationMs = null;
		this.createResponse = null;
		this.interruptResponse = null;
	}

}
