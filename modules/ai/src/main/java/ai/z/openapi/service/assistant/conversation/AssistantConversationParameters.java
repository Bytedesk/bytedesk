package ai.z.openapi.service.assistant.conversation;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This class represents the parameters for a conversation, including pagination.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantConversationParameters extends CommonRequest
		implements ClientRequest<AssistantConversationParameters> {

	/**
	 * The Assistant ID.
	 */
	@JsonProperty("assistant_id")
	private String assistantId;

	/**
	 * The current page number for pagination.
	 */
	@JsonProperty("page")
	private int page;

	/**
	 * The number of items per page for pagination.
	 */
	@JsonProperty("page_size")
	private int pageSize;

}
