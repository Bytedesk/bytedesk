package ai.z.openapi.service.file;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class QueryFileApiResponse implements ClientResponse<QueryFileResult> {

	private int code;

	private String msg;

	private boolean success;

	private QueryFileResult data;

	private ChatError error;

}
