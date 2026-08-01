package ai.z.openapi.service.batches;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

	@JsonProperty("id")
	private String id;

	@JsonProperty("completion_window")
	private String completionWindow;

	@JsonProperty("created_at")
	private Long createdAt;

	@JsonProperty("endpoint")
	private String endpoint;

	@JsonProperty("input_file_id")
	private String inputFileId;

	@JsonProperty("object")
	private String object;

	@JsonProperty("status")
	private String status;

	@JsonProperty("cancelled_at")
	private Long cancelledAt;

	@JsonProperty("cancelling_at")
	private Long cancellingAt;

	@JsonProperty("completed_at")
	private Long completedAt;

	@JsonProperty("error_file_id")
	private String errorFileId;

	@JsonProperty("errors")
	private Errors errors;

	@JsonProperty("expired_at")
	private Long expiredAt;

	@JsonProperty("expires_at")
	private Long expiresAt;

	@JsonProperty("failed_at")
	private Long failedAt;

	@JsonProperty("finalizing_at")
	private Long finalizingAt;

	@JsonProperty("in_progress_at")
	private Long inProgressAt;

	@JsonProperty("metadata")
	private Object metadata;

	@JsonProperty("output_file_id")
	private String outputFileId;

	@JsonProperty("request_counts")
	private BatchRequestCounts requestCounts;

}

@Data
class Errors {

	@JsonProperty("data")
	private List<BatchError> data;

	@JsonProperty("object")
	private String object;

}

@Data
class BatchRequestCounts {

	@JsonProperty("completed")
	private int completed;

	@JsonProperty("failed")
	private int failed;

	@JsonProperty("total")
	private int total;

}

@Data
class BatchError {

	@JsonProperty("code")
	private String code;

	@JsonProperty("line")
	private Long line;

	@JsonProperty("message")
	private String message;

	@JsonProperty("param")
	private String param;

}
