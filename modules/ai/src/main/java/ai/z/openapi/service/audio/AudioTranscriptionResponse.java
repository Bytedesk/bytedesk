package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.BiFlowableClientResponse;
import ai.z.openapi.service.model.ChatError;
import io.reactivex.rxjava3.core.Flowable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioTranscriptionResponse
		implements BiFlowableClientResponse<AudioTranscriptionResult, AudioTranscriptionChunk> {

	private int code;

	private String msg;

	private boolean success;

	private AudioTranscriptionResult data;

	private Flowable<AudioTranscriptionChunk> flowable;

	private ChatError error;

}
