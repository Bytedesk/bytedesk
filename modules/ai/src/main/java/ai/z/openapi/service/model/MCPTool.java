package ai.z.openapi.service.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCPTool {

	/**
	 * Identifier for the MCP server, used to distinguish different MCP servers, required
	 */
	private String server_label;

	/**
	 * URL of the MCP server, optional Default (if this field is empty): use server_label
	 * as mcpCode to connect to Zhipu AI's MCP servers
	 */
	private String server_url;

	/**
	 * Transport method for MCP calls: sse/streamable-http, defaults to streamable-http
	 */
	private String transport_type;

	/**
	 * List of allowed tools to call, defaults to empty (allowing all tools)
	 */
	private Set<String> allowed_tools;

	/**
	 * Headers for connecting to MCP server, used for authentication
	 */
	private Map<String, String> headers;

}
