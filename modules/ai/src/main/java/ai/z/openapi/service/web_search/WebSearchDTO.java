package ai.z.openapi.service.web_search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WebSearchDTO {

	/**
	 * Creation time
	 */
	@JsonProperty("created")
	private Long created;

	/**
	 * ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Request ID
	 */
	@JsonProperty("request_id")
	private String requestId;

	@JsonProperty("search_result")
	private List<WebSearchResp> webSearchResp;

	@JsonProperty("search_intent")
	private List<SearchIntentResp> searchIntentResp;

}
