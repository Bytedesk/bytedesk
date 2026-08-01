package ai.z.openapi.core.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.core.cache.ICache;
import ai.z.openapi.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT token manager for handling token generation and caching.
 */
public class TokenManager {

	private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);

	private final ICache cache;

	private static final String TOKEN_KEY_PREFIX = "zai_oapi_token";

	/**
	 * Additional delay time (5 minutes) to prevent token expiration issues.
	 */
	private static final Long DELAY_EXPIRE_TIME = 5 * 60 * 1000L;

	/**
	 * Constructs TokenManager with specified cache implementation.
	 * @param cache cache implementation for token storage
	 */
	public TokenManager(ICache cache) {
		this.cache = cache;
	}

	/**
	 * Gets valid JWT token, either from cache or generates new one.
	 * @param config ZAI configuration containing API credentials
	 * @return valid JWT token
	 */
	public String getToken(ZaiConfig config) {
		String[] arrStr = config.getApiKey().split("\\.");
		if (arrStr.length != 2) {
			throw new RuntimeException("invalid api Key");
		}
		String appId = arrStr[0];
		String tokenCacheKey = genTokenCacheKey(appId);
		String cacheToken = cache.get(tokenCacheKey);
		if (StringUtils.isNotEmpty(cacheToken)) {
			return cacheToken;
		}
		String newToken = createJwt(config);
		cache.set(tokenCacheKey, newToken, config.getExpireMillis(), TimeUnit.MILLISECONDS);
		return newToken;
	}

	/**
	 * Creates JWT token using HMAC256 algorithm.
	 * @param config ZAI configuration
	 * @return JWT token string or null if creation fails
	 */
	private static String createJwt(ZaiConfig config) {
		Algorithm alg;
		String algId = config.getAlg();
		String[] arrStr = config.getApiKey().split("\\.");
		if (arrStr.length != 2) {
			throw new RuntimeException("invalid api Key");
		}
		String appId = arrStr[0];
		String apiSecret = arrStr[1];
		if ("HS256".equals(algId)) {
			try {
				alg = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
			}
			catch (Exception e) {
				logger.error("Failed to create HMAC256 algorithm", e);
				return null;
			}
		}
		else {
			// Currently only HS256 is supported
			logger.error("Algorithm: {} not supported", algId);
			return null;
		}

		Map<String, Object> payload = new HashMap<>();
		// here the api_key is the apiId
		payload.put("api_key", appId);
		payload.put("exp", System.currentTimeMillis() + config.getExpireMillis() + DELAY_EXPIRE_TIME);
		payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
		Map<String, Object> headerClaims = new HashMap<>();
		headerClaims.put("alg", "HS256");
		headerClaims.put("sign_type", "SIGN");
		return JWT.create().withPayload(payload).withHeader(headerClaims).sign(alg);
	}

	/**
	 * Generates cache key for token storage.
	 * @param apiKey API key
	 * @return formatted cache key
	 */
	private String genTokenCacheKey(String apiKey) {
		return String.format("%s-%s", TOKEN_KEY_PREFIX, apiKey);
	}

}
