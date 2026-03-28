package ai.z.openapi.service.assistant.query_support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * This class represents the response containing a list of assistant supports.
 */
@Data
public class AssistantSupportStatus {

	/**
	 * The response code.
	 */
	@JsonProperty("code")
	private Integer code;

	/**
	 * The response message.
	 */
	@JsonProperty("msg")
	private String msg;

	/**
	 * The list of assistant supports.
	 */
	@JsonProperty("data")
	private List<AssistantSupport> data;

}
