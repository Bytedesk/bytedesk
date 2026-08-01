package ai.z.openapi.service.file;

import ai.z.openapi.service.model.ChatError;
import lombok.Data;

/**
 * Represents the result of a file deletion operation. Contains information about whether
 * the file was successfully deleted.
 */
@Data
public class FileDeleted {

	/**
	 * The ID of the deleted file.
	 */
	private String id;

	/**
	 * Indicates whether the file was successfully deleted.
	 */
	private boolean deleted;

	/**
	 * The object type, always "file".
	 */
	private final String object = "file";

	/**
	 * Error information if the deletion failed.
	 */
	private ChatError error;

}
