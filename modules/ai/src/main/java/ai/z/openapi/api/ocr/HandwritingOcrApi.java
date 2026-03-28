package ai.z.openapi.api.ocr;

import ai.z.openapi.service.ocr.HandwritingOcrResult;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * OCR Handwriting Recognition API Provides functionality to upload an image for
 * handwriting recognition, and retrieve the parsed result.
 */
public interface HandwritingOcrApi {

	/**
	 * Executes handwriting recognition synchronously.
	 * @param multipartBody The multipart request body containing the image file and
	 * metadata.
	 * @return The recognition result as a HandwritingOcrResp object.
	 */
	@POST("files/ocr")
	Call<HandwritingOcrResult> recognize(@Body MultipartBody multipartBody);

}