package ai.z.openapi.api.chat;

import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ModelData;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

import java.util.Map;

/**
 * Chat Completions API for advanced GLM series models Provides synchronous, asynchronous,
 * and streaming chat completion capabilities Supports complex reasoning, long context
 * processing and ultra-fast inference Features
 */
public interface ChatApi {

	/**
	 * Create a streaming chat completion with real-time response Returns response content
	 * incrementally through Server-Sent Events (SSE) for immediate user feedback
	 * Optimized for interactive applications requiring low latency and progressive
	 * content delivery Supports all GLM models with configurable streaming parameters and
	 * token-by-token generation
	 * @param request Chat completion parameters including model selection messages,
	 * temperature, top_p, max_tokens, and streaming settings
	 * @return Streaming response body with incremental content, usage statistics, and
	 * completion indicators
	 */
	@Streaming
	@POST("chat/completions")
	Call<ResponseBody> createChatCompletionStream(@Body ChatCompletionCreateParams request);

	/**
	 * Create a streaming chat completion with custom headers support Returns response
	 * content incrementally through Server-Sent Events (SSE) for immediate user feedback
	 * Optimized for interactive applications requiring low latency and progressive
	 * content delivery Supports all GLM models with configurable streaming parameters and
	 * custom HTTP headers
	 * @param request Chat completion parameters including model selection, messages, and
	 * streaming settings
	 * @param headers Custom HTTP headers to be added to the request
	 * @return Streaming response body with incremental content, usage statistics, and
	 * completion indicators
	 */
	@Streaming
	@POST("chat/completions")
	Call<ResponseBody> createChatCompletionStream(@Body ChatCompletionCreateParams request,
			@HeaderMap Map<String, String> headers);

	/**
	 * Create an asynchronous chat completion for long-running tasks Submits the request
	 * and returns immediately with a task ID for later result retrieval Ideal for complex
	 * reasoning tasks, long document processing, or batch operations Supports advanced
	 * GLM-4 models with extended context and computational requirements
	 * @param request Chat completion parameters including model selection, messages,
	 * advanced reasoning settings, tools configuration, and processing options
	 * @return Task information with unique ID, estimated completion time, and processing
	 * status for asynchronous tracking
	 */
	@POST("async/chat/completions")
	Single<ModelData> createChatCompletionAsync(@Body ChatCompletionCreateParams request);

	/**
	 * Create a synchronous chat completion with immediate response Waits for the GLM
	 * model to complete execution and returns the final result Supports complex
	 * reasoning, tool calling, function execution, and multi-modal understanding Features
	 * advanced capabilities like web search integration, code interpretation, and image
	 * analysis
	 * @param request Chat completion parameters including model selection, conversation
	 * messages, generation settings (temperature, top_p, max_tokens), tools
	 * configuration, and response format
	 * @return Complete chat completion response with generated content, usage statistics,
	 * tool call results, and reasoning traces
	 */
	@POST("chat/completions")
	Single<ModelData> createChatCompletion(@Body ChatCompletionCreateParams request);

	/**
	 * Create a synchronous chat completion with custom headers support Waits for the GLM
	 * models to complete execution and returns the final result with custom HTTP headers
	 * Supports complex reasoning, tool calling, function execution, and multi-modal
	 * understanding Features advanced capabilities like web search integration, code
	 * interpretation, and image analysis
	 * @param request Chat completion parameters including model selection, conversation
	 * messages, generation settings, tools configuration, and response format
	 * @param headers Custom HTTP headers to be added to the request
	 * @return Complete chat completion response with generated content, usage statistics,
	 * tool call results, and reasoning traces
	 */
	@POST("chat/completions")
	Single<ModelData> createChatCompletion(@Body ChatCompletionCreateParams request,
			@HeaderMap Map<String, String> headers);

	/**
	 * Query the result of an asynchronous chat completion task Retrieves the completion
	 * result or current status using the task ID from async request Provides detailed
	 * progress information for long-running tasks and complex reasoning operations
	 * Supports polling-based result retrieval with comprehensive status reporting
	 * @param id Unique task ID returned from asynchronous chat completion request
	 * @return Chat completion result with generated content, processing status,
	 * completion percentage, and any intermediate results or error information
	 */
	@GET("async-result/{id}")
	Single<ModelData> queryAsyncResult(@Path("id") String id);

}
