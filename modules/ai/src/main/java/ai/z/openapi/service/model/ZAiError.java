package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the error body when an ZAI request fails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZAiError {

	public ZAiErrorDetails error;

	@JsonProperty("contentFilter")
	public List<ContentFilter> contentFilter;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ZAiErrorDetails {

		/**
		 * Human-readable error message
		 */
		String message;

		/**
		 * ZAI error code, for example "401"
		 */
		String code;

	}

	/**
	 * Sensitive words
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ContentFilter {

		String level;

		String role;

	}

}
