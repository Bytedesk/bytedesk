package ai.z.openapi.service.image;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An object containing either a URL or a base 64 encoded image.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

	/**
	 * The URL where the image can be accessed.
	 */
	@JsonProperty("url")
	String url;

	/**
	 * Base64 encoded image string.
	 */
	@JsonProperty("b64_json")
	String b64Json;

	/**
	 * The prompt that was used to generate the image, if there was any revision to the
	 * prompt.
	 */
	@JsonProperty("revised_prompt")
	String revisedPrompt;

}
