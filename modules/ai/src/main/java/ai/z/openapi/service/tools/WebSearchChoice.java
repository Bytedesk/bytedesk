package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSearchChoice {

	/**
	 * Index
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * Finish reason
	 */
	@JsonProperty("finish_reason")
	private String finishReason;

	/**
	 * Message
	 */
	@JsonProperty("message")
	private WebSearchMessage message;

	/**
	 * delta
	 */
	@JsonProperty("delta")
	private ChoiceDelta delta;

}
