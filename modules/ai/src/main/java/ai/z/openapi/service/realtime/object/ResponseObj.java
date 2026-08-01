package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a response configuration object for realtime API responses. This object
 * defines how the AI model should generate responses, including modalities, tools, and
 * other parameters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ResponseObj {

	/**
	 * The modalities for the response (e.g., "text", "audio").
	 */
	@JsonProperty("modalities")
	private List<String> modalities;

	/**
	 * Instructions for the AI model on how to behave during the response.
	 */
	@JsonProperty("instructions")
	private String instructions;

	/**
	 * The voice to use for audio output (e.g., "alloy", "echo", "fable").
	 */
	@JsonProperty("voice")
	private String voice;

	/**
	 * The format for output audio (e.g., "pcm16", "g711_ulaw", "g711_alaw").
	 */
	@JsonProperty("output_audio_format")
	private String outputAudioFormat;

	/**
	 * The tools available to the model during response generation.
	 */
	@JsonProperty("tools")
	private List<ToolObj> tools;

	/**
	 * Controls which tools the model can call during response generation.
	 */
	@JsonProperty("tool_choice")
	private ToolChoiceObj toolChoice;

	/**
	 * The sampling temperature for response generation (0.0 to 1.0).
	 */
	@JsonProperty("temperature")
	private Double temperature;

	/**
	 * The maximum number of output tokens for the response.
	 */
	@JsonProperty("max_output_tokens")
	private IntOrInfObj maxOutputTokens;

	/**
	 * The conversation ID to use for this response.
	 */
	@JsonProperty("conversation")
	private String conversation;

	/**
	 * Additional metadata to include with the response.
	 */
	@JsonProperty("metadata")
	private Map<String, String> metadata;

	/**
	 * Input data for the response generation.
	 */
	@JsonProperty("input")
	private List<Object> input;

	public ResponseObj() {
		this.modalities = new ArrayList<>();
		this.modalities.add("text");
		this.modalities.add("audio");
		this.instructions = "";
		this.voice = "alloy";
		this.outputAudioFormat = "pcm16";
		this.tools = new ArrayList<>();
		this.toolChoice = ToolChoiceObj.of("auto");
		this.temperature = 0.7;
		this.maxOutputTokens = IntOrInfObj.inf();
		this.conversation = "";
		this.metadata = new HashMap<>();
		this.input = new ArrayList<>();
	}

}
