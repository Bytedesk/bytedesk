package ai.z.openapi.service.file;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Query file list
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileListParams extends CommonRequest implements ClientRequest<FileListParams> {

	private String purpose;

	private Integer limit;

	private String after;

	private String order;

}
