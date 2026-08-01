package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result data for voice cloning operations. This class contains the voice cloning result
 * information including voice, audio file ID, and file purpose.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoiceCloneResult {

	/** Voice */
	@JsonProperty("voice")
	private String voice;

	/** Audio preview file ID */
	@JsonProperty("file_id")
	private String fileId;

	/** File purpose */
	@JsonProperty("file_purpose")
	private String filePurpose;

}
