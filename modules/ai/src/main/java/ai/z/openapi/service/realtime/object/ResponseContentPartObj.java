package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a content part in a response from the realtime API. This object contains
 * different types of content such as audio, text, or transcript.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ResponseContentPartObj {

	/**
	 * Base64-encoded audio data, if this content part contains audio.
	 */
	@JsonProperty("audio")
	private String audio;

	/**
	 * Text content, if this content part contains text.
	 */
	@JsonProperty("text")
	private String text;

	/**
	 * Transcript of audio content, if available.
	 */
	@JsonProperty("transcript")
	private String transcript;

	/**
	 * The type of content part (e.g., "audio", "text").
	 */
	@JsonProperty("type")
	private String type;

	public ResponseContentPartObj() {
		this.audio = "";
		this.text = "";
		this.transcript = "";
		this.type = "";
	}

}
