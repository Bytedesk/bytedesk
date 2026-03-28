package ai.z.openapi.service.model;

import ai.z.openapi.service.tools.DocReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents tool calls made by the model during conversation. This class contains
 * information about function calls including the function details, unique identifier, and
 * call type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToolCalls {

	@JsonProperty("function")
	private ChatFunctionCall function;

	/**
	 * Unique identifier of the function call.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * Type of tool called by the model, currently only supports 'function', 'mcp'.
	 */
	@JsonProperty("type")
	private String type;

	@JsonProperty("mcp")
	private MCPToolCall mcp;

	@JsonProperty("doc_reference")
	private List<DocReference> docReferenceList;

}
