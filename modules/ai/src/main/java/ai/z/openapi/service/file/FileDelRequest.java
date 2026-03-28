package ai.z.openapi.service.file;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.batches.BatchRequest;
import ai.z.openapi.service.model.ChatError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileDelRequest implements ClientRequest<FileDelRequest> {

	private String fileId;

}
