package ai.z.openapi.service.image;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

/**
 * Response wrapper for image generation API calls. Contains the result of image
 * generation operations along with status information.
 */
@Data
public class ImageResponse implements ClientResponse<ImageResult> {

	/**
	 * Response status code.
	 */
	private int code;

	/**
	 * Response message.
	 */
	private String msg;

	/**
	 * Indicates whether the request was successful.
	 */
	private boolean success;

	/**
	 * The image generation result data.
	 */
	private ImageResult data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

}
