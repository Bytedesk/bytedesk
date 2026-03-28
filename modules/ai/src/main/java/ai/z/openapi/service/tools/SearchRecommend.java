package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents search query recommendations. This class contains recommended search queries
 * based on search context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRecommend {

	/**
	 * Search round index, defaults to 0.
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * Recommended search query.
	 */
	@JsonProperty("query")
	private String query;

}
