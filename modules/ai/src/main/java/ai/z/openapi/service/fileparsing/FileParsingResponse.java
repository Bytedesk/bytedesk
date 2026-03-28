package ai.z.openapi.service.fileparsing;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class FileParsingResponse implements ClientResponse<FileParsingUploadResp> {

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
	 * The FileParsing result data.
	 */
	private FileParsingUploadResp data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

}
