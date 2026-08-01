package ai.z.openapi.core.token;

import ai.z.openapi.core.cache.LocalCache;

/**
 * Global token manager providing singleton access to TokenManager instance. Uses
 * LocalCache as default cache implementation.
 */
public class GlobalTokenManager {

	/**
	 * Global TokenManager instance with LocalCache.
	 */
	private static volatile TokenManager globalTokenManager = new TokenManager(LocalCache.getInstance());

	/**
	 * Gets the global TokenManager instance.
	 * @return TokenManager instance
	 */
	public static TokenManager getTokenManagerV4() {
		return globalTokenManager;
	}

	/**
	 * Sets custom TokenManager implementation.
	 * @param tokenManager custom TokenManager instance
	 */
	public static void setTokenManager(TokenManager tokenManager) {
		globalTokenManager = tokenManager;
	}

}
