package ai.z.openapi.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * Token cache interface with default LocalCache implementation. Can be replaced with
 * distributed cache (e.g., Redis) as needed.
 */
public interface ICache {

	/**
	 * Retrieves cached value by key.
	 * @param key the cache key
	 * @return cached value or empty string if not found or expired
	 */
	String get(String key);

	/**
	 * Sets cache value with expiration time.
	 * @param key the cache key
	 * @param value the value to cache
	 * @param expire expiration duration
	 * @param timeUnit time unit for expiration
	 */
	void set(String key, String value, int expire, TimeUnit timeUnit);

}
