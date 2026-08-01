package ai.z.openapi.service.model;

import ai.z.openapi.core.model.ClientResponse;
import lombok.Data;

@Data
public class QueryModelResultResponse implements ClientResponse<ModelData> {

	private int code;

	private String msg;

	private boolean success;

	private ModelData data;

	private ChatError error;

}
