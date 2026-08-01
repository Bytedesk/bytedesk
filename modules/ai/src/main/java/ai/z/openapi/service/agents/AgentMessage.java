package ai.z.openapi.service.agents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentMessage {

	private String role;

	/**
	 * list AgentContent or One AgentContent
	 */
	private Object content;

}
