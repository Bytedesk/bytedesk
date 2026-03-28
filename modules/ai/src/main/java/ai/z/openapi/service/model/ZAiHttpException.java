package ai.z.openapi.service.model;

public class ZAiHttpException extends RuntimeException {

	/**
	 * HTTP status code
	 */
	public final int statusCode;

	/**
	 * ZAI error code, for example "1233"
	 */
	public final String code;

	/**
	 * ZAI error message
	 */
	public final String msg;

	public ZAiHttpException(ZAiError error, Exception parent, int statusCode) {
		super(error.error.message, parent);
		this.statusCode = statusCode;
		this.code = error.error.code;
		this.msg = error.error.message;
	}

	public ZAiHttpException(String errorMsg, String errorCode, Exception parent, int statusCode) {
		super(errorMsg, parent);
		this.statusCode = statusCode;
		this.code = errorCode;
		this.msg = errorMsg;
	}

}
