package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebSearchParamsRequest extends CommonRequest implements ClientRequest<WebSearchParamsRequest> {

	/**
	 * Tool name: web-search-pro parameter type definition Attributes: Model name
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * Whether streaming
	 */
	@JsonProperty("stream")
	private Boolean stream;

	/**
	 * Content containing historical conversation context, passed in JSON array format
	 * like {"role": "user", "content": "hello"} Current version only supports User
	 * Message single-turn conversation, the tool will understand User Message and perform
	 * search, Please try to pass in user's original questions without instruction format
	 * to improve search accuracy.
	 */
	@JsonProperty("messages")
	private List<SearchChatMessage> messages;

	/**
	 * Specify search scope, web-wide, academic, etc., default is web-wide
	 */
	@JsonProperty("scope")
	private String scope;

	/**
	 * Specify search user location to improve relevance
	 */
	@JsonProperty("location")
	private String location;

	/**
	 * Support specifying search results updated within N days (1-30)
	 */
	@JsonProperty("recent_days")
	private Integer recentDays;

}
