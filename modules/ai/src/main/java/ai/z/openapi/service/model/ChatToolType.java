package ai.z.openapi.service.model;

public enum ChatToolType {

	WEB_SEARCH("web_search"),

	RETRIEVAL("retrieval"),

	FUNCTION("function"),

	MCP("mcp"),;

	private final String value;

	ChatToolType(final String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

}
