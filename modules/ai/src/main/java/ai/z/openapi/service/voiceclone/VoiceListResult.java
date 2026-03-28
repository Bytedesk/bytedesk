package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Result data for voice list operations. This class contains the list of voice data
 * returned from voice listing API calls.
 */
@Data
public class VoiceListResult {

	@JsonProperty("voice_list")
	private List<VoiceData> voiceList;

}
