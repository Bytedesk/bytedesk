package ai.z.openapi;

import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.utils.StringUtils;

import static ai.z.openapi.core.Constants.ZHIPU_AI_BASE_URL;
import static ai.z.openapi.core.Constants.Z_AI_BASE_URL;

/**
 * ZaiClient is the main entry point for interacting with the Z.ai API. This client
 * provides access to various AI services including chat, embeddings, file operations,
 * audio processing, image generation, and more.
 *
 * <p>
 * The client supports both Z.ai and ZHIPU AI endpoints and provides thread-safe lazy
 * initialization of services.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>{@code
 * ZaiClient client = new ZaiClient.Builder("your-api-key")
 *     .networkConfig(30, 10, 30, 30, TimeUnit.SECONDS)
 *     .build();
 *
 * ChatService chatService = client.chat();
 * // Use the chat service...
 *
 * client.close(); // Don't forget to close when done
 * }</pre>
 *
 */
public class ZaiClient extends AbstractAiClient {

	/**
	 * Constructs a new ZaiClient with the specified configuration. By default, this
	 * client uses the Z.ai OpenAPI endpoint.
	 * @param config the configuration object containing API keys, timeouts, and other
	 * settings
	 * @throws IllegalArgumentException if config is null or invalid
	 */
	public ZaiClient(ZaiConfig config) {
		super(config, StringUtils.isEmpty(config.getBaseUrl()) ? Z_AI_BASE_URL : config.getBaseUrl());
	}

	// ==================== Builder Pattern Implementation ====================

	/**
	 * Builder class for creating ZaiClient instances with custom configurations. This
	 * builder provides a fluent API for setting up the client with various options
	 * including authentication, network settings, and connection pooling.
	 *
	 * <p>
	 * Example usage:
	 * </p>
	 * <pre>{@code
	 * ZaiClient client = new ZaiClient.Builder("your-api-key")
	 *     .networkConfig(30, 10, 30, 30, TimeUnit.SECONDS)
	 *     .connectionPool(10, 5, TimeUnit.MINUTES)
	 *     .enableTokenCache()
	 *     .build();
	 * }</pre>
	 */
	public static class Builder extends AbstractBuilder<ZaiClient, Builder> {

		public Builder() {
			super();
		}

		public Builder(String apiKey) {
			super(apiKey);
		}

		public Builder(String baseUrl, String apiKey) {
			super(baseUrl, apiKey);
		}

		@Override
		protected Builder self() {
			return this;
		}

		/**
		 * Use the ZHIPU AI base url
		 * @return this Builder instance for method chaining
		 */
		public Builder ofZHIPU() {
			config.setBaseUrl(ZHIPU_AI_BASE_URL);
			return this;
		}

		/**
		 * Use the Z AI base url
		 * @return this Builder instance for method chaining
		 */
		public Builder ofZAI() {
			config.setBaseUrl(Z_AI_BASE_URL);
			return this;
		}

		@Override
		public ZaiClient build() {
			return new ZaiClient(config);
		}

	}

	// ==================== Static Factory Methods ====================

	/**
	 * Creates a new Builder instance for constructing ZaiClient.
	 * @return a new Builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates a ZaiClient configured for ZHIPU AI platform with the specified API key.
	 * @param apiKey the API key for authentication
	 * @return a new ZaiClient instance configured for ZHIPU AI
	 * @throws IllegalArgumentException if apiKey is null or empty
	 */
	public static Builder ofZHIPU(String apiKey) {
		return new Builder().apiKey(apiKey).ofZHIPU();
	}

	/**
	 * Creates a ZaiClient configured for ZHIPU AI platform with the specified API key.
	 * @return a new ZaiClient instance configured for ZHIPU AI
	 * @throws IllegalArgumentException if apiKey is null or empty
	 */
	public static Builder ofZHIPU() {
		return new Builder().ofZHIPU();
	}

}
