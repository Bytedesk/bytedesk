package ai.z.openapi.service.web_search;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebSearchRequest implements ClientRequest<WebSearchRequest> {

	/**
	 * Search engine
	 */
	@JsonProperty("search_engine")
	private String searchEngine;

	/**
	 * Search query text
	 */
	@JsonProperty("search_query")
	private String searchQuery;

	/**
	 * Passed by client, must ensure uniqueness; used to distinguish unique identifier for
	 * each request, platform will generate by default if not provided by client.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * User ID
	 */
	@JsonProperty("user_id")
	private String userId;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

	/**
	 * Number of returned results
	 */
	@JsonProperty("count")
	private Integer count;

	/**
	 * Limit the scope of search results
	 */
	@JsonProperty("search_domain_filter")
	private String searchDomainFilter;

	/**
	 * Limit the scope of search results
	 */
	@JsonProperty("search_recency_filter")
	private String searchRecencyFilter;

	/**
	 * Control the word count of web page summary
	 */
	@JsonProperty("content_size")
	private String contentSize;

	/**
	 * Whether to include image in search results.
	 */
	@JsonProperty("include_image")
	private Boolean includeImage;

}
