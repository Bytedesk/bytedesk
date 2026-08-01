package ai.z.openapi.service.model;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Chat request wrapper that includes custom headers support. This class wraps the
 * original ChatCompletionCreateParams and adds custom headers functionality.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestWithHeaders extends CommonRequest implements ClientRequest<ChatRequestWithHeaders> {

	/**
	 * The original chat completion request parameters
	 */
	private ChatCompletionCreateParams request;

	/**
	 * Custom headers to be added to the HTTP request
	 */
	private Map<String, String> customHeaders;

}