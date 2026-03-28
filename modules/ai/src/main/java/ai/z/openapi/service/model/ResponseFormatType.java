package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseFormatType {

	TEXT,

	JSON_OBJECT,;

	@JsonValue
	public String value() {
		return this.name().toLowerCase();
	}

}
