package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

import java.io.File;

/**
 * Response wrapper for audio customization API calls. This class contains the response
 * data for audio customization requests, including the generated audio file and status
 * information.
 */
@Data
public class AudioCustomizationResponse implements ClientResponse<File> {

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
	 * The generated audio file.
	 */
	private File data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

}
