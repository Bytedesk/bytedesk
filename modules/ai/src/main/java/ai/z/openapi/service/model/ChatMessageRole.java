package ai.z.openapi.service.model;

public enum ChatMessageRole {

	SYSTEM("system"), USER("user"), ASSISTANT("assistant"), FUNCTION("function");

	private final String value;

	ChatMessageRole(final String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

}
