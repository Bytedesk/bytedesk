package ai.z.openapi.service.assistant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Extra parameters for assistant configuration. This class contains additional optional
 * parameters that can be used to customize assistant behavior and functionality.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantExtraParameters {

	/**
	 * Translation agent parameters
	 */
	private AssistantTranslateParameters translate;

}
