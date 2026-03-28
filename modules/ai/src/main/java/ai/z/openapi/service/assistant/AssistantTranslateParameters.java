package ai.z.openapi.service.assistant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Parameters for translation operations. This class contains the source and target
 * language settings for translation requests.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantTranslateParameters {

	/**
	 * Source language for translation.
	 */
	private String from;

	/**
	 * Target language for translation.
	 */
	private String to;

}
