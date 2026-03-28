package ai.z.openapi.service.videos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the result of a video creation process.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoResult {

	/**
	 * Video URL
	 */
	@JsonProperty("url")
	private String url;

	/**
	 * Cover image URL
	 */
	@JsonProperty("cover_image_url")
	private String coverImageUrl;

}
