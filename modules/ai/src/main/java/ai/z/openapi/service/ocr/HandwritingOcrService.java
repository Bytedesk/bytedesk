package ai.z.openapi.service.ocr;

public interface HandwritingOcrService {

	/**
	 * Executes a synchronous handwriting recognition operation.
	 * @param request The OCR upload request (contains file path, tool type, language
	 * type)
	 * @return HandwritingOcrResp containing the recognition result
	 */
	HandwritingOcrResponse recognize(HandwritingOcrUploadReq request);

}