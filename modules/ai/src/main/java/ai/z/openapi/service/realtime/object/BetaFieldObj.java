package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class BetaFieldObj {

	@JsonProperty("chat_mode")
	private String chatMode;

	@JsonProperty("tts_source")
	private String ttsSource;

	@JsonProperty("auto_search")
	private boolean autoSearch;

	public BetaFieldObj() {
		this.chatMode = "audio";
		this.ttsSource = "e2e";
		this.autoSearch = true;
	}

}
