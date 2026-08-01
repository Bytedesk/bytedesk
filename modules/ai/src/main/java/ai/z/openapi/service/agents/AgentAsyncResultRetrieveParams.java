package ai.z.openapi.service.agents;

import ai.z.openapi.core.model.ClientRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Parameters for retrieving agent asynchronous task results. This class contains the
 * necessary parameters to query the result of an agent's async task.
 */
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentAsyncResultRetrieveParams implements ClientRequest<Map<String, Object>> {

	/**
	 * The ID of the agent async task to retrieve
	 */
	@JsonProperty("task_id")
	private String taskId;

	/**
	 * The agent ID associated with the async task
	 */
	@JsonProperty("agent_id")
	private String agentId;

	/**
	 * Optional request ID for tracking
	 */
	@JsonProperty("request_id")
	private String requestId;

}
