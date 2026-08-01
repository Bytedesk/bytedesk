package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This class represents the text content of a message. Currently supports only type =
 * text.
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantMessageTextContent {

	/**
	 * The type of the message content, currently only "text" is supported.
	 */
	@JsonProperty("type")
	private String type;

	/**
	 * The text content of the message.
	 */
	@JsonProperty("text")
	private String text;

}
