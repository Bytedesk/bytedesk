package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientResponse;
import ai.z.openapi.service.batches.BatchPage;
import ai.z.openapi.service.model.ChatError;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response wrapper for voice cloning API operations. This class contains the standard
 * response structure including status code, message, success flag, result data, and error
 * information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceCloneResponse implements ClientResponse<VoiceCloneResult> {

	private int code;

	private String msg;

	private boolean success;

	private VoiceCloneResult data;

	private ChatError error;

}
