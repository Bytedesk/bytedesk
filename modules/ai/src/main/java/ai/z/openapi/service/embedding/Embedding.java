package ai.z.openapi.service.embedding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;

/**
 * Represents an embedding returned by the embedding api
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Embedding {

	/**
	 * The type of object returned, should be "embedding"
	 */
	String object;

	/**
	 * The embedding vector
	 */
	List<Double> embedding;

	/**
	 * The position of this embedding in the list
	 */
	Integer index;

}
