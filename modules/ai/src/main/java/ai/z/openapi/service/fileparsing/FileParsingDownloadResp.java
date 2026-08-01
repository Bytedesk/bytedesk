package ai.z.openapi.service.fileparsing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * File parsing result response DTO
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingDownloadResp {

	/**
	 * Parsing task ID
	 */
	@JsonProperty("task_id")
	private String taskId;

	/**
	 * Result status (e.g., succeeded, failed, etc.)
	 */
	@JsonProperty("status")
	private String status;

	/**
	 * Response message
	 */
	@JsonProperty("message")
	private String message;

	/**
	 * Parsed result content
	 */
	@JsonProperty("content")
	private String content;

	/**
	 * Parsing result download link (if available)
	 */
	@JsonProperty("parsing_result_url")
	private String parsingResultUrl;

}