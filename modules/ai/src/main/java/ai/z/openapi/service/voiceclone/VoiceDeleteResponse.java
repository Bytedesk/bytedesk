package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response wrapper for voice deletion API operations. This class contains the standard
 * response structure including status code, message, success flag, result data, and error
 * information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceDeleteResponse implements ClientResponse<VoiceDeleteResult> {

	private int code;

	private String msg;

	private boolean success;

	private VoiceDeleteResult data;

	private ChatError error;

}
