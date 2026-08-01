package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputAudio {

	/**
	 * audio data base64_string
	 */
	private String data;

	/**
	 * wav
	 */
	private String format;

}
