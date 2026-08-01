package ai.z.openapi.utils;

/**
 * Utility class providing common string manipulation operations. This class contains
 * static methods for string validation, formatting, and template processing.
 *
 */
public final class StringUtils {

	private StringUtils() {
		throw new UnsupportedOperationException("StringUtils is a utility class and cannot be instantiated");
	}

	/**
	 * Checks if a string is empty or null. A string is considered empty if it's null or
	 * contains only whitespace characters.
	 * @param str the string to check
	 * @return true if the string is null or empty after trimming, false otherwise
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * Checks if a string is not empty and not null.
	 * @param str the string to check
	 * @return true if the string is not null and not empty after trimming, false
	 * otherwise
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

}
