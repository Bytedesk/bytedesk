package ai.z.openapi.service.embedding;

import ai.z.openapi.service.model.Usage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;

/**
 * An object containing a response from the answer api
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingResult {

	/**
	 * The GLMmodel used for generating embeddings
	 */
	String model;

	/**
	 * The type of object returned, should be "list"
	 */
	String object;

	/**
	 * A list of the calculated embeddings
	 */
	List<Embedding> data;

	/**
	 * The API usage for this request
	 */
	Usage usage;

}
