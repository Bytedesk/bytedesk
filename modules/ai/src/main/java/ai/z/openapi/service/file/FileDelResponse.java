package ai.z.openapi.service.file;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class FileDelResponse implements ClientResponse<FileDeleted> {

	private int code;

	private String msg;

	private boolean success;

	private FileDeleted data;

	private ChatError error;

}
