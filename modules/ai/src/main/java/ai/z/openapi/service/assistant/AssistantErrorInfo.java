package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents error information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantErrorInfo {

	/**
	 * Error code.
	 */
	@JsonProperty("code")
	private String code;

	/**
	 * Error message.
	 */
	@JsonProperty("message")
	private String message;

}
