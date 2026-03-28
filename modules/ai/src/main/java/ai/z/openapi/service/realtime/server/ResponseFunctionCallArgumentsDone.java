package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that model-generated function call arguments are complete. This
 * event is sent when the function call arguments generation has finished.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ResponseFunctionCallArgumentsDone extends RealtimeServerEvent {

	/**
	 * The ID of the response that generated this function call.
	 */
	@JsonProperty("response_id")
	private String responseId;

	/**
	 * The ID of the item that contains this function call.
	 */
	@JsonProperty("item_id")
	private String itemId;

	/**
	 * The index of the output in the response.
	 */
	@JsonProperty("output_index")
	private int outputIndex;

	/**
	 * The unique identifier for this function call.
	 */
	@JsonProperty("call_id")
	private String callId;

	/**
	 * The name of the function being called.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * The complete arguments for the function call as a JSON string.
	 */
	@JsonProperty("arguments")
	private String arguments;

	public ResponseFunctionCallArgumentsDone() {
		super.setType("response.function_call_arguments.done");
		this.responseId = "";
		this.itemId = "";
		this.outputIndex = 0;
		this.callId = "";
		this.name = "";
		this.arguments = "";
	}

}
