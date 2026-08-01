package ai.z.openapi.service.assistant.query_support;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

/**
 * Response for assistant support query API calls. This class contains the response data
 * for checking assistant support status.
 */
@Data
public class AssistantSupportResponse implements ClientResponse<AssistantSupportStatus> {

	/**
	 * Response status code.
	 */
	private int code;

	/**
	 * Response message.
	 */
	private String msg;

	/**
	 * Indicates if the request was successful.
	 */
	private boolean success;

	/**
	 * The assistant support status data.
	 */
	private AssistantSupportStatus data;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

	public AssistantSupportResponse() {
	}

	public AssistantSupportResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
