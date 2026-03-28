package ai.z.openapi.service.voiceclone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Voice data entity containing voice information. This class contains voice details
 * including voice, name, type, download URL, and creation time.
 */
@Data
public class VoiceData {

	@JsonProperty("voice")
	private String voice;

	@JsonProperty("voice_name")
	private String voiceName;

	@JsonProperty("voice_type")
	private String voiceType;

	@JsonProperty("download_url")
	private String downloadUrl;

	@JsonProperty("create_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

}
