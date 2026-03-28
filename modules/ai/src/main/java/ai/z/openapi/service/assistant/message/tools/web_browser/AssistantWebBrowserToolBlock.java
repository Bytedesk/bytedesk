package ai.z.openapi.service.assistant.message.tools.web_browser;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a block for invoking the web browser tool.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantWebBrowserToolBlock extends AssistantToolsType {

	/**
	 * The index of the tool call.
	 */
	@JsonProperty("index")
	private Integer index;

	/**
	 * The unique identifier of the tool call.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * An instance of the WebBrowser class containing the search input and outputs.
	 */
	@JsonProperty("web_browser")
	private AssistantWebBrowser webBrowser;

	/**
	 * The type of tool being used, always set to "web_browser".
	 */
	@JsonProperty("type")
	@Builder.Default
	private String type = "web_browser";

}
