package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request parameters for voice deletion API. This class contains the necessary parameters
 * for deleting a voice, specifically the voice.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceDeleteRequest extends CommonRequest implements ClientRequest<VoiceDeleteRequest> {

	@JsonProperty("voice")
	private String voice;

}
