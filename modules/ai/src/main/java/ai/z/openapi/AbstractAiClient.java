package ai.z.openapi;

import ai.z.openapi.service.AbstractClientBaseService;
import ai.z.openapi.service.fileparsing.FileParsingService;
import ai.z.openapi.service.fileparsing.FileParsingServiceImpl;
import ai.z.openapi.service.model.ChatError;
import ai.z.openapi.service.model.ZAiHttpException;
import ai.z.openapi.service.chat.ChatService;
import ai.z.openapi.service.chat.ChatServiceImpl;
import ai.z.openapi.service.agents.AgentService;
import ai.z.openapi.service.agents.AgentServiceImpl;
import ai.z.openapi.service.embedding.EmbeddingService;
import ai.z.openapi.service.embedding.EmbeddingServiceImpl;
import ai.z.openapi.service.file.FileService;
import ai.z.openapi.service.file.FileServiceImpl;
import ai.z.openapi.service.audio.AudioService;
import ai.z.openapi.service.audio.AudioServiceImpl;
import ai.z.openapi.service.image.ImageService;
import ai.z.openapi.service.image.ImageServiceImpl;
import ai.z.openapi.service.batches.BatchService;
import ai.z.openapi.service.batches.BatchServiceImpl;
import ai.z.openapi.service.ocr.HandwritingOcrService;
import ai.z.openapi.service.ocr.HandwritingOcrServiceImpl;
import ai.z.openapi.service.web_search.WebSearchService;
import ai.z.openapi.service.web_search.WebSearchServiceImpl;
import ai.z.openapi.service.web_reader.WebReaderService;
import ai.z.openapi.service.web_reader.WebReaderServiceImpl;
import ai.z.openapi.service.videos.VideosService;
import ai.z.openapi.service.videos.VideosServiceImpl;
import ai.z.openapi.service.layoutparsing.LayoutParsingService;
import ai.z.openapi.service.layoutparsing.LayoutParsingServiceImpl;
import ai.z.openapi.service.assistant.AssistantService;
import ai.z.openapi.service.assistant.AssistantServiceImpl;
import ai.z.openapi.service.voiceclone.VoiceCloneService;
import ai.z.openapi.service.voiceclone.VoiceCloneServiceImpl;
import ai.z.openapi.service.moderations.ModerationService;
import ai.z.openapi.service.moderations.ModerationServiceImpl;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.core.model.BiFlowableClientResponse;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.OkHttps;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.utils.StringUtils;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AbstractAiClient is the abstract base class for AI client implementations. This class
 * provides common functionality for interacting with AI APIs including service
 * management, request execution, and resource management.
 *
 * <p>
 * This class serves as the foundation for specific AI client implementations such as
 * ZaiClient and ZhipuAiClient, providing shared functionality while allowing each
 * implementation to have its own static factory methods.
 * </p>
 */
public abstract class AbstractAiClient extends AbstractClientBaseService {

	/** Logger instance for this class */
	private static final Logger logger = LoggerFactory.getLogger(AbstractAiClient.class);

	/** HTTP client for making network requests */
	protected final OkHttpClient httpClient;

	/** Retrofit instance for API communication */
	protected final Retrofit retrofit;

	// Service instances - lazily initialized for thread safety and performance
	/** Chat service for conversational AI operations */
	private ChatService chatService;

	/** Agent service for AI agent management */
	private AgentService agentService;

	/** Embedding service for text embeddings */
	private EmbeddingService embeddingService;

	/** File service for file operations */
	private FileService fileService;

	/** Audio service for audio processing */
	private AudioService audioService;

	/** Image service for image generation and processing */
	private ImageService imageService;

	/** Batch service for batch processing operations */
	private BatchService batchService;

	/** Web search service for internet search capabilities */
	private WebSearchService webSearchService;

	/** Web reader service for parsing web pages */
	private WebReaderService webReaderService;

	/** Videos service for video processing */
	private VideosService videosService;

	/** Assistant service for AI assistant functionality */
	private AssistantService assistantService;

	/** Voice clone service for voice cloning operations */
	private VoiceCloneService voiceCloneService;

	/** FileParsing service for fileParsing operations */
	private FileParsingService fileParsingService;

	/** HandWriting service for handwritingOcrService operations */
	private HandwritingOcrService handwritingOcrService;

	/** Layout parsing service for layout_parsing operations */
	private LayoutParsingService layoutParsingService;

	/** Moderation service for content safety detection */
	private ModerationService moderationService;

	/**
	 * Constructs a new AbstractAiClient with the specified configuration.
	 * @param config the configuration object containing API keys, timeouts, and other
	 * settings
	 * @param baseUrl the base URL for the API endpoint
	 * @throws IllegalArgumentException if config is null or invalid
	 */
	protected AbstractAiClient(ZaiConfig config, String baseUrl) {
		logger.info("ZAI Init the client: {}, baseUrl: {}", this.getClass().getSimpleName(), baseUrl);
		this.httpClient = OkHttps.create(config);
		this.retrofit = new Retrofit.Builder().baseUrl(baseUrl)
			.client(httpClient)
			.addConverterFactory(JacksonConverterFactory.create(mapper))
			.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
			.build();
	}

	// ==================== Service Accessor Methods ====================
	// All service methods use lazy initialization with synchronization for thread safety

	/**
	 * Returns the chat service for conversational AI operations. This service handles
	 * chat completions, streaming conversations, and related functionality.
	 * @return the ChatService instance (lazily initialized)
	 */
	public synchronized ChatService chat() {
		if (chatService == null) {
			this.chatService = new ChatServiceImpl(this);
		}
		return chatService;
	}

	/**
	 * Returns the agent service for AI agent management. This service handles agent
	 * creation, configuration, and execution.
	 * @return the AgentService instance (lazily initialized)
	 */
	public synchronized AgentService agents() {
		if (agentService == null) {
			this.agentService = new AgentServiceImpl(this);
		}
		return agentService;
	}

	/**
	 * Returns the embedding service for text embeddings. This service converts text into
	 * numerical vector representations.
	 * @return the EmbeddingService instance (lazily initialized)
	 */
	public synchronized EmbeddingService embeddings() {
		if (embeddingService == null) {
			this.embeddingService = new EmbeddingServiceImpl(this);
		}
		return embeddingService;
	}

	/**
	 * Returns the file service for file operations. This service handles file uploads,
	 * downloads, and management.
	 * @return the FileService instance (lazily initialized)
	 */
	public synchronized FileService files() {
		if (fileService == null) {
			this.fileService = new FileServiceImpl(this);
		}
		return fileService;
	}

	/**
	 * Returns the audio service for audio processing. This service handles
	 * speech-to-text, text-to-speech, and audio analysis.
	 * @return the AudioService instance (lazily initialized)
	 */
	public synchronized AudioService audio() {
		if (audioService == null) {
			this.audioService = new AudioServiceImpl(this);
		}
		return audioService;
	}

	/**
	 * Returns the image service for image generation and processing. This service handles
	 * image creation, editing, and analysis.
	 * @return the ImageService instance (lazily initialized)
	 */
	public synchronized ImageService images() {
		if (imageService == null) {
			this.imageService = new ImageServiceImpl(this);
		}
		return imageService;
	}

	/**
	 * Returns the batch service for batch processing operations. This service handles
	 * large-scale batch processing of requests.
	 * @return the BatchService instance (lazily initialized)
	 */
	public synchronized BatchService batches() {
		if (batchService == null) {
			this.batchService = new BatchServiceImpl(this);
		}
		return batchService;
	}

	/**
	 * Returns the web search service for internet search capabilities. This service
	 * provides AI-powered web search functionality.
	 * @return the WebSearchService instance (lazily initialized)
	 */
	public synchronized WebSearchService webSearch() {
		if (webSearchService == null) {
			this.webSearchService = new WebSearchServiceImpl(this);
		}
		return webSearchService;
	}

	/**
	 * Returns the web reader service for parsing web pages. This service reads and
	 * extracts content, metadata, images, and links from URLs.
	 * @return the WebReaderService instance (lazily initialized)
	 */
	public synchronized WebReaderService webReader() {
		if (webReaderService == null) {
			this.webReaderService = new WebReaderServiceImpl(this);
		}
		return webReaderService;
	}

	/**
	 * Returns the videos service for video processing. This service handles video
	 * analysis, generation, and manipulation.
	 * @return the VideosService instance (lazily initialized)
	 */
	public synchronized VideosService videos() {
		if (videosService == null) {
			this.videosService = new VideosServiceImpl(this);
		}
		return videosService;
	}

	/**
	 * Returns the assistant service for AI assistant functionality. This service provides
	 * advanced AI assistant capabilities.
	 * @return the AssistantService instance (lazily initialized)
	 */
	public synchronized AssistantService assistants() {
		if (assistantService == null) {
			this.assistantService = new AssistantServiceImpl(this);
		}
		return assistantService;
	}

	/**
	 * Returns the voice clone service for voice cloning operations. This service handles
	 * voice cloning creation, deletion, and listing functionality.
	 * @return the VoiceCloneService instance (lazily initialized)
	 */
	public synchronized VoiceCloneService voiceClone() {
		if (voiceCloneService == null) {
			this.voiceCloneService = new VoiceCloneServiceImpl(this);
		}
		return voiceCloneService;
	}

	/**
	 * Returns the file service for file operations. This service handles file uploads,
	 * downloads, and management.
	 * @return the FileParsingService instance (lazily initialized)
	 */
	public synchronized FileParsingService fileParsing() {
		if (fileParsingService == null) {
			this.fileParsingService = new FileParsingServiceImpl(this);
		}
		return fileParsingService;
	}

	public synchronized HandwritingOcrService handwriting() {
		if (handwritingOcrService == null) {
			this.handwritingOcrService = new HandwritingOcrServiceImpl(this);
		}
		return handwritingOcrService;
	}

	public synchronized LayoutParsingService layoutParsing() {
		if (layoutParsingService == null) {
			this.layoutParsingService = new LayoutParsingServiceImpl(this);
		}
		return layoutParsingService;
	}

	/**
	 * Returns the moderation service for content safety detection. This service handles
	 * content moderation for text, image, video, and audio inputs.
	 * @return the ModerationService instance (lazily initialized)
	 */
	public synchronized ModerationService moderations() {
		if (moderationService == null) {
			this.moderationService = new ModerationServiceImpl(this);
		}
		return moderationService;
	}

	// ==================== Utility Methods ====================

	/**
	 * Returns the underlying Retrofit instance used for API communication. This method is
	 * primarily intended for advanced users who need direct access to the Retrofit client
	 * for custom API calls.
	 * @return the Retrofit instance
	 */
	public Retrofit retrofit() {
		return retrofit;
	}

	/**
	 * Closes the AI client and releases all associated resources. This method shuts down
	 * the HTTP client's connection pool and executor service. After calling this method,
	 * the client should not be used for further requests.
	 *
	 * <p>
	 * <strong>Important:</strong> Always call this method when you're done with the
	 * client to prevent resource leaks.
	 * </p>
	 */
	public void close() {
		try {
			if (httpClient != null) {
				httpClient.dispatcher().executorService().shutdown();
				httpClient.connectionPool().evictAll();
				if (httpClient.cache() != null) {
					httpClient.cache().close();
				}
			}
		}
		catch (Exception e) {
			logger.error("Error closing http client", e);
		}
	}

	// ==================== Core Request Execution Methods ====================

	/**
	 * Executes a synchronous API request and returns the response. This method handles
	 * the complete request lifecycle including error handling and response wrapping.
	 * @param <Data> the type of data expected in the response
	 * @param <Param> the type of parameters for the request
	 * @param <TReq> the type of client request
	 * @param <TResp> the type of client response
	 * @param request the client request containing parameters
	 * @param requestSupplier the supplier that creates the actual API call
	 * @param tRespClass the class of the response type
	 * @return the wrapped response containing either success data or error information
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Data, Param, TReq extends ClientRequest<Param>, TResp extends ClientResponse<Data>> TResp executeRequest(
			TReq request, RequestSupplier<Param, Data> requestSupplier, Class<TResp> tRespClass) {
		Single<Data> apiCall = requestSupplier.get((Param) request);

		TResp tResp = convertToClientResponse(tRespClass);
		try {
			// Execute the API call synchronously
			Data response = execute(apiCall);
			tResp.setCode(200);
			tResp.setMsg("Call Successful");
			tResp.setData(response);
			tResp.setSuccess(true);
		}
		catch (ZAiHttpException e) {
			logger.error("API request failed with call error", e);
			tResp.setCode(e.statusCode);
			tResp.setMsg("Call Failed");
			tResp.setSuccess(false);
			ChatError chatError = new ChatError();
			if (StringUtils.isNotEmpty(e.code)) {
				chatError.setCode(Integer.parseInt(e.code));
			}
			chatError.setMessage(e.getMessage());
			tResp.setError(chatError);
		}
		return tResp;
	}

	/**
	 * Executes a streaming API request and returns a BiFlowableClientResponse where
	 * response body and stream element type can differ.
	 * @param request the request object
	 * @param requestSupplier the streaming API supplier
	 * @param tRespClass the client response class (must implement
	 * BiFlowableClientResponse<Data, F>)
	 * @param tStreamDataClass the stream data element class
	 * @return a response containing a Flowable<F> stream
	 */
	@SuppressWarnings("unchecked")
	public <Data, F, Param, TReq extends ClientRequest<Param>, TResp extends BiFlowableClientResponse<Data, F>> TResp biStreamRequest(
			TReq request, FlowableRequestSupplier<Param, retrofit2.Call<ResponseBody>> requestSupplier,
			Class<TResp> tRespClass, Class<F> tStreamDataClass) {
		retrofit2.Call<ResponseBody> apiCall = requestSupplier.get((Param) request);
		TResp tResp = convertToClientResponse(tRespClass);

		try {
			Flowable<F> stream = stream(apiCall, tStreamDataClass);
			tResp.setCode(200);
			tResp.setMsg("Stream initialized successfully");
			tResp.setSuccess(true);
			tResp.setFlowable(stream);
		}
		catch (ZAiHttpException e) {
			handleStreamError(tResp, e);
		}
		return tResp;
	}

	private void handleStreamError(ClientResponse<?> response, ZAiHttpException e) {
		logger.error("Streaming API request failed with business error", e);
		response.setCode(e.statusCode);
		response.setMsg("Business error");
		response.setSuccess(false);
		ChatError chatError = new ChatError();
		chatError.setCode(Integer.parseInt(e.code));
		chatError.setMessage(e.getMessage());
		response.setError(chatError);
	}

	/**
	 * Executes a streaming API request and returns a FlowableClientResponse with stream
	 * elements of type Data.
	 * @param request the request object
	 * @param requestSupplier the streaming API supplier
	 * @param tRespClass the client response class (must implement
	 * FlowableClientResponse<Data>)
	 * @param tDataClass the class representing stream data type
	 * @return a response containing a Flowable<Data> stream
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Data, Param, TReq extends ClientRequest<Param>, TResp extends FlowableClientResponse<Data>> TResp streamRequest(
			TReq request, FlowableRequestSupplier<Param, retrofit2.Call<ResponseBody>> requestSupplier,
			Class<TResp> tRespClass, Class<Data> tDataClass) {
		retrofit2.Call<ResponseBody> apiCall = requestSupplier.get((Param) request);
		TResp tResp = convertToClientResponse(tRespClass);

		try {
			Flowable<Data> stream = stream(apiCall, tDataClass);
			tResp.setCode(200);
			tResp.setMsg("Stream initialized successfully");
			tResp.setSuccess(true);
			tResp.setFlowable(stream);
		}
		catch (ZAiHttpException e) {
			handleStreamError(tResp, e);
		}
		return tResp;
	}

	/**
	 * Creates a new instance of the specified response class using reflection. This
	 * helper method is used to instantiate response objects dynamically.
	 * @param <Data> the type of data contained in the response
	 * @param <TResp> the type of client response
	 * @param tRespClass the class of the response type to instantiate
	 * @return a new instance of the response class
	 * @throws RuntimeException if the response object cannot be created
	 */
	private <Data, TResp extends ClientResponse<Data>> TResp convertToClientResponse(Class<TResp> tRespClass) {
		try {
			return tRespClass.getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException("Failed to create response object of type: " + tRespClass.getSimpleName(), e);
		}
	}

	// ==================== Abstract Builder Base Class ====================

	/**
	 * Abstract base builder class for creating AI client instances with custom
	 * configurations. This builder provides common functionality for setting up clients
	 * with various options including authentication, network settings, and connection
	 * pooling.
	 */
	public abstract static class AbstractBuilder<T extends AbstractAiClient, B extends AbstractBuilder<T, B>> {

		/** Configuration object that accumulates all builder settings */
		protected final ZaiConfig config = new ZaiConfig();

		public AbstractBuilder() {
		}

		/**
		 * Creates a new builder with the specified API key.
		 * @param apiKey the API key for authentication
		 * @throws IllegalArgumentException if apiKey is null or empty
		 */
		public AbstractBuilder(String apiKey) {
			if (apiKey == null || apiKey.trim().isEmpty()) {
				throw new IllegalArgumentException("API key cannot be null or empty");
			}
			config.setApiKey(apiKey);
		}

		/**
		 * Creates a new builder with a custom base URL and API secret key.
		 * @param baseUrl the custom base URL for the API endpoint
		 * @param apiKey the API secret key for authentication
		 * @throws IllegalArgumentException if any parameter is null or empty
		 */
		public AbstractBuilder(String baseUrl, String apiKey) {
			if (baseUrl == null || baseUrl.trim().isEmpty()) {
				throw new IllegalArgumentException("Base URL cannot be null or empty");
			}
			if (apiKey == null || apiKey.trim().isEmpty()) {
				throw new IllegalArgumentException("API secret key cannot be null or empty");
			}
			config.setBaseUrl(baseUrl);
			config.setApiKey(apiKey);
		}

		/**
		 * Returns this builder instance with proper type casting for method chaining.
		 * @return this builder instance
		 */
		@SuppressWarnings("unchecked")
		protected B self() {
			return (B) this;
		}

		/**
		 * Config the api service base url
		 * @param baseUrl base url
		 * @return this Builder instance for method chaining
		 */
		public B baseUrl(String baseUrl) {
			if (baseUrl == null || baseUrl.trim().isEmpty()) {
				throw new IllegalArgumentException("Base URL cannot be null or empty");
			}
			config.setBaseUrl(baseUrl);
			return self();
		}

		/**
		 * Config the apikey
		 * @param apiKey api key
		 * @return this Builder instance for method chaining
		 */
		public B apiKey(String apiKey) {
			if (apiKey == null || apiKey.trim().isEmpty()) {
				throw new IllegalArgumentException("API secret key cannot be null or empty");
			}
			config.setApiKey(apiKey);
			return self();
		}

		/**
		 * Config the custom headers
		 * @param customHeaders custom headers
		 * @return this Builder instance for method chaining
		 */
		public B customHeaders(Map<String, String> customHeaders) {
			if (customHeaders == null || customHeaders.isEmpty()) {
				throw new IllegalArgumentException("Custom headers cannot be null or empty");
			}
			config.setCustomHeaders(customHeaders);
			return self();
		}

		/**
		 * Disables token caching, forcing the client to use API keys for direct requests.
		 * This is the default behavior.
		 * @return this Builder instance for method chaining
		 */
		public B disableTokenCache() {
			config.setDisableTokenCache(true);
			return self();
		}

		/**
		 * Enables token caching, allowing the client to use access tokens for requests.
		 * This can improve performance by reducing authentication overhead.
		 * @return this Builder instance for method chaining
		 */
		public B enableTokenCache() {
			config.setDisableTokenCache(false);
			return self();
		}

		/**
		 * Configures the HTTP connection pool settings.
		 * @param maxIdleConnections maximum number of idle connections to keep in the
		 * pool
		 * @param keepAliveDuration how long to keep idle connections alive
		 * @param timeUnit the time unit for the keep alive duration
		 * @return this Builder instance for method chaining
		 */
		public B connectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
			config.setConnectionPoolMaxIdleConnections(maxIdleConnections);
			config.setConnectionPoolKeepAliveDuration(keepAliveDuration);
			config.setConnectionPoolTimeUnit(timeUnit);
			return self();
		}

		/**
		 * Sets the token expiration time in milliseconds.
		 * @param expireMillis the token expiration time in milliseconds
		 * @return this Builder instance for method chaining
		 */
		public B tokenExpire(int expireMillis) {
			config.setExpireMillis(expireMillis);
			return self();
		}

		/**
		 * Configures network request timeout settings.
		 * @param requestTimeOut the overall request timeout, 0 is no timeout
		 * @param connectTimeout the connection timeout
		 * @param readTimeout the read timeout
		 * @param writeTimeout the write timeout
		 * @param timeUnit the time unit for all timeout values
		 * @return this Builder instance for method chaining
		 */
		public B networkConfig(int requestTimeOut, int connectTimeout, int readTimeout, int writeTimeout,
				TimeUnit timeUnit) {
			config.setRequestTimeOut(requestTimeOut);
			config.setConnectTimeout(connectTimeout);
			config.setReadTimeout(readTimeout);
			config.setWriteTimeout(writeTimeout);
			config.setTimeOutTimeUnit(timeUnit);
			return self();
		}

		/**
		 * Builds and returns a new AI client instance with the configured settings.
		 * @return a new AI client instance
		 * @throws IllegalStateException if the configuration is invalid
		 */
		public abstract T build();

	}

}