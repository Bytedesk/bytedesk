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

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AudioSpeechRequest extends CommonRequest implements ClientRequest<AudioSpeechRequest> {

	/**
	 * Model code to call
	 */
	private String model;

	/**
	 * Text to generate speech from
	 */
	private String input;

	/**
	 * Voice tone for speech generation
	 */
	private String voice;

	/**
	 * Format of the generated speech file
	 */
	@JsonProperty("response_format")
	private String responseFormat;

	/**
	 * Encode format of the generated speech file
	 */
	@JsonProperty("encode_format")
	private String encodeFormat;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

	/**
	 * Forced watermark switch
	 */
	@JsonProperty("watermark_enabled")
	private Boolean watermarkEnabled;

	/**
	 * Is streaming response
	 */
	private Boolean stream;

	/**
	 * Voice speed for speech generation
	 */
	private Float speed;

	/**
	 * Volume of the generated speech file
	 */
	private Float volume;

}
