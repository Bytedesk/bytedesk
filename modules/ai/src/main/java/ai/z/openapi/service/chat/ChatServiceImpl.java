package ai.z.openapi.service.chat;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.chat.ChatApi;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.service.model.QueryModelResultResponse;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.service.model.ChatRequestWithHeaders;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import okhttp3.ResponseBody;

import java.util.Map;
import java.util.Objects;

/**
 * Chat completion service implementation
 */
public class ChatServiceImpl implements ChatService {

	private final AbstractAiClient zAiClient;

	private final ChatApi chatApi;

	public ChatServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.chatApi = this.zAiClient.retrofit().create(ChatApi.class);
	}

	@Override
	public ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request) {
		validateParams(request);
		if (Objects.nonNull(request.getStream()) && request.getStream()) {
			return streamChatCompletion(request);
		}
		else {
			return syncChatCompletion(request);
		}
	}

	@Override
	public ChatCompletionResponse asyncChatCompletion(ChatCompletionCreateParams request) {
		RequestSupplier<ChatCompletionCreateParams, ModelData> supplier = chatApi::createChatCompletionAsync;
		return this.zAiClient.executeRequest(request, supplier, ChatCompletionResponse.class);
	}

	@Override
	public QueryModelResultResponse retrieveAsyncResult(AsyncResultRetrieveParams request) {
		RequestSupplier<AsyncResultRetrieveParams, ModelData> supplier = (params) -> chatApi
			.queryAsyncResult(params.getTaskId());
		// Handle response
		return this.zAiClient.executeRequest(request, supplier, QueryModelResultResponse.class);
	}

	private ChatCompletionResponse streamChatCompletion(ChatCompletionCreateParams request) {
		FlowableRequestSupplier<ChatCompletionCreateParams, retrofit2.Call<ResponseBody>> supplier = chatApi::createChatCompletionStream;
		return this.zAiClient.streamRequest(request, supplier, ChatCompletionResponse.class, ModelData.class);
	}

	private ChatCompletionResponse syncChatCompletion(ChatCompletionCreateParams request) {
		RequestSupplier<ChatCompletionCreateParams, ModelData> supplier = chatApi::createChatCompletion;
		// Handle response
		return this.zAiClient.executeRequest(request, supplier, ChatCompletionResponse.class);
	}

	@Override
	public ChatCompletionResponse createChatCompletion(ChatCompletionCreateParams request,
			Map<String, String> customHeaders) {
		if (Objects.isNull(customHeaders)) {
			throw new IllegalArgumentException("customHeaders can not be null");
		}
		validateParams(request);
		if (Objects.nonNull(request.getStream()) && request.getStream()) {
			return streamChatCompletionWithHeaders(request, customHeaders);
		}
		else {
			return syncChatCompletionWithHeaders(request, customHeaders);
		}
	}

	private ChatCompletionResponse streamChatCompletionWithHeaders(ChatCompletionCreateParams request,
			Map<String, String> customHeaders) {
		ChatRequestWithHeaders requestWithHeaders = new ChatRequestWithHeaders(request, customHeaders);
		FlowableRequestSupplier<ChatRequestWithHeaders, retrofit2.Call<ResponseBody>> supplier = (wrapper) -> chatApi
			.createChatCompletionStream(wrapper.getRequest(), wrapper.getCustomHeaders());
		return this.zAiClient.streamRequest(requestWithHeaders, supplier, ChatCompletionResponse.class,
				ModelData.class);
	}

	private ChatCompletionResponse syncChatCompletionWithHeaders(ChatCompletionCreateParams request,
			Map<String, String> customHeaders) {
		ChatRequestWithHeaders requestWithHeaders = new ChatRequestWithHeaders(request, customHeaders);
		RequestSupplier<ChatRequestWithHeaders, ModelData> supplier = (wrapper) -> chatApi
			.createChatCompletion(wrapper.getRequest(), wrapper.getCustomHeaders());
		return this.zAiClient.executeRequest(requestWithHeaders, supplier, ChatCompletionResponse.class);
	}

	private void validateParams(ChatCompletionCreateParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getMessages() == null || request.getMessages().isEmpty()) {
			throw new IllegalArgumentException("request messages cannot be null or empty");
		}
		if (request.getModel() == null) {
			throw new IllegalArgumentException("request model cannot be null");
		}
	}

}
