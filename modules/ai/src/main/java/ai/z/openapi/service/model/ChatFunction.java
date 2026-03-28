package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatFunction {

	private String name;

	private String description;

	/**
	 * The JSON schema defining the function's input arguments, you can use the
	 * ChatFunctionParameters or others
	 */
	private Object parameters;

}
