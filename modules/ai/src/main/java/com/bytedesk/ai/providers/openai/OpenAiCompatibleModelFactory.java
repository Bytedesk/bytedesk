package com.bytedesk.ai.providers.openai;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;

public final class OpenAiCompatibleModelFactory {

	private OpenAiCompatibleModelFactory() {
	}

	public static OpenAiChatOptions withConnection(OpenAiChatOptions options, String baseUrl, String apiKey) {
		OpenAiChatOptions.Builder builder = options.mutate();
		if (baseUrl != null) {
			builder.baseUrl(baseUrl);
		}
		if (apiKey != null) {
			builder.apiKey(apiKey);
		}
		return builder.build();
	}

	public static OpenAiEmbeddingOptions withConnection(OpenAiEmbeddingOptions options, String baseUrl, String apiKey) {
		OpenAiEmbeddingOptions.Builder builder = OpenAiEmbeddingOptions.builder().from(options);
		if (baseUrl != null) {
			builder.baseUrl(baseUrl);
		}
		if (apiKey != null) {
			builder.apiKey(apiKey);
		}
		return builder.build();
	}

	public static OpenAiChatModel chatModel(OpenAiChatOptions options) {
		return OpenAiChatModel.builder()
			.options(options)
			.build();
	}

	public static OpenAiEmbeddingModel embeddingModel(OpenAiEmbeddingOptions options, MetadataMode metadataMode) {
		return OpenAiEmbeddingModel.builder()
			.options(options)
			.metadataMode(metadataMode)
			.build();
	}

}