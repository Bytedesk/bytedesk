package ai.z.openapi.api.embedding;

import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResult;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Embeddings API for text vectorization using GLM models Converts text into
 * high-dimensional vector representations for semantic similarity and search Supports
 * batch processing of multiple text inputs with configurable dimensions Ideal for RAG
 * applications, semantic search, text classification, and similarity matching Features
 * optimized performance for Chinese and multilingual text processing
 */
public interface EmbeddingApi {

	/**
	 * Create embeddings for input text using GLM embedding models Converts text strings
	 * into numerical vector representations that capture semantic meaning Supports
	 * customizable vector dimensions for different use cases and performance requirements
	 * Optimized for both single text and batch processing scenarios
	 * @param request Embedding parameters including input text, model selection,
	 * dimensions, and encoding format
	 * @return High-quality embedding vectors with usage statistics and token consumption
	 * details
	 */
	@POST("embeddings")
	Single<EmbeddingResult> createEmbeddings(@Body EmbeddingCreateParams request);

}
