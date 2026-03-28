package ai.z.openapi.service.agents;

import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ModelData;
import io.reactivex.rxjava3.core.Single;

/**
 * Agent completion service interface
 */
public interface AgentService {

	/**
	 * Creates an agent completion, either streaming or non-streaming based on the request
	 * configuration.
	 * @param request the agents completion request
	 * @return ChatCompletionResponse containing the agent completion result
	 */
	ChatCompletionResponse createAgentCompletion(AgentsCompletionRequest request);

	/**
	 * Retrieves the result of an asynchronous agent operation.
	 * @param request the query request for the async agent result
	 * @return Single<ModelData> containing the async agent operation result
	 */
	Single<ModelData> retrieveAgentAsyncResult(AgentAsyncResultRetrieveParams request);

}