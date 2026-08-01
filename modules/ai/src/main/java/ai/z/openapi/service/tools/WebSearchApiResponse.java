package ai.z.openapi.service.tools;

import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.service.model.ChatError;
import io.reactivex.rxjava3.core.Flowable;
import lombok.Data;

@Data
public class WebSearchApiResponse implements FlowableClientResponse<WebSearchPro> {

	private int code;

	private String msg;

	private boolean success;

	private WebSearchPro data;

	private ChatError error;

	private Flowable<WebSearchPro> flowable;

}
