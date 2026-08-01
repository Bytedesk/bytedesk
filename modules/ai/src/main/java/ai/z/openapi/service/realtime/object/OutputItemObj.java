package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class OutputItemObj {

	@JsonProperty("id")
	private String id;

	@JsonProperty("role")
	private String role;

	@JsonProperty("status")
	private String status;

	@JsonProperty("type")
	private String type;

	@JsonProperty("content")
	private List<OutputMessage> content;

	@JsonProperty("file_search_tool_call")
	private FileSearchToolCall fileSearchToolCall;

	@JsonProperty("function_tool_call")
	private FunctionToolCall functionToolCall;

	@JsonProperty("web_search_tool_call")
	private WebSearchToolCall webSearchToolCall;

	@JsonProperty("computer_tool_call")
	private ComputerToolCall computerToolCall;

	@JsonProperty("reasoning")
	private Reasoning reasoning;

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class OutputMessage {

		@JsonProperty("id")
		private String id;

		@JsonProperty("role")
		private String role;

		@JsonProperty("status")
		private String status;

		@JsonProperty("type")
		private String type;

		@JsonProperty("content")
		private List<String> content;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class FileSearchToolCall {

		@JsonProperty("id")
		private String id;

		@JsonProperty("queries")
		private List<String> queries;

		@JsonProperty("status")
		private String status;

		@JsonProperty("type")
		private String type;

		@JsonProperty("results")
		private List<FileSearchResult> results;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class FileSearchResult {

		@JsonProperty("attributes")
		private Map<String, Object> attributes;

		@JsonProperty("file_id")
		private String fileId;

		@JsonProperty("filename")
		private String filename;

		@JsonProperty("score")
		private Double score;

		@JsonProperty("text")
		private String text;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class FunctionToolCall {

		@JsonProperty("arguments")
		private String arguments;

		@JsonProperty("call_id")
		private String callId;

		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("type")
		private String type;

		@JsonProperty("status")
		private String status;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class WebSearchToolCall {

		@JsonProperty("id")
		private String id;

		@JsonProperty("status")
		private String status;

		@JsonProperty("type")
		private String type;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class ComputerToolCall {

		@JsonProperty("action")
		private Object action;

		@JsonProperty("call_id")
		private String callId;

		@JsonProperty("id")
		private String id;

		@JsonProperty("pending_safety_checks")
		private List<PendingSafetyCheck> pendingSafetyChecks;

		@JsonProperty("status")
		private String status;

		@JsonProperty("type")
		private String type;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class PendingSafetyCheck {

		@JsonProperty("code")
		private String code;

		@JsonProperty("id")
		private String id;

		@JsonProperty("message")
		private String message;

	}

	@Getter
	@Setter
	@EqualsAndHashCode(callSuper = false)
	public static class Reasoning {

		@JsonProperty("id")
		private String id;

		@JsonProperty("summary")
		private List<String> summary;

		@JsonProperty("type")
		private String type;

		@JsonProperty("status")
		private String status;

	}

}
