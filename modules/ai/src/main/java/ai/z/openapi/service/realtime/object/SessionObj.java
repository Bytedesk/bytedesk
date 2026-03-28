package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a session configuration object for the realtime API. This object contains
 * all the settings and parameters for a realtime session, including model configuration,
 * audio settings, tools, and other session parameters.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class SessionObj {

	/**
	 * The object type, always "realtime.session".
	 */
	@JsonProperty("object")
	private String object;

	/**
	 * The unique identifier for this session.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * The model to use for this session (e.g., "gpt-4o-realtime-preview").
	 */
	@JsonProperty("model")
	private String model;

	/**
	 * The set of modalities the model can respond with (e.g., ["text", "audio"]).
	 */
	@JsonProperty("modalities")
	private List<String> modalities;

	/**
	 * The voice the model uses to respond (e.g., "alloy", "echo", "fable").
	 */
	@JsonProperty("voice")
	private String voice;

	/**
	 * The system instructions for the model.
	 */
	@JsonProperty("instructions")
	private String instructions;

	/**
	 * The format of input audio (e.g., "pcm16", "g711_ulaw", "g711_alaw").
	 */
	@JsonProperty("input_audio_format")
	private String inputAudioFormat;

	/**
	 * The format of output audio (e.g., "pcm16", "g711_ulaw", "g711_alaw").
	 */
	@JsonProperty("output_audio_format")
	private String outputAudioFormat;

	/**
	 * Configuration for input audio transcription.
	 */
	@JsonProperty("input_audio_transcription")
	private InputAudioTranscriptionObj inputAudioTranscription;

	/**
	 * Configuration for turn detection (when the model should respond).
	 */
	@JsonProperty("turn_detection")
	private TurnDetectionObj turnDetection;

	/**
	 * List of tools available to the model.
	 */
	@JsonProperty("tools")
	private List<ToolObj> tools;

	/**
	 * Controls which tools the model can call.
	 */
	@JsonProperty("tool_choice")
	private ToolChoiceObj toolChoice;

	/**
	 * Sampling temperature for the model (0.0 to 1.0).
	 */
	@JsonProperty("temperature")
	private Double temperature;

	/**
	 * Maximum number of output tokens for a single response.
	 */
	@JsonProperty("max_response_output_tokens")
	private IntOrInfObj maxResponseOutputTokens;

	/**
	 * Beta fields for experimental features.
	 */
	@JsonProperty("beta_fields")
	private BetaFieldObj betaFields;

	public SessionObj() {
		this.object = null;
		this.id = null;
		this.modalities = Arrays.asList("text", "audio");
		this.model = null;
		this.instructions = null;
		this.voice = "tongtong";
		this.inputAudioFormat = "pcm16";
		this.outputAudioFormat = "pcm16";
		this.inputAudioTranscription = new InputAudioTranscriptionObj();
		this.turnDetection = new TurnDetectionObj();
		this.tools = new ArrayList<>();
		this.toolChoice = ToolChoiceObj.of("auto");
		this.temperature = 0.8;
		this.maxResponseOutputTokens = IntOrInfObj.inf();
		this.betaFields = new BetaFieldObj();
		// Temporary handling
		this.object = null;
		this.id = null;
		this.modalities = null;
		this.model = null;
		this.instructions = null;
		this.inputAudioTranscription = null;
		this.voice = null;
		this.toolChoice = null;
		this.temperature = null;
		this.maxResponseOutputTokens = null;
		// Field not supported yet
		this.toolChoice = null;
	}

}
