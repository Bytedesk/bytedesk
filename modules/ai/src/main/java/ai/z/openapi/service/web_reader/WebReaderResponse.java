package ai.z.openapi.service.web_reader;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class WebReaderResponse implements ClientResponse<WebReaderResult> {

	private int code;

	private String msg;

	private boolean success;

	private WebReaderResult data;

	private ChatError error;

}
