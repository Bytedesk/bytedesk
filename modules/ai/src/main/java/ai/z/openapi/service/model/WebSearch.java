package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration class for web search functionality. This class defines various parameters
 * and settings for web search operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSearch {

	/**
	 * Whether to enable search, defaults to enabled. true: enabled false: disabled
	 */
	private Boolean enable;

	/**
	 * Whether to return search results, defaults to returning results. true: return
	 * results false: do not return results
	 */
	@JsonProperty("search_result")
	private Boolean searchResult;

	/**
	 * Force search for custom keyword content. The model will use the results from custom
	 * search keywords as background knowledge to answer user conversations.
	 */
	@JsonProperty("search_query")
	private String searchQuery;

	/**
	 * Prompt for specifying search engine output results.
	 */
	@JsonProperty("search_prompt")
	private String searchPrompt;

	/**
	 * Whether to perform search intent recognition. By default, search intent recognition
	 * is performed. true: perform search intent recognition and perform search after
	 * there is search intent; false: skip search intent recognition and perform search
	 * directly
	 */
	@JsonProperty("search_intent")
	private String searchIntent;

	/**
	 * Specify the search engine to use.
	 */
	@JsonProperty("search_engine")
	private String searchEngine;

	/**
	 * Whether search results must be obtained before answering.
	 */
	@JsonProperty("require_search")
	private Boolean requireSearch;

	/**
	 * Output position of search results.
	 */
	@JsonProperty("result_sequence")
	private String resultSequence;

	/**
	 * Number of results to return.
	 */
	@JsonProperty("count")
	private Integer count;

	/**
	 * Domain filter to limit search result scope.
	 */
	@JsonProperty("search_domain_filter")
	private String searchDomainFilter;

	/**
	 * Recency filter to limit search result scope.
	 */
	@JsonProperty("search_recency_filter")
	private String searchRecencyFilter;

	/**
	 * Control the word count of web page summaries.
	 */
	@JsonProperty("content_size")
	private String contentSize;

}
