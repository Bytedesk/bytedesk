package ai.z.openapi.service.moderations;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

/**
 * Response wrapper for moderation API calls. Contains the result of content moderation
 * operations along with status information.
 */
@Data
public class ModerationResponse implements ClientResponse<ModerationResult> {

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
	 * The moderation result data.
	 */
	private ModerationResult data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

}