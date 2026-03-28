package ai.z.openapi.service.file;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Create file upload request
 *
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUploadParams extends CommonRequest implements ClientRequest<FileUploadParams> {

	/**
	 * The purpose of the file
	 */
	private String purpose;

	/**
	 * local file
	 */
	private String filePath;

}
