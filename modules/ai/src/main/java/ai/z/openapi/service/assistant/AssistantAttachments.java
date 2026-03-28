package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents an attachment with a file ID.
 */
public class AssistantAttachments {

	/**
	 * The ID of the file attachment.
	 */
	@JsonProperty("file_id")
	private String fileId;

	// Getters and Setters

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
