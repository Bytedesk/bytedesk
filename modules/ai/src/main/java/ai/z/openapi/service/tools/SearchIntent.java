package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents search intent information for web search operations. This class contains
 * details about search queries, intent analysis, and keywords.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchIntent {

	/**
	 * Search round index, defaults to 0.
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * Optimized search query.
	 */
	@JsonProperty("query")
	private String query;

	/**
	 * Determined intent type.
	 */
	@JsonProperty("intent")
	private String intent;

	/**
	 * Search keywords.
	 */
	@JsonProperty("keywords")
	private String keywords;

}
