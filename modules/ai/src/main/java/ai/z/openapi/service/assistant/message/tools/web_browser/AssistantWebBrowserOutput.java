package ai.z.openapi.service.assistant.message.tools.web_browser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the output of a web browser search result.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantWebBrowserOutput {

	/**
	 * The title of the search result.
	 */
	@JsonProperty("title")
	private String title;

	/**
	 * The URL link to the search result's webpage.
	 */
	@JsonProperty("link")
	private String link;

	/**
	 * The textual content extracted from the search result.
	 */
	@JsonProperty("content")
	private String content;

	/**
	 * Any error message encountered during the search or retrieval process.
	 */
	@JsonProperty("error_msg")
	private String errorMsg;

}
