package ai.z.openapi.service.batches;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Parameters for creating batch processing requests. This class contains all the
 * necessary parameters to create a batch job, including completion window, endpoint,
 * input file, and metadata.
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchCreateParams implements ClientRequest<BatchCreateParams> {

	/**
	 * The time frame within which the batch should be processed.
	 */
	@JsonProperty("completion_window")
	private String completionWindow;

	/**
	 * The API endpoint to be used for batch processing.
	 */
	@JsonProperty("endpoint")
	private String endpoint;

	/**
	 * The ID of the uploaded file containing batch requests. Must be the ID of the
	 * uploaded file.
	 */
	@JsonProperty("input_file_id")
	private String inputFileId;

	/**
	 * Optional custom metadata for the batch job.
	 */
	@JsonProperty("metadata")
	private Map<String, String> metadata;

}
