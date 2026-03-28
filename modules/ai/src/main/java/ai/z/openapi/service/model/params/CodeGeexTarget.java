package ai.z.openapi.service.model.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeGeexTarget {

	@JsonProperty("path")
	private String path;

	@JsonProperty("language")
	private String language;

	@JsonProperty("code_prefix")
	private String codePrefix;

	@JsonProperty("code_suffix")
	private String codeSuffix;

}
