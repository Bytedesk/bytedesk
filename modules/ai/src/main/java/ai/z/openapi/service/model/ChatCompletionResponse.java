package ai.z.openapi.service.model;

import ai.z.openapi.core.model.FlowableClientResponse;
import io.reactivex.rxjava3.core.Flowable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionResponse implements FlowableClientResponse<ModelData> {

	private int code;

	private String msg;

	private boolean success;

	private ModelData data;

	private Flowable<ModelData> flowable;

	private ChatError error;

}
