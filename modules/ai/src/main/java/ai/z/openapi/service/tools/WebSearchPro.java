package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSearchPro {

	/**
	 * Creation time
	 */
	@JsonProperty("created")
	private Long created;

	/**
	 * Choices
	 */
	@JsonProperty("choices")
	private List<WebSearchChoice> choices;

	/**
	 * Request ID
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * ID
	 */
	@JsonProperty("id")
	private String id;

}
