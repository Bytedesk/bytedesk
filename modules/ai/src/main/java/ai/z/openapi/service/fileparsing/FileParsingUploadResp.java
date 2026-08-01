package ai.z.openapi.service.fileparsing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * File Parsing Task Upload Response DTO Compatible with multiple response structures
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingUploadResp {

	/**
	 * Task ID (API field: task_id or taskId)
	 */
	private String taskId;

	/**
	 * Return message (API field: message)
	 */
	private String message;

}