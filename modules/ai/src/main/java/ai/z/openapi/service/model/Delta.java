package ai.z.openapi.service.model;

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
public class Delta {

	private String role;

	private String content;

	@JsonProperty("reasoning_content")
	private String reasoningContent;

	private Audio audio;

	@JsonProperty("tool_calls")
	private List<ToolCalls> tool_calls;

}
