package ai.z.openapi.service.fileparsing;

import ai.z.openapi.core.model.ClientRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * File parsing task upload request parameters
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileParsingUploadReq implements ClientRequest<FileParsingUploadReq> {

	/**
	 * Local file path
	 */
	private String filePath;

	/**
	 * Tool type, e.g. "lite"
	 */
	private String toolType;

	/**
	 * File type, e.g. "pdf", "doc", etc.
	 */
	private String fileType;

}