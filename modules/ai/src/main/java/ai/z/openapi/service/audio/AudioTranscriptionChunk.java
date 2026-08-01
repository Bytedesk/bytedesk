package ai.z.openapi.service.audio;

import ai.z.openapi.service.model.Choice;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class AudioTranscriptionChunk {

	@JsonProperty("choices")
	private List<Choice> choices;

	private Long created;

	private String model;

	private String id;

	private String type;

	private String delta;

}
