package ai.z.openapi.service.fileparsing;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.fileparsing.FileParsingApi;
import ai.z.openapi.core.response.HttpxBinaryResponseContent;
import ai.z.openapi.utils.RequestSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;

/**
 * File parsing service implementation
 */
public class FileParsingServiceImpl implements FileParsingService {

	private final AbstractAiClient zAiClient;

	private final FileParsingApi fileParsingApi;

	public FileParsingServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.fileParsingApi = zAiClient.retrofit().create(FileParsingApi.class);
	}

	@Override
	public FileParsingResponse createParseTask(FileParsingUploadReq request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getFilePath() == null) {
			throw new IllegalArgumentException("file path cannot be null");
		}
		if (request.getToolType() == null) {
			throw new IllegalArgumentException("toolType cannot be null");
		}
		// Construct multipart/form-data
		RequestSupplier<FileParsingUploadReq, FileParsingUploadResp> supplier = params -> {
			try {
				File file = new File(params.getFilePath());
				if (!file.exists()) {
					throw new RuntimeException("file not found");
				}

				String toolType = params.getToolType();
				String fileType = params.getFileType() == null ? "" : params.getFileType();

				MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
						RequestBody.create(MediaType.parse("application/octet-stream"), file));
				MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				formBodyBuilder.addPart(filePart);
				formBodyBuilder.addFormDataPart("tool_type", toolType);
				formBodyBuilder.addFormDataPart("file_type", fileType);

				MultipartBody multipartBody = formBodyBuilder.build();

				return fileParsingApi.createParseTask(multipartBody);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
		return this.zAiClient.executeRequest(request, supplier, FileParsingResponse.class);
	}

	@Override
	public FileParsingDownloadResponse getParseResult(FileParsingDownloadReq request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getTaskId() == null) {
			throw new IllegalArgumentException("taskId cannot be null");
		}
		if (request.getFormatType() == null) {
			throw new IllegalArgumentException("formatType cannot be null");
		}

		RequestSupplier<FileParsingDownloadReq, FileParsingDownloadResp> supplier = params -> {
			try {
				retrofit2.Call<ResponseBody> call = fileParsingApi.downloadParseResult(request.getTaskId(),
						request.getFormatType());
				Response<ResponseBody> execute = call.execute();
				if (!execute.isSuccessful() || execute.body() == null) {
					throw new IOException("Failed to download parse result");
				}

				HttpxBinaryResponseContent httpxBinaryResponseContent = new HttpxBinaryResponseContent(execute);
				String result = httpxBinaryResponseContent.getText();

				ObjectMapper mapper = new ObjectMapper();
				FileParsingDownloadResp fileParsingDownloadResp = mapper.readValue(result,
						FileParsingDownloadResp.class);

				return Single.just(fileParsingDownloadResp);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		return this.zAiClient.executeRequest(request, supplier, FileParsingDownloadResponse.class);
	}

	@Override
	public FileParsingDownloadResponse syncParse(FileParsingUploadReq request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getFilePath() == null) {
			throw new IllegalArgumentException("filePath cannot be null");
		}
		if (request.getToolType() == null) {
			throw new IllegalArgumentException("toolType cannot be null");
		}

		RequestSupplier<FileParsingUploadReq, FileParsingDownloadResp> supplier = params -> {
			try {
				File file = new File(params.getFilePath());
				if (!file.exists()) {
					throw new RuntimeException("file not found at " + params.getFilePath());
				}

				String toolType = params.getToolType();
				String fileType = params.getFileType();

				// Construct multipart/form-data
				MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
						RequestBody.create(MediaType.parse("application/octet-stream"), file));
				MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				formBodyBuilder.addPart(filePart);
				formBodyBuilder.addFormDataPart("tool_type", toolType);
				formBodyBuilder.addFormDataPart("file_type", fileType);

				MultipartBody multipartBody = formBodyBuilder.build();

				// Send a POST request
				retrofit2.Call<FileParsingDownloadResp> call = fileParsingApi.syncParse(multipartBody);
				Response<FileParsingDownloadResp> response = call.execute();
				if (!response.isSuccessful() || response.body() == null) {
					throw new IOException(
							"Failed to sync parse, code: " + response.code() + ", msg: " + response.message());
				}

				return Single.just(response.body());

			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		return this.zAiClient.executeRequest(request, supplier, FileParsingDownloadResponse.class);
	}

}