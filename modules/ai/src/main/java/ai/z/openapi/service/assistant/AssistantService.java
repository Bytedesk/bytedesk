package ai.z.openapi.service.assistant;

import ai.z.openapi.service.assistant.conversation.AssistantConversationParameters;
import ai.z.openapi.service.assistant.conversation.AssistantConversationUsageListResponse;
import ai.z.openapi.service.assistant.query_support.AssistantSupportResponse;
import ai.z.openapi.service.assistant.query_support.AssistantQuerySupportParams;

/**
 * Assistant service interface
 */
public interface AssistantService {

	/**
	 * Creates a streaming assistant completion.
	 * @param request the assistant completion request
	 * @return AssistantApiResponse containing the completion result
	 */
	AssistantApiResponse assistantCompletionStream(AssistantParameters request);

	/**
	 * Creates a non-streaming assistant completion.
	 * @param request the assistant completion request
	 * @return AssistantApiResponse containing the completion result
	 */
	AssistantApiResponse assistantCompletion(AssistantParameters request);

	/**
	 * Queries assistant support status.
	 * @param request the query support request
	 * @return AssistantSupportResponse containing the support information
	 */
	AssistantSupportResponse querySupport(AssistantQuerySupportParams request);

	/**
	 * Queries conversation usage information.
	 * @param request the conversation parameters
	 * @return ConversationUsageListResponse containing the usage information
	 */
	AssistantConversationUsageListResponse queryConversationUsage(AssistantConversationParameters request);

}