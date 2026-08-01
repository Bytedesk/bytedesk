package ai.z.openapi.service.moderations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Usage statistics for Moderation API calls.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationUsage {

	@JsonProperty("moderation_text")
	private ModerationText moderationText;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModerationText {

		@JsonProperty("call_count")
		private String callCount;

	}

}
