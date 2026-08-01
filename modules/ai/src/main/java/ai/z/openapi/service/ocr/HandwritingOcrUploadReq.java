package ai.z.openapi.service.ocr;

import ai.z.openapi.core.model.ClientRequest;
import lombok.Data;

/**
 * Handwriting OCR upload request object
 */
@Data
public class HandwritingOcrUploadReq implements ClientRequest<HandwritingOcrUploadReq> {

	private String filePath; // Path to the image file

	private String toolType; // Tool type, must be "hand_write"

	private String languageType; // Language type (optional)

	private Boolean probability; // Confidence score for each line of text recognition

}