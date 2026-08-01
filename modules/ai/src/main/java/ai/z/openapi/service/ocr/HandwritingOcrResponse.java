package ai.z.openapi.service.ocr;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class HandwritingOcrResponse implements ClientResponse<HandwritingOcrResult> {

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
	 * The HandwritingOcr result data.
	 */
	private HandwritingOcrResult data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

}
