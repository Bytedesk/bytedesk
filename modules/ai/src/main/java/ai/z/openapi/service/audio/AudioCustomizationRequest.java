package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.File;

/**
 * Request parameters for audio customization API calls. This class contains all the
 * necessary parameters for generating customized audio, including input text, model
 * selection, voice cloning data, and response format options.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AudioCustomizationRequest extends CommonRequest implements ClientRequest<AudioCustomizationRequest> {

	/**
	 * Text to generate audio from
	 */
	private String input;

	/**
	 * Model code to call
	 */
	private String model;

	/**
	 * Text description of the original audio to clone
	 */
	@JsonProperty("voice_text")
	private String voiceText;

	/**
	 * Original audio file to clone
	 */
	@JsonProperty("voice_data")
	private File voiceData;

	/**
	 * Audio response format
	 */
	@JsonProperty("response_format")
	private String responseFormat;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

}
