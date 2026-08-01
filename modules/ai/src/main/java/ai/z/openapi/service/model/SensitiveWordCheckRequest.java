package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request parameters for sensitive word checking. This class defines the configuration
 * for sensitive word detection and filtering.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SensitiveWordCheckRequest {

	/**
	 * Type of sensitive words to check. Currently only supports "ALL". Available values:
	 * ALL (all types)
	 */
	private String type;

	/**
	 * Status of sensitive word checking. Available values: - ENABLE: Enable sensitive
	 * word checking - DISABLE: Disable sensitive word checking
	 *
	 * Note: Sensitive word checking is enabled by default. To disable it, you need to
	 * contact business support to obtain the corresponding permissions, otherwise the
	 * disable setting will not take effect.
	 */
	private String status;

}
