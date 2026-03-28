package ai.z.openapi.service.moderations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result data from the moderation API containing safety analysis results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationResult {

	/**
	 * Task id.
	 */
	private String id;

	/**
	 * Unique request identifier for tracking.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * Request time in milliseconds.
	 */
	private Long created;

	/**
	 * List of moderation results for each input item.
	 */
	@JsonProperty("result_list")
	private List<ModerationItem> resultList;

	/**
	 * Token usage information for the request.
	 */
	private ModerationUsage usage;

	/**
	 * Individual moderation result for a single input item.
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModerationItem {

		/**
		 * Type of content being moderated (text, image, video, audio).
		 */
		@JsonProperty("content_type")
		private String contentType;

		/**
		 * Risk level assessment: "PASS", "REVIEW", "REJECT".
		 */
		@JsonProperty("risk_level")
		private String riskLevel;

		@JsonProperty("risk_type")
		private List<String> riskType;

		/**
		 * Check if the content is flagged as unsafe.
		 * @return true if risk level is REVIEW or REJECT
		 */
		public boolean isFlagged() {
			return "REVIEW".equalsIgnoreCase(riskLevel) || "REJECT".equalsIgnoreCase(riskLevel);
		}

		/**
		 * Check if the content is safe.
		 * @return true if risk level is low
		 */
		public boolean isSafe() {
			return "PASS".equalsIgnoreCase(riskLevel);
		}

	}

}