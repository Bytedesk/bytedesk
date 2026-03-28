package ai.z.openapi.api.web_search;

import ai.z.openapi.service.web_search.WebSearchDTO;
import ai.z.openapi.service.web_search.WebSearchRequest;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Web Search API for real-time internet information retrieval Integrated with GLM-4
 * models to provide comprehensive web search capabilities Enables AI models to access
 * current information, news, and real-time data from the internet Supports intelligent
 * search result filtering, ranking, and content summarization Features automatic query
 * optimization and multi-source information aggregation
 */
public interface WebSearchApi {

	/**
	 * Perform intelligent web search operation with GLM-4 integration Searches the
	 * internet for relevant, up-to-date information using advanced query processing
	 * Features automatic query expansion, result filtering, and content quality
	 * assessment Supports real-time information retrieval for news, facts, and current
	 * events
	 * @param request Web search request containing search query, result count, language
	 * preference, and filtering options
	 * @return Comprehensive web search results with URLs, titles, content snippets,
	 * publication dates, and relevance scores
	 */
	@POST("web_search")
	Single<WebSearchDTO> webSearch(@Body WebSearchRequest request);

}
