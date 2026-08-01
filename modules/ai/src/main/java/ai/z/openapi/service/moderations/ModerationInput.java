package ai.z.openapi.service.moderations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for moderation input content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationInput {

	/**
	 * Type of content being moderated Possible values: "text", "image_url", "video_url",
	 * "audio_url"
	 */
	private String type;

	/**
	 * Text content for text moderation
	 */
	private String text;

	/**
	 * Image URL configuration for image moderation
	 */
	@JsonProperty("image_url")
	private MediaUrl imageUrl;

	/**
	 * Video URL configuration for video moderation
	 */
	@JsonProperty("video_url")
	private MediaUrl videoUrl;

	/**
	 * Audio URL configuration for audio moderation
	 */
	@JsonProperty("audio_url")
	private MediaUrl audioUrl;

	/**
	 * Helper method to create text input
	 */
	public static ModerationInput text(String text) {
		return ModerationInput.builder().type("text").text(text).build();
	}

	/**
	 * Helper method to create image input
	 */
	public static ModerationInput image(String url) {
		return ModerationInput.builder().type("image_url").imageUrl(MediaUrl.builder().url(url).build()).build();
	}

	/**
	 * Helper method to create video input
	 */
	public static ModerationInput video(String url) {
		return ModerationInput.builder().type("video_url").videoUrl(MediaUrl.builder().url(url).build()).build();
	}

	/**
	 * Helper method to create audio input
	 */
	public static ModerationInput audio(String url) {
		return ModerationInput.builder().type("audio_url").audioUrl(MediaUrl.builder().url(url).build()).build();
	}

	/**
	 * Media URL configuration
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MediaUrl {

		/**
		 * URL of the media file
		 */
		private String url;

	}

}