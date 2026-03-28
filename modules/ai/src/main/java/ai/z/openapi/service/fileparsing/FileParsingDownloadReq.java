package ai.z.openapi.service.fileparsing;

import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * File parsing result download request parameters
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingDownloadReq implements ClientRequest<FileParsingDownloadReq> {

	/**
	 * Parsing task ID (required)
	 */
	private String taskId;

	/**
	 * Returned content format type (e.g., "download_link", "txt", required)
	 */
	private String formatType;

}