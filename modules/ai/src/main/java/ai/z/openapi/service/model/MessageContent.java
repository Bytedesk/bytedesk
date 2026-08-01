package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class MessageContent {

	/** Message Type: text image_url input_audio video_url file_url */
	private String type;

	/** Message Content: when type is text */
	private String text;

	/** Message Content: when type is image_url */
	@JsonProperty("image_url")
	private ImageUrl imageUrl;

	/** Message Content: when type is input_audio */
	@JsonProperty("input_audio")
	private InputAudio inputAudio;

	/**
	 * Message Content: when type is video_url
	 */
	@JsonProperty("video_url")
	private VideoUrl videoUrl;

	/**
	 * Message Content: when type is file_url Only support by GLM4.5V
	 */
	@JsonProperty("file_url")
	private FileUrl fileUrl;

}
