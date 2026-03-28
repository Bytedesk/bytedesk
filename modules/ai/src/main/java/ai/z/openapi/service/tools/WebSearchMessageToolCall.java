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
public class WebSearchMessageToolCall {

	/**
	 * Tool call ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Search intent
	 */
	@JsonProperty("search_intent")
	private List<SearchIntent> searchIntent;

	/**
	 * Search results
	 */
	@JsonProperty("search_result")
	private List<SearchResult> searchResult;

	/**
	 * Recommended query
	 */
	@JsonProperty("search_recommend")
	private SearchRecommend searchRecommend;

	/**
	 * Tool call type
	 */
	@JsonProperty("type")
	private String type;

}
