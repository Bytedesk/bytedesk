package ai.z.openapi.service.agents;

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
public class AgentContent {

	/** Message type: text, image_url, video_url */
	private String type;

	/** Message content when type is text */
	private String text;

	/** Message image URL when type is image_url */
	@JsonProperty("image_url")
	private String imageUrl;

	/** Message video URL when type is video_url */
	@JsonProperty("video_url")
	private String videoUrl;

	/** Message object when type is object */
	@JsonProperty("object")
	private Object object;

	public static AgentContent ofText(String text) {
		return AgentContent.builder().type("text").text(text).build();
	}

	public static AgentContent ofImageUrl(String imageUrl) {
		return AgentContent.builder().type("image_url").imageUrl(imageUrl).build();
	}

	public static AgentContent ofVideoUrl(String videoUrl) {
		return AgentContent.builder().type("video_url").videoUrl(videoUrl).build();
	}

	public static AgentContent ofObject(Object object) {
		return AgentContent.builder().type("object").object(object).build();
	}

}
