package ai.z.openapi.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ai.z.openapi.core.Constants.Z_AI_BASE_URL;

/**
 * Configuration class for ZAI SDK containing API credentials, JWT settings, HTTP client
 * configurations, and cache settings. Supports reading configuration values from
 * environment variables with memory values taking priority.
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZaiConfig {

	// Environment variable names
	private static final String ENV_BASE_URL = "ZAI_BASE_URL";

	private static final String ENV_API_KEY = "ZAI_API_KEY";

	private static final String ENV_EXPIRE_MILLIS = "ZAI_EXPIRE_MILLIS";

	private static final String ENV_ALG = "ZAI_ALG";

	private static final String ENV_DISABLE_TOKEN_CACHE = "ZAI_DISABLE_TOKEN_CACHE";

	private static final String ENV_CONNECTION_POOL_MAX_IDLE = "ZAI_CONNECTION_POOL_MAX_IDLE";

	private static final String ENV_CONNECTION_POOL_KEEP_ALIVE = "ZAI_CONNECTION_POOL_KEEP_ALIVE";

	private static final String ENV_REQUEST_TIMEOUT = "ZAI_REQUEST_TIMEOUT";

	private static final String ENV_CONNECT_TIMEOUT = "ZAI_CONNECT_TIMEOUT";

	private static final String ENV_READ_TIMEOUT = "ZAI_READ_TIMEOUT";

	private static final String ENV_WRITE_TIMEOUT = "ZAI_WRITE_TIMEOUT";

	/**
	 * Base URL for API endpoints.
	 */
	private String baseUrl;

	/**
	 * Combined API secret key in format: {apiId}.{apiSecret}
	 */
	private String apiKey;

	/**
	 * Custom Http Request Headers
	 */
	private Map<String, String> customHeaders;

	/**
	 * JWT token expiration time in milliseconds (default: 30 minutes).
	 */
	@Builder.Default
	private int expireMillis = 30 * 60 * 1000;

	/**
	 * JWT encryption algorithm (default: HS256).
	 */
	@Builder.Default
	private String alg = "HS256";

	/**
	 * Flag to disable token caching.
	 */
	@Builder.Default
	private boolean disableTokenCache = true;

	/**
	 * Maximum number of idle connections in the connection pool.
	 */
	@Builder.Default
	private int connectionPoolMaxIdleConnections = 5;

	/**
	 * Keep alive duration for connections in the pool (in seconds).
	 */
	@Builder.Default
	private long connectionPoolKeepAliveDuration = 10;

	/**
	 * Time unit for connection pool keep alive duration.
	 */
	@Builder.Default
	private TimeUnit connectionPoolTimeUnit = TimeUnit.SECONDS;

	/**
	 * Request timeout in specified time unit. The whole timeout for complete calls, is
	 * the okhttp call timeout. The 0 value means no timeout.
	 */
	private Integer requestTimeOut;

	/**
	 * Connection timeout in specified time unit.
	 */
	private Integer connectTimeout;

	/**
	 * Read timeout in specified time unit.
	 */
	private Integer readTimeout;

	/**
	 * Write timeout in specified time unit.
	 */
	private Integer writeTimeout;

	/**
	 * Time unit for timeout configurations.
	 */
	@Builder.Default
	private TimeUnit timeOutTimeUnit = TimeUnit.SECONDS;

	/**
	 * Source channel identifier for request tracking.
	 */
	@Builder.Default
	private String source_channel = "java-sdk";

	/**
	 * Constructor with combined API secret key.
	 * @param apiKey combined secret key in format {apiKey}.{apiSecret}
	 * @throws RuntimeException if apiSecretKey format is invalid
	 */
	public ZaiConfig(String apiKey) {
		this.apiKey = apiKey;
		String[] arrStr = apiKey.split("\\.");
		if (arrStr.length != 2) {
			throw new RuntimeException("invalid api Key");
		}
	}

	/**
	 * Sets API key and parses it into id and secret components.
	 * @param apiKey combined secret key in format {apiId}.{apiSecret}
	 * @throws RuntimeException if apiSecretKey format is invalid
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
		String[] arrStr = apiKey.split("\\.");
		if (arrStr.length != 2) {
			throw new RuntimeException("invalid api Key");
		}
	}

	/**
	 * Gets base URL with system property and environment variable fallback.
	 */
	public String getBaseUrl() {
		if (baseUrl != null) {
			return baseUrl;
		}
		return System.getProperty(ENV_BASE_URL);
	}

	/**
	 * Gets API secret key with system property and environment variable fallback.
	 */
	public String getApiKey() {
		if (apiKey != null && !apiKey.isEmpty()) {
			return apiKey;
		}
		String propValue = System.getProperty(ENV_API_KEY);
		String value = propValue != null ? propValue : System.getenv(ENV_API_KEY);
		if (value != null && !value.isEmpty()) {
			// Parse value and set components
			this.apiKey = value;
		}
		return apiKey;
	}

	/**
	 * Gets expire millis with system property and environment variable fallback.
	 */
	public int getExpireMillis() {
		if (expireMillis != 30 * 60 * 1000) { // If not default value
			return expireMillis;
		}
		String propValue = System.getProperty(ENV_EXPIRE_MILLIS);
		String value = propValue != null ? propValue : System.getenv(ENV_EXPIRE_MILLIS);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return expireMillis;
	}

	/**
	 * Gets algorithm with system property and environment variable fallback.
	 */
	public String getAlg() {
		if (!"HS256".equals(alg)) {
			return alg;
		}
		String propValue = System.getProperty(ENV_ALG);
		String value = propValue != null ? propValue : System.getenv(ENV_ALG);
		return value != null ? value : alg;
	}

	/**
	 * Gets disable token cache flag with system property and environment variable
	 * fallback.
	 */
	public boolean isDisableTokenCache() {
		if (disableTokenCache) {
			return true;
		}
		String propValue = System.getProperty(ENV_DISABLE_TOKEN_CACHE);
		String value = propValue != null ? propValue : System.getenv(ENV_DISABLE_TOKEN_CACHE);
		return Boolean.parseBoolean(value);
	}

	/**
	 * Gets connection pool max idle connections with system property and environment
	 * variable fallback.
	 */
	public int getConnectionPoolMaxIdleConnections() {
		if (connectionPoolMaxIdleConnections != 5) { // If not default value
			return connectionPoolMaxIdleConnections;
		}
		String propValue = System.getProperty(ENV_CONNECTION_POOL_MAX_IDLE);
		String value = propValue != null ? propValue : System.getenv(ENV_CONNECTION_POOL_MAX_IDLE);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return connectionPoolMaxIdleConnections;
	}

	/**
	 * Gets connection pool keep alive duration with system property and environment
	 * variable fallback.
	 */
	public long getConnectionPoolKeepAliveDuration() {
		if (connectionPoolKeepAliveDuration != 1) { // If not default value
			return connectionPoolKeepAliveDuration;
		}
		String propValue = System.getProperty(ENV_CONNECTION_POOL_KEEP_ALIVE);
		String value = propValue != null ? propValue : System.getenv(ENV_CONNECTION_POOL_KEEP_ALIVE);
		if (value != null) {
			try {
				return Long.parseLong(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return connectionPoolKeepAliveDuration;
	}

	/**
	 * Gets connection pool time unit (always returns the set value or default).
	 */
	public TimeUnit getConnectionPoolTimeUnit() {
		return connectionPoolTimeUnit != null ? connectionPoolTimeUnit : TimeUnit.SECONDS;
	}

	/**
	 * Gets request timeout with system property and environment variable fallback.
	 */
	public Integer getRequestTimeOut() {
		if (requestTimeOut != null) {
			return requestTimeOut;
		}
		String propValue = System.getProperty(ENV_REQUEST_TIMEOUT);
		String value = propValue != null ? propValue : System.getenv(ENV_REQUEST_TIMEOUT);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return requestTimeOut;
	}

	/**
	 * Gets connect timeout with system property and environment variable fallback.
	 */
	public Integer getConnectTimeout() {
		if (connectTimeout != null) {
			return connectTimeout;
		}
		String propValue = System.getProperty(ENV_CONNECT_TIMEOUT);
		String value = propValue != null ? propValue : System.getenv(ENV_CONNECT_TIMEOUT);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return connectTimeout;
	}

	/**
	 * Gets read timeout with system property and environment variable fallback.
	 */
	public Integer getReadTimeout() {
		if (readTimeout != null) {
			return readTimeout;
		}
		String propValue = System.getProperty(ENV_READ_TIMEOUT);
		String value = propValue != null ? propValue : System.getenv(ENV_READ_TIMEOUT);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return readTimeout;
	}

	/**
	 * Gets write timeout with system property and environment variable fallback.
	 */
	public Integer getWriteTimeout() {
		if (writeTimeout != null) {
			return writeTimeout;
		}
		String propValue = System.getProperty(ENV_WRITE_TIMEOUT);
		String value = propValue != null ? propValue : System.getenv(ENV_WRITE_TIMEOUT);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
				// Return default value if parsing fails
			}
		}
		return writeTimeout;
	}

	/**
	 * Gets timeout time unit (always returns the set value or null).
	 */
	public TimeUnit getTimeOutTimeUnit() {
		return timeOutTimeUnit;
	}

	/**
	 * Gets source channel with system property.
	 */
	public String getSource_channel() {
		return source_channel;
	}

	/**
	 * Get custom headers
	 * @return
	 */
	public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}

}
