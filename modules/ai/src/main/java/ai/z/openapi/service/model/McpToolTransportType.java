package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum McpToolTransportType {

	SSE("sse", "SSE"), STREAMABLE_HTTP("streamable-http", "streamable http");

	private final String code;

	private final String value;

}
