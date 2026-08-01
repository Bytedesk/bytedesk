package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCPToolCall implements Serializable {

	private static final long serialVersionUID = 2080214859980927710L;

	// Unique identifier for MCP tool call
	private String id;

	private String type;

	private String server_label;

	private String error;

	// type = mcp_list_tools
	private List<MCPToolDefinition> tools;

	// type = mcp_call
	// Tool call parameters, parameters as JSON string
	private String arguments;

	// Tool name
	private String name;

	// Tool result output
	private Object output;

}