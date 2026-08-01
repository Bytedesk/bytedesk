package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatThinking {

	/**
	 * Model thinking type Only support by GLM-4.5 and above models.<br>
	 * Value: enabled, disabled <br>
	 * Control whether the model enable the chain of thought.<br>
	 * When enabled, GLM-4.5 will automatically determine whether to think, while GLM-4.5V
	 * will think compulsorily.
	 */
	private String type;

}
