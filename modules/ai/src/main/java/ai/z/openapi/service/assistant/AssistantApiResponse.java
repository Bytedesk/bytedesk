package ai.z.openapi.service.assistant;

import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.service.model.ChatError;
import io.reactivex.rxjava3.core.Flowable;
import lombok.Data;

/**
 * Response wrapper for Assistant API calls that supports both synchronous and streaming
 * responses. This class implements FlowableClientResponse to handle streaming assistant
 * completions.
 */
@Data
public class AssistantApiResponse implements FlowableClientResponse<AssistantCompletion> {

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
	 * The assistant completion data for synchronous responses.
	 */
	private AssistantCompletion data;

	/**
	 * The flowable stream for streaming responses.
	 */
	private Flowable<AssistantCompletion> flowable;

	/**
	 * Error information if the request failed.
	 */
	private ChatError error;

	/**
	 * Default constructor.
	 */
	public AssistantApiResponse() {
	}

	/**
	 * Constructor with code and message.
	 * @param code the response code
	 * @param msg the response message
	 */
	public AssistantApiResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
