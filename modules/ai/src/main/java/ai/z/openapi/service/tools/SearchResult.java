package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a search result item from web search operations. This class contains details
 * about search results including title, link, content, and metadata.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

	/**
	 * Search round index, defaults to 0.
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * Title of the search result.
	 */
	@JsonProperty("title")
	private String title;

	/**
	 * URL link of the search result.
	 */
	@JsonProperty("link")
	private String link;

	/**
	 * Content or snippet of the search result.
	 */
	@JsonProperty("content")
	private String content;

	/**
	 * Icon URL of the search result.
	 */
	@JsonProperty("icon")
	private String icon;

	/**
	 * Source media of the search result.
	 */
	@JsonProperty("media")
	private String media;

	/**
	 * Reference number, e.g. [ref_1].
	 */
	@JsonProperty("refer")
	private String refer;

}
