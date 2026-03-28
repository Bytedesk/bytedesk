package ai.z.openapi.service.layoutparsing;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class LayoutParsingResponse implements ClientResponse<LayoutParsingResult> {

	private int code;

	private String msg;

	private boolean success;

	private LayoutParsingResult data;

	private ChatError error;

}
