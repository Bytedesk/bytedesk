package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatThinkingType {

	ENABLED,

	DISABLED,;

	@JsonValue
	public String value() {
		return this.name().toLowerCase();
	}

}
