package ai.z.openapi.service.image;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result object for async image generation API. Contains task status and image results
 * when completed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncImageResult {

	/**
	 * The unique identifier for the async task.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * The model used for image generation.
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * The request ID for tracking.
	 */
	@JsonProperty("request_id")
	private String requestId;

	/**
	 * The status of the async task. Possible values: PROCESSING, SUCCESS, FAIL
	 */
	@JsonProperty("task_status")
	private String taskStatus;

	/**
	 * List of generated images. Available when task_status is SUCCESS.
	 */
	@JsonProperty("image_result")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<Image> imageResult;

}
