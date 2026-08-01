package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an error that occurred during API operations. Contains error code and
 * descriptive message for troubleshooting.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatError {

	/**
	 * Error code indicating the type of error.
	 */
	private Integer code;

	/**
	 * Descriptive error message.
	 */
	private String message;

}
