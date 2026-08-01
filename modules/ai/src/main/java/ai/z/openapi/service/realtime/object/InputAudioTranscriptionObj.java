package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class InputAudioTranscriptionObj {

	@JsonProperty("model")
	private String model;

	@JsonProperty("language")
	private String language;

	@JsonProperty("prompt")
	private String prompt;

	public InputAudioTranscriptionObj() {
		this.model = "whisper-1";
		this.language = "en";
		this.prompt = "";
	}

}
