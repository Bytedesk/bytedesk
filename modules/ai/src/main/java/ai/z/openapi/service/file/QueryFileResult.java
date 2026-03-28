package ai.z.openapi.service.file;

import ai.z.openapi.service.model.ChatError;
import lombok.Data;

import java.util.List;

@Data
public class QueryFileResult {

	private String object;

	private List<File> data;

	private ChatError error;

}
