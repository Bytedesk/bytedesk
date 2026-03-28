package ai.z.openapi.core.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe local cache implementation using ConcurrentHashMap. Provides basic caching
 * functionality with expiration support.
 */
public class LocalCache implements ICache {

	private static final Logger log = LoggerFactory.getLogger(LocalCache.class);

	private static final ConcurrentMap<String, Value> CACHE = new ConcurrentHashMap<>(8);

	/**
	 * Private constructor to prevent direct instantiation.
	 */
	private LocalCache() {
	}

	/**
	 * Gets singleton instance of LocalCache.
	 * @return LocalCache instance
	 */
	public static LocalCache getInstance() {
		return Inner.LOCAL_CACHE;
	}

	@Override
	public String get(String key) {
		Value v = LocalCache.CACHE.get(key);
		if (v == null || new Date().after(v.end)) {
			return "";
		}

		log.debug("Retrieved key: {}, time left: {}s", key, (v.end.getTime() - new Date().getTime()) / 1000);
		return v.value;
	}

	@Override
	public void set(String key, String value, int expire, TimeUnit timeUnit) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, (int) timeUnit.toSeconds(expire));
		Value v = new Value(value, calendar.getTime());
		log.debug("Cached key: {}, expire time: {}", key, calendar.getTime());
		LocalCache.CACHE.put(key, v);
	}

	/**
	 * Internal value wrapper with expiration time.
	 */
	private static class Value {

		final String value;

		final Date end;

		public Value(String value, Date time) {
			this.value = value;
			this.end = time;
		}

	}

	/**
	 * Singleton holder pattern for thread-safe lazy initialization.
	 */
	private static class Inner {

		private static final LocalCache LOCAL_CACHE = new LocalCache();

	}

}
