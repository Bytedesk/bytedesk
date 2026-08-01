package ai.z.openapi.service.web_search;

import ai.z.openapi.service.tools.WebSearchApiResponse;
import ai.z.openapi.service.tools.WebSearchParamsRequest;

/**
 * Web search service interface
 */
public interface WebSearchService {

	/**
	 * Creates a streaming web search with enhanced capabilities.
	 * @param request the web search pro request
	 * @return WebSearchApiResponse containing the streaming search results
	 */
	WebSearchApiResponse createWebSearchProStream(WebSearchParamsRequest request);

	/**
	 * Creates a web search with enhanced capabilities.
	 * @param request the web search pro request
	 * @return WebSearchApiResponse containing the search results
	 */
	WebSearchApiResponse createWebSearchPro(WebSearchParamsRequest request);

	/**
	 * Creates a basic web search.
	 * @param request the web search request
	 * @return WebSearchResponse containing the search results
	 */
	WebSearchResponse createWebSearch(WebSearchRequest request);

}