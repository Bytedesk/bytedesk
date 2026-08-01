package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a conversation item object containing message content. This object can
 * represent different types of items such as messages, function calls, or function
 * responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ItemObj {

	/**
	 * The unique identifier for this item.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * The type of the item (e.g., "message", "function_call", "function_call_output").
	 */
	@JsonProperty("type")
	private String type;

	/**
	 * The content of the item, containing text, audio, or transcript data.
	 */
	@JsonProperty("content")
	private ItemContent content;

	/**
	 * The call ID for function call items.
	 */
	@JsonProperty("call_id")
	private String callId;

	/**
	 * The arguments for function call items, typically in JSON format.
	 */
	@JsonProperty("arguments")
	private String arguments;

	public ItemObj() {
	}

	/**
	 * Represents the content of an item, which can include text, audio, or transcript
	 * data.
	 */
	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class ItemContent {

		/**
		 * The text content of the item.
		 */
		@JsonProperty("text")
		private String text;

		/**
		 * The audio content of the item, typically base64-encoded.
		 */
		@JsonProperty("audio")
		private String audio;

		/**
		 * The transcript of audio content.
		 */
		@JsonProperty("transcript")
		private String transcript;

		public ItemContent() {
			this.text = "";
			this.audio = "";
			this.transcript = "";
		}

	}

}
