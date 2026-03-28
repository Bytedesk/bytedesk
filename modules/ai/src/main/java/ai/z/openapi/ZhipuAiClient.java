package ai.z.openapi;

import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.utils.StringUtils;

import static ai.z.openapi.core.Constants.ZHIPU_AI_BASE_URL;
import static ai.z.openapi.core.Constants.Z_AI_BASE_URL;

/**
 * ZhipuAiClient is the main entry point for interacting with the ZHIPU AI API. This
 * client provides access to various AI services including chat, embeddings, file
 * operations, audio processing, image generation, and more.
 *
 * <p>
 * The client is specifically configured for ZHIPU AI endpoints and provides thread-safe
 * lazy initialization of services.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>{@code
 * ZhipuAiClient client = new ZhipuAiClient.Builder("your-api-key")
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
public class ZhipuAiClient extends AbstractAiClient {

	/**
	 * Constructs a new ZhipuAiClient with the specified configuration. By default, this
	 * client uses the ZHIPU AI OpenAPI endpoint.
	 * @param config the configuration object containing API keys, timeouts, and other
	 * settings
	 * @throws IllegalArgumentException if config is null or invalid
	 */
	public ZhipuAiClient(ZaiConfig config) {
		super(StringUtils.isEmpty(config.getBaseUrl()) ? createConfigWithDefaultUrl(config) : config,
				StringUtils.isEmpty(config.getBaseUrl()) ? ZHIPU_AI_BASE_URL : config.getBaseUrl());
	}

	/**
	 * Creates a new config with ZHIPU AI base URL as default
	 * @param originalConfig the original configuration
	 * @return a new configuration with ZHIPU AI base URL set
	 */
	private static ZaiConfig createConfigWithDefaultUrl(ZaiConfig originalConfig) {
		ZaiConfig newConfig = new ZaiConfig();
		newConfig.setApiKey(originalConfig.getApiKey());
		newConfig.setBaseUrl(ZHIPU_AI_BASE_URL);
		newConfig.setDisableTokenCache(originalConfig.isDisableTokenCache());
		newConfig.setConnectionPoolMaxIdleConnections(originalConfig.getConnectionPoolMaxIdleConnections());
		newConfig.setConnectionPoolKeepAliveDuration(originalConfig.getConnectionPoolKeepAliveDuration());
		newConfig.setConnectionPoolTimeUnit(originalConfig.getConnectionPoolTimeUnit());
		newConfig.setExpireMillis(originalConfig.getExpireMillis());
		newConfig.setRequestTimeOut(originalConfig.getRequestTimeOut());
		newConfig.setConnectTimeout(originalConfig.getConnectTimeout());
		newConfig.setReadTimeout(originalConfig.getReadTimeout());
		newConfig.setWriteTimeout(originalConfig.getWriteTimeout());
		newConfig.setTimeOutTimeUnit(originalConfig.getTimeOutTimeUnit());
		return newConfig;
	}

	/**
	 * Creates a new Builder instance for constructing ZaiClient.
	 * @return a new Builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	// ==================== Builder Pattern Implementation ====================

	/**
	 * Builder class for creating ZhipuAiClient instances with custom configurations. This
	 * builder provides a fluent API for setting up the client with various options
	 * including authentication, network settings, and connection pooling.
	 *
	 * <p>
	 * Example usage:
	 * </p>
	 * <pre>{@code
	 * ZhipuAiClient client = new ZhipuAiClient.Builder("your-api-key")
	 *     .networkConfig(0, 10, 30, 30, TimeUnit.SECONDS)
	 *     .connectionPool(10, 5, TimeUnit.MINUTES)
	 *     .enableTokenCache()
	 *     .build();
	 * }</pre>
	 */
	public static final class Builder extends AbstractBuilder<ZhipuAiClient, Builder> {

		public Builder() {
			super();
		}

		/**
		 * Creates a new builder with the specified API key.
		 * @param apiKey the API key for authentication
		 * @throws IllegalArgumentException if apiKey is null or empty
		 */
		public Builder(String apiKey) {
			super(apiKey);
		}

		/**
		 * Creates a new builder with a custom base URL and API secret key.
		 * @param baseUrl the custom base URL for the API endpoint
		 * @param apiKey the API secret key for authentication
		 * @throws IllegalArgumentException if any parameter is null or empty
		 */
		public Builder(String baseUrl, String apiKey) {
			super(baseUrl, apiKey);
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
		public ZhipuAiClient build() {
			return new ZhipuAiClient(config);
		}

	}

}