package ai.z.openapi.service.videos;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.Data;

@Data
public class VideosResponse implements ClientResponse<VideoObject> {

	private int code;

	private String msg;

	private boolean success;

	private VideoObject data;

	private ChatError error;

}
