package ai.z.openapi.service.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandwritingOcrResult {

	private String task_id; // Task ID or result ID

	private String message; // Response message

	private String status; // OCR task status (e.g., "succeeded")

	private int words_result_num; // Number of recognition results

	private List<WordsResult> words_result; // List of recognition results

}
