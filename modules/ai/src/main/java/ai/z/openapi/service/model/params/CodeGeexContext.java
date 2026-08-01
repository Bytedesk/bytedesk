package ai.z.openapi.service.model.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeGeexContext {

	@JsonProperty("path")
	private String path;

	@JsonProperty("code")
	private String code;

}
