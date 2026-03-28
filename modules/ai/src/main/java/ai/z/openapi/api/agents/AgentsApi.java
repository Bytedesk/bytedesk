package ai.z.openapi.api.agents;

import ai.z.openapi.service.agents.AgentAsyncResultRetrieveParams;
import ai.z.openapi.service.agents.AgentsCompletionRequest;
import ai.z.openapi.service.model.ModelData;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * Agents API for intelligent agent capabilities based on GLM-4 All Tools Provides AI
 * agents with automatic tool calling, function execution, and complex task automation
 * Features include web browsing, code interpreter, image generation, and multi-tool
 * coordination Supports streaming and synchronous agent interactions with real-time task
 * planning
 */
public interface AgentsApi {

	/**
	 * Create a streaming agent completion with GLM-4 All Tools Returns agent responses in
	 * real-time through Server-Sent Events (SSE) Automatically selects and calls
	 * appropriate tools (web search, code execution, image generation)
	 * @param request Agent completion parameters including tools, functions, and
	 * execution context
	 * @return Streaming response body with incremental agent outputs and tool execution
	 * results
	 */
	@Streaming
	@POST("v1/agents")
	Call<ResponseBody> agentsCompletionStream(@Body AgentsCompletionRequest request);

	/**
	 * Create a synchronous agent completion with GLM-4 All Tools Waits for the agent to
	 * complete complex task execution and returns the final result Supports automatic
	 * tool selection including CogView3 image generation, Python code interpreter, and
	 * web browsing
	 * @param request Agent completion parameters including tools, functions, and
	 * execution context
	 * @return Complete agent execution response with results and comprehensive tool
	 * outputs
	 */
	@POST("v1/agents")
	Single<ModelData> agentsCompletionSync(@Body AgentsCompletionRequest request);

	/**
	 * Query the result of an asynchronous agent execution Retrieves the agent execution
	 * result using the task parameters for long-running tasks Useful for complex
	 * multi-tool operations that require extended processing time
	 * @param request Parameters for retrieving asynchronous agent execution results
	 * @return Agent execution result with tool outputs, status information, and task
	 * completion details
	 */
	@POST("v1/agents/async-result")
	Single<ModelData> queryAgentsAsyncResult(@Body AgentAsyncResultRetrieveParams request);

}
