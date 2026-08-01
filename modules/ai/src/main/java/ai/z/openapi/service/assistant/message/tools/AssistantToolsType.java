package ai.z.openapi.service.assistant.message.tools;

import ai.z.openapi.service.assistant.message.tools.code_interpreter.AssistantCodeInterpreterToolBlock;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ai.z.openapi.service.assistant.message.tools.drawing_tool.AssistantDrawingToolBlock;
import ai.z.openapi.service.assistant.message.tools.function.AssistantFunctionToolBlock;
import ai.z.openapi.service.assistant.message.tools.retrieval.AssistantRetrievalToolBlock;
import ai.z.openapi.service.assistant.message.tools.web_browser.AssistantWebBrowserToolBlock;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({ @JsonSubTypes.Type(value = AssistantWebBrowserToolBlock.class, name = "web_browser"),
		@JsonSubTypes.Type(value = AssistantRetrievalToolBlock.class, name = "retrieval"),
		@JsonSubTypes.Type(value = AssistantFunctionToolBlock.class, name = "function"),
		@JsonSubTypes.Type(value = AssistantCodeInterpreterToolBlock.class, name = "code_interpreter"),
		@JsonSubTypes.Type(value = AssistantDrawingToolBlock.class, name = "drawing_tool") })
public abstract class AssistantToolsType {

}
