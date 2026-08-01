package ai.z.openapi.service.agents;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import ai.z.openapi.service.model.SensitiveWordCheckRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Request parameters for agent completion API calls. This class contains all the
 * necessary parameters to initiate an agent completion request, including agent ID,
 * messages, streaming options, and custom variables.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentsCompletionRequest extends CommonRequest implements ClientRequest<AgentsCompletionRequest> {

	/**
	 * Agent ID
	 */
	@JsonProperty("agent_id")
	private String agentId;

	/**
	 * Message body
	 */
	private List<AgentMessage> messages;

	/**
	 * Synchronous call: false, SSE call: true
	 */
	private Boolean stream;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

	/**
	 * Agent business fields
	 * @return
	 */
	@JsonProperty("custom_variables")
	private ObjectNode customVariables;

}
