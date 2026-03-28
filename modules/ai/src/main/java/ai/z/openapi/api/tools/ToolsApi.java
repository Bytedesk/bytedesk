package ai.z.openapi.api.tools;

import ai.z.openapi.service.tools.WebSearchParamsRequest;
import ai.z.openapi.service.tools.WebSearchPro;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * Tools API for enhanced AI capabilities and intelligent agent functions Provides access
 * to external tools and services integrated with GLM-4 All Tools Enables AI models to
 * access real-time information, perform web searches, and execute complex tasks Features
 * intelligent tool selection, automatic parameter optimization, and result synthesis
 * Supports web browsing, code interpretation, image generation, and custom tool
 * integration
 */
public interface ToolsApi {

	/**
	 * Perform intelligent web search with streaming response Executes real-time web
	 * search using GLM-4 enhanced query processing and streams results Features automatic
	 * query optimization, result filtering, and relevance ranking Provides incremental
	 * results for immediate processing and user feedback
	 * @param request Web search parameters including optimized query, result filters,
	 * language preferences, and streaming options
	 * @return Streaming response body with progressive search results, relevance scores,
	 * and metadata
	 */
	@Streaming
	@POST("tools")
	Call<ResponseBody> webSearchStreaming(@Body WebSearchParamsRequest request);

	/**
	 * Perform intelligent web search with comprehensive response Executes advanced web
	 * search using GLM-4 All Tools with intelligent result synthesis Features automatic
	 * query expansion, content summarization, and quality assessment Provides structured
	 * results optimized for AI model consumption and reasoning
	 * @param request Web search parameters including query, result count, content
	 * filters, and processing preferences
	 * @return Complete web search results with URLs, content snippets, relevance scores,
	 * publication dates, and synthesized summaries
	 */
	@POST("tools")
	Single<WebSearchPro> webSearch(@Body WebSearchParamsRequest request);

}
