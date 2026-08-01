/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedesk.ai.springai.providers.moonshot.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Options for Moonshot chat completions.
 *
 * @author Geng Rong
 * @author Thomas Vitale
 * @author Alexandros Pappas
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoonshotChatOptions implements ToolCallingChatOptions {

	/**
	 * ID of the model to use
	 */
	private @JsonProperty("model") String model;

	/**
	 * The maximum number of tokens to generate in the chat completion. The total length
	 * of input tokens and generated tokens is limited by the model's context length.
	 */
	private @JsonProperty("max_tokens") Integer maxTokens;

	/**
	 * The maximum number of completion tokens to generate in the chat completion.
	 * Preferred by current Kimi API over max_tokens.
	 */
	private @JsonProperty("max_completion_tokens") Integer maxCompletionTokens;

	/**
	 * What sampling temperature to use, between 0.0 and 1.0. Higher values like 0.8 will
	 * make the output more random, while lower values like 0.2 will make it more focused
	 * and deterministic. We generally recommend altering this or top_p but not both.
	 */
	private @JsonProperty("temperature") Double temperature;

	/**
	 * An alternative to sampling with temperature, called nucleus sampling, where the
	 * model considers the results of the tokens with top_p probability mass. So 0.1 means
	 * only the tokens comprising the top 10% probability mass are considered. We
	 * generally recommend altering this or temperature but not both.
	 */
	private @JsonProperty("top_p") Double topP;

	/**
	 * How many chat completion choices to generate for each input message. Note that you
	 * will be charged based on the number of generated tokens across all the choices.
	 * Keep n as 1 to minimize costs.
	 */
	private @JsonProperty("n") Integer n;

	/**
	 * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether
	 * they appear in the text so far, increasing the model's likelihood to talk about new
	 * topics.
	 */
	private @JsonProperty("presence_penalty") Double presencePenalty;

	/**
	 * Number between -2.0 and 2.0. Positive values penalize new tokens based on their
	 * existing frequency in the text so far, decreasing the model's likelihood to repeat
	 * the same line verbatim.
	 */
	private @JsonProperty("frequency_penalty") Double frequencyPenalty;

	/**
	 * Up to 5 sequences where the API will stop generating further tokens.
	 */
	private @JsonProperty("stop") List<String> stop;

	private @JsonProperty("tools") List<MoonshotApi.FunctionTool> tools;

	/**
	 * Controls which (if any) function is called by the model. none means the model will
	 * not call a function and instead generates a message. auto means the model can pick
	 * between generating a message or calling a function. Specifying a particular
	 * function via {"type: "function", "function": {"name": "my_function"}} forces the
	 * model to call that function. none is the default when no functions are present.
	 * auto is the default if functions are present. Use the
	 * {@link MoonshotApi.ChatCompletionRequest.ToolChoiceBuilder} to create a tool choice
	 * object.
	 */
	private @JsonProperty("tool_choice") String toolChoice;

	/**
	 * A unique identifier representing your end-user, which can help Moonshot to monitor
	 * and detect abuse.
	 */
	private @JsonProperty("user") String user;

	/**
	 * Controls thinking mode for kimi-k2.* models.
	 */
	private @JsonProperty("thinking") Thinking thinking;

	/**
	 * Collection of {@link ToolCallback}s to be used for tool calling in the chat
	 * completion requests.
	 */
	@JsonIgnore
	private List<ToolCallback> toolCallbacks = new ArrayList<>();

	/**
	 * Collection of tool names to be resolved at runtime and used for tool calling in the
	 * chat completion requests.
	 */
	@JsonIgnore
	private Set<String> toolNames = new HashSet<>();

	/**
	 * Context values passed to tool callbacks during execution.
	 */
	@JsonIgnore
	private Map<String, Object> toolContext = new HashMap<>();

	// @formatter:on

	public static Builder builder() {
		return new Builder();
	}

	public static MoonshotChatOptions fromOptions(MoonshotChatOptions fromOptions) {
		return MoonshotChatOptions.builder()
			.model(fromOptions.getModel())
			.frequencyPenalty(fromOptions.getFrequencyPenalty())
			.maxTokens(fromOptions.getMaxTokens())
			.maxCompletionTokens(fromOptions.getMaxCompletionTokens())
			.N(fromOptions.getN())
			.presencePenalty(fromOptions.getPresencePenalty())
			.stop(fromOptions.getStop() != null ? new ArrayList<>(fromOptions.getStop()) : null)
			.temperature(fromOptions.getTemperature())
			.topP(fromOptions.getTopP())
			.tools(fromOptions.getTools())
			.toolChoice(fromOptions.getToolChoice())
			.user(fromOptions.getUser())
			.thinking(fromOptions.getThinking())
			.toolCallbacks(
					fromOptions.getToolCallbacks() != null ? new ArrayList<>(fromOptions.getToolCallbacks()) : null)
			.toolNames(fromOptions.getToolNames() != null ? new HashSet<>(fromOptions.getToolNames()) : null)
			.toolContext(fromOptions.getToolContext() != null ? new HashMap<>(fromOptions.getToolContext()) : null)
			.build();
	}

	public List<MoonshotApi.FunctionTool> getTools() {
		return tools;
	}

	public void setTools(List<MoonshotApi.FunctionTool> tools) {
		this.tools = tools;
	}

	public String getToolChoice() {
		return toolChoice;
	}

	public void setToolChoice(String toolChoice) {
		this.toolChoice = toolChoice;
	}

	@Override
	@JsonIgnore
	public List<ToolCallback> getToolCallbacks() {
		return this.toolCallbacks;
	}

	@JsonIgnore
	public void setToolCallbacks(List<ToolCallback> toolCallbacks) {
		if (toolCallbacks == null) {
			this.toolCallbacks = new ArrayList<>();
			return;
		}
		Assert.noNullElements(toolCallbacks, "toolCallbacks cannot contain null elements");
		this.toolCallbacks = new ArrayList<>(toolCallbacks);
	}

	@JsonIgnore
	public Set<String> getToolNames() {
		return this.toolNames;
	}

	@JsonIgnore
	public void setToolNames(Set<String> toolNames) {
		if (toolNames == null) {
			this.toolNames = new HashSet<>();
			return;
		}
		Assert.noNullElements(toolNames, "toolNames cannot contain null elements");
		toolNames.forEach(tool -> Assert.hasText(tool, "toolNames cannot contain empty elements"));
		this.toolNames = new HashSet<>(toolNames);
	}

	@Override
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public Double getFrequencyPenalty() {
		return this.frequencyPenalty;
	}

	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	@Override
	public Integer getMaxTokens() {
		return this.maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Integer getMaxCompletionTokens() {
		return this.maxCompletionTokens;
	}

	public void setMaxCompletionTokens(Integer maxCompletionTokens) {
		this.maxCompletionTokens = maxCompletionTokens;
	}

	public Integer getN() {
		return this.n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	@Override
	public Double getPresencePenalty() {
		return this.presencePenalty;
	}

	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	@Override
	@JsonIgnore
	public List<String> getStopSequences() {
		return getStop();
	}

	@JsonIgnore
	public void setStopSequences(List<String> stopSequences) {
		setStop(stopSequences);
	}

	public List<String> getStop() {
		return this.stop;
	}

	public void setStop(List<String> stop) {
		this.stop = stop;
	}

	@Override
	public Double getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	@Override
	public Double getTopP() {
		return this.topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Thinking getThinking() {
		return this.thinking;
	}

	public void setThinking(Thinking thinking) {
		this.thinking = thinking;
	}

	@Override
	@JsonIgnore
	public Integer getTopK() {
		return null;
	}

	@Override
	public Map<String, Object> getToolContext() {
		return this.toolContext;
	}

	public void setToolContext(Map<String, Object> toolContext) {
		this.toolContext = toolContext != null ? new HashMap<>(toolContext) : new HashMap<>();
	}

	@Override
	public Builder mutate() {
		return builder()
				.model(this.model)
				.frequencyPenalty(this.frequencyPenalty)
				.maxTokens(this.maxTokens)
				.presencePenalty(this.presencePenalty)
				.stopSequences(this.stop)
				.temperature(this.temperature)
				.topP(this.topP)
				.maxCompletionTokens(this.maxCompletionTokens)
				.N(this.n)
				.tools(this.tools != null ? new ArrayList<>(this.tools) : null)
				.toolChoice(this.toolChoice)
				.user(this.user)
				.thinking(this.thinking)
				.toolCallbacks(this.toolCallbacks != null ? new ArrayList<>(this.toolCallbacks) : null)
				.toolNames(this.toolNames != null ? new HashSet<>(this.toolNames) : null)
				.toolContext(this.toolContext != null ? new HashMap<>(this.toolContext) : null);
	}

	@SuppressWarnings("unchecked")
	public <T extends org.springframework.ai.chat.prompt.ChatOptions> T copy() {
		return (T) MoonshotChatOptions.fromOptions(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.model, this.frequencyPenalty, this.maxTokens, this.maxCompletionTokens, this.n,
				this.presencePenalty, this.stop, this.temperature, this.topP, this.tools, this.toolChoice, this.user,
				this.thinking, this.toolCallbacks, this.toolNames, this.toolContext);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MoonshotChatOptions other = (MoonshotChatOptions) o;
		return Objects.equals(this.model, other.model) && Objects.equals(this.frequencyPenalty, other.frequencyPenalty)
				&& Objects.equals(this.maxTokens, other.maxTokens)
				&& Objects.equals(this.maxCompletionTokens, other.maxCompletionTokens) && Objects.equals(this.n, other.n)
				&& Objects.equals(this.presencePenalty, other.presencePenalty) && Objects.equals(this.stop, other.stop)
				&& Objects.equals(this.temperature, other.temperature) && Objects.equals(this.topP, other.topP)
				&& Objects.equals(this.tools, other.tools) && Objects.equals(this.toolChoice, other.toolChoice)
				&& Objects.equals(this.user, other.user) && Objects.equals(this.thinking, other.thinking)
				&& Objects.equals(this.toolCallbacks, other.toolCallbacks)
				&& Objects.equals(this.toolNames, other.toolNames)
				&& Objects.equals(this.toolContext, other.toolContext);
	}

	@Override
	public String toString() {
		return "MoonshotChatOptions{" +
				"model='" + this.model + '\'' +
				", maxTokens=" + this.maxTokens +
				", maxCompletionTokens=" + this.maxCompletionTokens +
				", temperature=" + this.temperature +
				", topP=" + this.topP +
				", user='" + this.user + '\'' +
				'}';
	}

	public static class Builder implements ToolCallingChatOptions.Builder<Builder> {

		protected MoonshotChatOptions options;

		public Builder() {
			this.options = new MoonshotChatOptions();
		}

		public Builder(MoonshotChatOptions options) {
			this.options = options;
		}

		@Override
		public Builder model(String model) {
			this.options.model = model;
			return this;
		}

		public Builder model(MoonshotApi.ChatModel moonshotChatModel) {
			this.options.model = moonshotChatModel.getName();
			return this;
		}

		@Override
		public Builder frequencyPenalty(Double frequencyPenalty) {
			this.options.frequencyPenalty = frequencyPenalty;
			return this;
		}

		@Override
		public Builder maxTokens(Integer maxTokens) {
			this.options.maxTokens = maxTokens;
			return this;
		}

		public Builder maxCompletionTokens(Integer maxCompletionTokens) {
			this.options.maxCompletionTokens = maxCompletionTokens;
			return this;
		}

		public Builder N(Integer n) {
			this.options.n = n;
			return this;
		}

		@Override
		public Builder presencePenalty(Double presencePenalty) {
			this.options.presencePenalty = presencePenalty;
			return this;
		}

		@Override
		public Builder stopSequences(List<String> stopSequences) {
			this.options.stop = stopSequences != null ? new ArrayList<>(stopSequences) : null;
			return this;
		}

		public Builder stop(List<String> stop) {
			this.options.stop = stop != null ? new ArrayList<>(stop) : null;
			return this;
		}

		@Override
		public Builder temperature(Double temperature) {
			this.options.temperature = temperature;
			return this;
		}

		@Override
		public Builder topK(Integer topK) {
			return this;
		}

		@Override
		public Builder topP(Double topP) {
			this.options.topP = topP;
			return this;
		}

		public Builder thinking(Thinking thinking) {
			this.options.thinking = thinking;
			return this;
		}

		public Builder tools(List<MoonshotApi.FunctionTool> tools) {
			this.options.tools = tools;
			return this;
		}

		public Builder toolChoice(String toolChoice) {
			this.options.toolChoice = toolChoice;
			return this;
		}

		public Builder user(String user) {
			this.options.user = user;
			return this;
		}

		@Override
		public Builder toolCallbacks(List<ToolCallback> toolCallbacks) {
			this.options.setToolCallbacks(toolCallbacks);
			return this;
		}

		@Override
		public Builder toolCallbacks(ToolCallback... toolCallbacks) {
			Assert.notNull(toolCallbacks, "toolCallbacks cannot be null");
			this.options.toolCallbacks.addAll(Arrays.asList(toolCallbacks));
			return this;
		}

		public Builder toolNames(Set<String> toolNames) {
			Assert.notNull(toolNames, "toolNames cannot be null");
			this.options.setToolNames(toolNames);
			return this;
		}

		public Builder toolNames(String... toolNames) {
			Assert.notNull(toolNames, "toolNames cannot be null");
			this.options.toolNames.addAll(Set.of(toolNames));
			return this;
		}

		@Override
		public Builder clone() {
			return new Builder(MoonshotChatOptions.fromOptions(this.options));
		}

		@Override
		public Builder combineWith(org.springframework.ai.chat.prompt.ChatOptions.Builder<?> other) {
			if (other == null) {
				return this;
			}
			org.springframework.ai.chat.prompt.ChatOptions otherOptions = other.build();
			if (otherOptions instanceof MoonshotChatOptions moonshotChatOptions) {
				this.options = MoonshotChatOptions.fromOptions(moonshotChatOptions);
				return this;
			}
			if (otherOptions.getModel() != null) {
				this.options.model = otherOptions.getModel();
			}
			if (otherOptions.getFrequencyPenalty() != null) {
				this.options.frequencyPenalty = otherOptions.getFrequencyPenalty();
			}
			if (otherOptions.getMaxTokens() != null) {
				this.options.maxTokens = otherOptions.getMaxTokens();
			}
			if (otherOptions.getPresencePenalty() != null) {
				this.options.presencePenalty = otherOptions.getPresencePenalty();
			}
			if (otherOptions.getStopSequences() != null) {
				this.options.stop = new ArrayList<>(otherOptions.getStopSequences());
			}
			if (otherOptions.getTemperature() != null) {
				this.options.temperature = otherOptions.getTemperature();
			}
			if (otherOptions.getTopP() != null) {
				this.options.topP = otherOptions.getTopP();
			}
			if (otherOptions instanceof ToolCallingChatOptions toolCallingChatOptions) {
				this.toolCallbacks(toolCallingChatOptions.getToolCallbacks());
				this.toolContext(toolCallingChatOptions.getToolContext());
			}
			return this;
		}

		@Override
		public Builder toolContext(Map<String, Object> toolContext) {
			if (this.options.toolContext == null) {
				this.options.toolContext = toolContext != null ? new HashMap<>(toolContext) : new HashMap<>();
			}
			else if (toolContext != null) {
				this.options.toolContext.putAll(toolContext);
			}
			return this;
		}

		@Override
		public Builder toolContext(String key, Object value) {
			Assert.hasText(key, "key cannot be null");
			Assert.notNull(value, "value cannot be null");
			this.options.toolContext.put(key, value);
			return this;
		}

		@Override
		public MoonshotChatOptions build() {
			return this.options;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Thinking(@JsonProperty("type") String type) {

		public static final String ENABLED = "enabled";
		public static final String DISABLED = "disabled";

	}

}
