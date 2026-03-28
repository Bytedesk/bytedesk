package ai.z.openapi.service.voiceclone;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request parameters for voice list API. This class contains optional filter parameters
 * for retrieving voice clones by type and name.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceListRequest extends CommonRequest implements ClientRequest<VoiceListRequest> {

	/** Voice type filter，"PRIVATE" or "OFFICIAL" or null */
	@JsonProperty("voice_type")
	private String voiceType;

	/** Voice name filter */
	@JsonProperty("voice_name")
	private String voiceName;

}