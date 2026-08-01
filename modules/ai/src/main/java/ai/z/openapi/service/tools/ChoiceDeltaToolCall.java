package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChoiceDeltaToolCall {

	/**
	 * Index
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Search intent
	 */
	@JsonProperty("search_intent")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<SearchIntent> searchIntent;

	/**
	 * Search result
	 */
	@JsonProperty("search_result")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
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
