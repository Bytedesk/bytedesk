package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.model.ChatError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response wrapper for voice list API operations. This class contains the standard
 * response structure including status code, message, success flag, result data, and error
 * information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceListResponse implements ClientResponse<VoiceListResult> {

	private int code;

	private String msg;

	private boolean success;

	private VoiceListResult data;

	private ChatError error;

}
