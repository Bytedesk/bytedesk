package ai.z.openapi.service.model;

import ai.z.openapi.service.tools.KnowledgeV2Result;
import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.service.web_search.WebSearchResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class ModelData {

	@JsonProperty("choices")
	private List<Choice> choices;

	@JsonProperty("usage")
	private Usage usage;

	@JsonProperty("request_id")
	private String requestId;

	@JsonProperty("task_status")
	private TaskStatus taskStatus;

	private Long created;

	private String model;

	private String id;

	@JsonProperty("agent_id")
	private String agentId;

	@JsonProperty("conversation_id")
	private String conversationId;

	@JsonProperty("async_id")
	private String asyncId;

	private String status;

	@JsonProperty("web_search")
	private List<WebSearchResp> webSearch;

	private String type;

	private String text;

	private List<Segment> segments;

	private String delta;

	private KnowledgeV2Result knowledgeV2;

}
