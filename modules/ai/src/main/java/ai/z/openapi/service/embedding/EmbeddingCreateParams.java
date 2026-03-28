package ai.z.openapi.service.embedding;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Creates an embedding vector representing the input text.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmbeddingCreateParams extends CommonRequest implements ClientRequest<EmbeddingCreateParams> {

	/**
	 * The name of the model to use. Required if using the new v1/embeddings endpoint.
	 */
	private String model;

	/**
	 * Input text to get embeddings for, encoded as a string or array of tokens. To get
	 * embeddings for multiple inputs in a single request, pass an array of strings or
	 * array of token arrays. Each input must not exceed 2048 tokens in length.
	 * <p>
	 * Unless you are embedding code, we suggest replacing newlines (\n) in your input
	 * with a single space, as we have observed inferior results when newlines are
	 * present.
	 */

	private Object input;

	private Integer dimensions;

	public void setInput(String input) {
		this.input = input;
	}

	public void setInput(List<String> input) throws IllegalArgumentException {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("Input cannot be null or empty");
		}

		this.input = input;
	}

}
