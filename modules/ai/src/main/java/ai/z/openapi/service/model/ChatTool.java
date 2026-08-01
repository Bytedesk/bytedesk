package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTool {

	private String type;

	@JsonProperty("function")
	private ChatFunction function;

	@JsonProperty("retrieval")
	private Retrieval retrieval;

	@JsonProperty("web_search")
	private WebSearch webSearch;

	@JsonProperty("mcp")
	private MCPTool mcp;

}
