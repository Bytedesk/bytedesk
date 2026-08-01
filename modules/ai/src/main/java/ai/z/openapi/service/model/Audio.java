package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audio {

	private String id;

	private String data;

	@JsonProperty("expires_at")
	private Long expiresAt;

}
