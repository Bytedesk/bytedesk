package ai.z.openapi.service.embedding;

/**
 * Embedding service interface
 */
public interface EmbeddingService {

	/**
	 * Creates embeddings for the given input text.
	 * @param request the embeddings request
	 * @return EmbeddingResponse containing the embedding vectors
	 */
	EmbeddingResponse createEmbeddings(EmbeddingCreateParams request);

}