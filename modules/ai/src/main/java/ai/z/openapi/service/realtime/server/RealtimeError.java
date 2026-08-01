package ai.z.openapi.service.realtime.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.realtime.RealtimeServerEvent;
import ai.z.openapi.service.realtime.object.ErrorObj;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Server event indicating that an error has occurred. This event is sent when there's a
 * client or server-side error during realtime communication.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class RealtimeError extends RealtimeServerEvent {

	/**
	 * The ID of the item associated with this error, if applicable.
	 */
	@JsonProperty("item_id")
	private String itemId;

	/**
	 * The index of the content part associated with this error, if applicable.
	 */
	@JsonProperty("content_index")
	private int contentIndex;

	/**
	 * The error details including type, code, message, and additional parameters.
	 */
	@JsonProperty("error")
	private ErrorObj error;

	public RealtimeError() {
		super.setType("error");
		this.itemId = "";
		this.contentIndex = 0;
		this.error = new ErrorObj();
	}

}
