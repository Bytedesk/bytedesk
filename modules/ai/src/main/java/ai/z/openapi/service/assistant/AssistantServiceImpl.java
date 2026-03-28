package ai.z.openapi.service.assistant;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.assistant.AssistantApi;
import ai.z.openapi.service.assistant.conversation.AssistantConversationParameters;
import ai.z.openapi.service.assistant.conversation.AssistantConversationUsageListResponse;
import ai.z.openapi.service.assistant.conversation.AssistantConversationUsageListStatus;
import ai.z.openapi.service.assistant.query_support.AssistantSupportResponse;
import ai.z.openapi.service.assistant.query_support.AssistantSupportStatus;
import ai.z.openapi.service.assistant.query_support.AssistantQuerySupportParams;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import okhttp3.ResponseBody;

/**
 * Implementation of AssistantService
 */
public class AssistantServiceImpl implements AssistantService {

	private final AbstractAiClient zAiClient;

	private final AssistantApi assistantApi;

	public AssistantServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.assistantApi = zAiClient.retrofit().create(AssistantApi.class);
	}

	@Override
	public AssistantApiResponse assistantCompletionStream(AssistantParameters request) {
		FlowableRequestSupplier<AssistantParameters, retrofit2.Call<ResponseBody>> supplier = assistantApi::assistantCompletionStream;
		return zAiClient.streamRequest(request, supplier, AssistantApiResponse.class, AssistantCompletion.class);
	}

	@Override
	public AssistantApiResponse assistantCompletion(AssistantParameters request) {
		RequestSupplier<AssistantParameters, AssistantCompletion> supplier = assistantApi::assistantCompletion;
		return zAiClient.executeRequest(request, supplier, AssistantApiResponse.class);
	}

	@Override
	public AssistantSupportResponse querySupport(AssistantQuerySupportParams request) {
		RequestSupplier<AssistantQuerySupportParams, AssistantSupportStatus> supplier = assistantApi::querySupport;
		return zAiClient.executeRequest(request, supplier, AssistantSupportResponse.class);
	}

	@Override
	public AssistantConversationUsageListResponse queryConversationUsage(AssistantConversationParameters request) {
		RequestSupplier<AssistantConversationParameters, AssistantConversationUsageListStatus> supplier = assistantApi::queryConversationUsage;
		return zAiClient.executeRequest(request, supplier, AssistantConversationUsageListResponse.class);
	}

}