package ai.z.openapi.service.audio;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

import java.io.File;

@Data
public class AudioSpeechResponse implements ClientResponse<File> {

	private int code;

	private String msg;

	private boolean success;

	private File data;

	private ChatError error;

}
