package ai.z.openapi.service.web_search;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.tools.ToolsApi;
import ai.z.openapi.api.web_search.WebSearchApi;
import ai.z.openapi.service.tools.WebSearchApiResponse;
import ai.z.openapi.service.tools.WebSearchParamsRequest;
import ai.z.openapi.service.tools.WebSearchPro;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import okhttp3.ResponseBody;

/**
 * Web search service implementation
 */
public class WebSearchServiceImpl implements WebSearchService {

	private final AbstractAiClient zAiClient;

	private final ToolsApi toolsApi;

	private final WebSearchApi webSearchApi;

	public WebSearchServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.toolsApi = zAiClient.retrofit().create(ToolsApi.class);
		this.webSearchApi = zAiClient.retrofit().create(WebSearchApi.class);
	}

	@Override
	public WebSearchApiResponse createWebSearchProStream(WebSearchParamsRequest request) {
		FlowableRequestSupplier<WebSearchParamsRequest, retrofit2.Call<ResponseBody>> supplier = toolsApi::webSearchStreaming;
		return zAiClient.streamRequest(request, supplier, WebSearchApiResponse.class, WebSearchPro.class);
	}

	@Override
	public WebSearchApiResponse createWebSearchPro(WebSearchParamsRequest request) {
		RequestSupplier<WebSearchParamsRequest, WebSearchPro> supplier = toolsApi::webSearch;
		return this.zAiClient.executeRequest(request, supplier, WebSearchApiResponse.class);
	}

	@Override
	public WebSearchResponse createWebSearch(WebSearchRequest request) {
		RequestSupplier<WebSearchRequest, WebSearchDTO> supplier = webSearchApi::webSearch;
		return this.zAiClient.executeRequest(request, supplier, WebSearchResponse.class);
	}

}
