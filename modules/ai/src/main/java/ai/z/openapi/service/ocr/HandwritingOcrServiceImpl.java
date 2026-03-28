package ai.z.openapi.service.ocr;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.ocr.HandwritingOcrApi;
import ai.z.openapi.utils.RequestSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;

/**
 * OCR handwriting recognition service implementation
 */
public class HandwritingOcrServiceImpl implements HandwritingOcrService {

	private final AbstractAiClient zAiClient;

	private final HandwritingOcrApi handwritingOcrApi;

	public HandwritingOcrServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.handwritingOcrApi = zAiClient.retrofit().create(HandwritingOcrApi.class);
	}

	@Override
	public HandwritingOcrResponse recognize(HandwritingOcrUploadReq request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getFilePath() == null) {
			throw new IllegalArgumentException("filePath cannot be null");
		}
		if (request.getToolType() == null) {
			throw new IllegalArgumentException("toolType cannot be null");
		}

		RequestSupplier<HandwritingOcrUploadReq, HandwritingOcrResult> supplier = params -> {
			try {
				File file = new File(params.getFilePath());
				if (!file.exists()) {
					throw new RuntimeException("file not found at " + params.getFilePath());
				}
				String toolType = params.getToolType();
				String languageType = params.getLanguageType();
				Boolean probability = params.getProbability();

				// Build multipart/form-data
				MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
						RequestBody.create(MediaType.parse("application/octet-stream"), file));
				MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				formBodyBuilder.addPart(filePart);
				formBodyBuilder.addFormDataPart("tool_type", toolType);
				if (languageType != null) {
					formBodyBuilder.addFormDataPart("language_type", languageType);
				}
				if (probability != null) {
					formBodyBuilder.addFormDataPart("probability", String.valueOf(probability));
				}

				MultipartBody multipartBody = formBodyBuilder.build();

				// Send POST request
				retrofit2.Call<HandwritingOcrResult> call = handwritingOcrApi.recognize(multipartBody);
				Response<HandwritingOcrResult> response = call.execute();
				if (!response.isSuccessful() || response.body() == null) {
					String errorJson = "";
					if (response.errorBody() != null) {
						errorJson = response.errorBody().string();
					}
					String msg = response.message();
					String msgFromBody = "";
					if (errorJson != null && errorJson.trim().startsWith("{")) {
						try {
							ObjectMapper mapper = new ObjectMapper();
							HandwritingOcrResult errorResult = mapper.readValue(errorJson, HandwritingOcrResult.class);
							msgFromBody = errorResult.getMessage();
						}
						catch (Exception e) {
							msgFromBody = errorJson;
						}
					}
					throw new IOException("Failed to recognize, code: " + response.code() + ", msg: " + msg
							+ (msgFromBody.isEmpty() ? "" : (", detail: " + msgFromBody)));
				}

				return Single.just(response.body());

			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		return this.zAiClient.executeRequest(request, supplier, HandwritingOcrResponse.class);
	}

}