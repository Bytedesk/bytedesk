package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an error object containing error details. This object provides information
 * about errors that occur during realtime operations.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ErrorObj {

	/**
	 * The error code identifying the type of error.
	 */
	@JsonProperty("code")
	private String code;

	/**
	 * A human-readable error message describing the error.
	 */
	@JsonProperty("message")
	private String message;

	/**
	 * The parameter that caused the error, if applicable.
	 */
	@JsonProperty("param")
	private String param;

	/**
	 * The type of error that occurred.
	 */
	@JsonProperty("type")
	private String type;

	public ErrorObj() {
		this.code = "";
		this.message = "";
		this.param = "";
		this.type = "";
	}

}
