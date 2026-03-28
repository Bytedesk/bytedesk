package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.core.model.FlowableClientResponse;
import ai.z.openapi.service.model.ChatError;
import java.io.File;

import ai.z.openapi.service.model.ModelData;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.reactivex.rxjava3.core.Flowable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioSpeechStreamingResponse implements FlowableClientResponse<ModelData> {

	private int code;

	private String msg;

	private boolean success;

	private ModelData data;

	private ChatError error;

	private Flowable<ModelData> flowable;

}
