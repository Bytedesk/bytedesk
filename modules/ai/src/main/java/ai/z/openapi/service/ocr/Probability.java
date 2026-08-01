package ai.z.openapi.service.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Probability {

	private Double average; // Average confidence of the line

	private Double variance; // Confidence variance of the line

	private Double min; // Minimum confidence of the line

}