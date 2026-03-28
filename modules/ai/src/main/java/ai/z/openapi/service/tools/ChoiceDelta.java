package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceDelta {

	/**
	 * Role
	 */
	@JsonProperty("role")
	private String role;

	/**
	 * Tool calls list
	 */
	@JsonProperty("tool_calls")
	private List<ChoiceDeltaToolCall> toolCalls;

}
