package ai.z.openapi.service.assistant.message.tools.web_browser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents the input and outputs of a web browser search.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantWebBrowser {

	/**
	 * The input query for the web browser search.
	 */
	@JsonProperty("input")
	private String input;

	/**
	 * A list of search results returned by the web browser.
	 */
	@JsonProperty("outputs")
	private List<AssistantWebBrowserOutput> outputs;

}
