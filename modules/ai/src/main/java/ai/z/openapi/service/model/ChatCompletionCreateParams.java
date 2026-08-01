package ai.z.openapi.service.model;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import ai.z.openapi.service.model.params.CodeGeexExtra;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatCompletionCreateParams extends CommonRequest implements ClientRequest<ChatCompletionCreateParams> {

	/**
	 * Model code to call
	 */
	private String model;

	/**
	 * When calling the language model, pass the current conversation information list as
	 * prompt input to the model, in JSON array format like {"role": "user", "content":
	 * "hello"}; possible message types include System message, User message, Assistant
	 * message and Tool message. See message field description below
	 */
	private List<ChatMessage> messages;

	/**
	 * When do_sample is true, sampling strategy is enabled; when do_sample is false,
	 * sampling strategies temperature and top_p will not take effect
	 */
	@JsonProperty("do_sample")
	private Boolean doSample;

	/**
	 * Synchronous call: false, SSE call: true
	 */
	private Boolean stream;

	/**
	 * Sampling temperature, controls output randomness, must be positive Range: [0.0,1.0]
	 * default value is 0.95 Higher values make output more random and creative; lower
	 * values make output more stable or deterministic It's recommended to adjust either
	 * top_p or temperature parameter based on your use case, but not both simultaneously
	 */
	private Float temperature;

	/**
	 * Another method for temperature sampling, called nucleus sampling Range: (0.0, 1.0]
	 * Model considers results with top_p probability mass tokens For example: 0.1 means
	 * the model decoder only considers tokens from the top 10% probability candidate set
	 * It's recommended to adjust either top_p or temperature parameter based on your use
	 * case, but not both simultaneously
	 */
	@JsonProperty("top_p")
	private Float topP;

	/**
	 * Maximum output tokens for the model
	 */
	@JsonProperty("max_tokens")
	private Integer maxTokens;

	/**
	 * Model will stop generating when encountering characters specified by stop,
	 * currently only supports single stop word format
	 */
	private List<String> stop;

	/**
	 * Specifies the response format of the model. Defaults to text. Supports two
	 * formats:"text" "json_object". When using JSON mode, it’s recommended to clearly
	 * request JSON output in the prompt.
	 */
	@JsonProperty("response_format")
	private ResponseFormat responseFormat;

	/**
	 * Sensitive word detection control
	 */
	@JsonProperty("sensitive_word_check")
	private SensitiveWordCheckRequest sensitiveWordCheck;

	/**
	 * List of tools available for model invocation The tools field will count tokens and
	 * is also subject to token length limitations: 1. The sum of the length of the last
	 * message in messages, system message length, and functions field length cannot
	 * exceed 4000 tokens. Otherwise, the conversation cannot be completed. 2. When the
	 * total length of messages exceeds the token limit, the system will sequentially
	 * forget the earliest historical conversations up to 4000 tokens
	 */
	private List<ChatTool> tools;

	/**
	 * Conversation metadata
	 */
	private ChatMeta meta;

	/**
	 * Conversation metadata
	 */
	private CodeGeexExtra extra;

	/**
	 * Specify calling a specific function
	 */
	@JsonProperty("tool_choice")
	private Object toolChoice;

	/**
	 * Configuration parameters for model reasoning
	 */
	private ChatThinking thinking;

	/**
	 * Whether to stream tool calls
	 */
	private Boolean tool_stream;

	/**
	 * Forced watermark switch
	 */
	@JsonProperty("watermark_enabled")
	private Boolean watermarkEnabled;

}
