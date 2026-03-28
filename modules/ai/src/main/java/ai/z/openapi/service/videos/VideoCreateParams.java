package ai.z.openapi.service.videos;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Parameters for video creation API calls. This class contains all the necessary
 * parameters for generating videos, including model selection, prompts, image inputs, and
 * various video settings.
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Data
public class VideoCreateParams implements ClientRequest<VideoCreateParams> {

	/**
	 * Model ID
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Model name
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * Text description of the required video
	 */
	@JsonProperty("prompt")
	private String prompt;

	/**
	 * Supports URL or Base64, input image for image-to-video generation Image format:
	 * Image size:
	 */
	@JsonProperty("image_url")
	private Object imageUrl;

	/**
	 * Call specified model to optimize the prompt, recommend using GLM-4-Air and
	 * GLM-4-Flash. If not specified, use the original prompt directly.
	 */
	@JsonProperty("prompt_opt_model")
	private String promptPptModel;

	/**
	 * Passed by the client, must ensure uniqueness; used to distinguish the unique
	 * identifier of each request, the platform will generate by default when the client
	 * does not pass.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * User ID
	 */
	@JsonProperty("user_id")
	private String userId;

	/**
	 * Video quality setting
	 */
	@JsonProperty("quality")
	private String quality;

	/**
	 * Whether to include audio in the generated video
	 */
	@JsonProperty("with_audio")
	private Boolean withAudio;

	/**
	 * Video size/resolution
	 */
	@JsonProperty("size")
	private String size;

	/**
	 * style anime general
	 */
	@JsonProperty("style")
	private String style;

	/**
	 * Video duration in seconds
	 */
	@JsonProperty("duration")
	private Integer duration;

	/**
	 * Frames per second for the video
	 */
	@JsonProperty("fps")
	private Integer fps;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

	@JsonProperty("movement_amplitude")
	private String movementAmplitude;

	/**
	 * 16:9 : 16:9、9:16、1:1
	 */
	@JsonProperty("aspect_ratio")
	private String aspectRatio;

	/**
	 * Forced watermark switch
	 */
	@JsonProperty("watermark_enabled")
	private Boolean watermarkEnabled;

	/**
	 * Is it necessary to perform off peak execution
	 */
	@JsonProperty("off_peak")
	private Boolean offPeak;

}