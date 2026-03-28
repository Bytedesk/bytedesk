package ai.z.openapi.service.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * A request to create an image based on a prompt All fields except prompt are optional
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageRequest extends CommonRequest implements ClientRequest<CreateImageRequest> {

	/**
	 * A text description of the desired image(s). The maximum length is 1000 characters
	 * for dall-e-2 and 4000 characters for dall-e-3.
	 */
	private String prompt;

	/**
	 * The model to use for image generation.
	 */
	private String model;

	/**
	 * The size of the image to generate.
	 */
	private String size;

	/**
	 * Optional. The quality of the generated image. hd: Generates more refined images
	 * with richer details and higher overall consistency, but takes longer. standard:
	 * Quickly generates images, suitable for scenarios with high speed requirements,
	 * takes less time.
	 */
	private String quality;

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

}
