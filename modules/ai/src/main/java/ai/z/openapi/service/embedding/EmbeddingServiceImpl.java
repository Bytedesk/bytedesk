package ai.z.openapi.service.embedding;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.embedding.EmbeddingApi;
import ai.z.openapi.utils.RequestSupplier;

import java.util.List;

/**
 * Embedding service implementation
 */
public class EmbeddingServiceImpl implements EmbeddingService {

	private final AbstractAiClient zAiClient;

	private final EmbeddingApi embeddingApi;

	public EmbeddingServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.embeddingApi = zAiClient.retrofit().create(EmbeddingApi.class);
	}

	@Override
	public EmbeddingResponse createEmbeddings(EmbeddingCreateParams request) {
		validateCreateEmbeddingsParams(request);
		RequestSupplier<EmbeddingCreateParams, EmbeddingResult> supplier = embeddingApi::createEmbeddings;
		return this.zAiClient.executeRequest(request, supplier, EmbeddingResponse.class);
	}

	private void validateCreateEmbeddingsParams(EmbeddingCreateParams request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null");
		}
		if (request.getModel() == null || request.getModel().trim().isEmpty()) {
			throw new IllegalArgumentException("Model cannot be null or empty");
		}
		if (request.getInput() == null) {
			throw new IllegalArgumentException("Input cannot be null");
		}
		if (request.getInput() instanceof String) {
			String input = (String) request.getInput();
			if (input.trim().isEmpty()) {
				throw new IllegalArgumentException("Input string cannot be empty");
			}
		}
		else if (request.getInput() instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> inputList = (List<String>) request.getInput();
			if (inputList.isEmpty()) {
				throw new IllegalArgumentException("Input list cannot be empty");
			}
		}
		if (request.getDimensions() != null && request.getDimensions() <= 0) {
			throw new IllegalArgumentException("Dimensions must be positive");
		}
	}

}