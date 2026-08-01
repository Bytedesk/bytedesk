package ai.z.openapi.api.assistant;

import ai.z.openapi.service.assistant.AssistantCompletion;
import ai.z.openapi.service.assistant.AssistantParameters;
import ai.z.openapi.service.assistant.conversation.AssistantConversationUsageListStatus;
import ai.z.openapi.service.assistant.query_support.AssistantSupportStatus;
import ai.z.openapi.service.assistant.conversation.AssistantConversationParameters;
import ai.z.openapi.service.assistant.query_support.AssistantQuerySupportParams;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * Assistant API for intelligent conversational AI Provides advanced assistant
 * capabilities with streaming and synchronous responses Supports conversation management
 * and usage tracking for AI assistants
 */
public interface AssistantApi {

	/**
	 * Generate assistant response with streaming output Creates real-time conversational
	 * responses with streaming delivery
	 * @param request Assistant parameters including messages, model settings, and context
	 * @return Streaming response body with assistant's reply
	 */
	@Streaming
	@POST("assistant")
	Call<ResponseBody> assistantCompletionStream(@Body AssistantParameters request);

	/**
	 * Generate assistant response with complete output Creates conversational responses
	 * and returns the complete assistant reply
	 * @param request Assistant parameters including conversation context and settings
	 * @return Complete assistant response with message content and metadata
	 */
	@POST("assistant")
	Single<AssistantCompletion> assistantCompletion(@Body AssistantParameters request);

	/**
	 * Query assistant support capabilities Retrieves information about available
	 * assistant features and supported operations
	 * @param request Query parameters for support information
	 * @return Assistant support status and available capabilities
	 */
	@POST("assistant/list")
	Single<AssistantSupportStatus> querySupport(@Body AssistantQuerySupportParams request);

	/**
	 * Query conversation usage statistics Retrieves usage metrics and conversation
	 * history for assistant interactions
	 * @param request Conversation query parameters including filters and pagination
	 * @return Conversation usage statistics and history information
	 */
	@POST("assistant/conversation/list")
	Single<AssistantConversationUsageListStatus> queryConversationUsage(@Body AssistantConversationParameters request);

}
