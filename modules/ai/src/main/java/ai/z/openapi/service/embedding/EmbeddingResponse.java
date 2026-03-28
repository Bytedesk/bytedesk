package ai.z.openapi.service.embedding;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

/**
 * Response wrapper for embedding API calls. Contains the result of text embedding
 * operations along with status information.
 */
@Data
public class EmbeddingResponse implements ClientResponse<EmbeddingResult> {

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
	 * The embedding result data.
	 */
	private EmbeddingResult data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

}
