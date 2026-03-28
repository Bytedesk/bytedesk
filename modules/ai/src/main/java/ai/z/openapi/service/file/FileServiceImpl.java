package ai.z.openapi.service.file;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.file.FileApi;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.core.response.HttpxBinaryResponseContent;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import java.io.IOException;
import java.util.Date;

/**
 * File service implementation
 */
public class FileServiceImpl implements FileService {

	private final AbstractAiClient zAiClient;

	private final FileApi fileApi;

	public FileServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.fileApi = zAiClient.retrofit().create(FileApi.class);
	}

	@Override
	public FileApiResponse uploadFile(FileUploadParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getFilePath() == null) {
			throw new IllegalArgumentException("request path cannot be null");
		}

		RequestSupplier<FileUploadParams, File> supplier = (params) -> {
			try {
				java.io.File file = new java.io.File(params.getFilePath());
				if (!file.exists()) {
					throw new RuntimeException("file not found");
				}
				MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
						RequestBody.create(MediaType.parse("application/octet-stream"), file));
				MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				formBodyBuilder.addPart(filePart);
				formBodyBuilder.addFormDataPart("purpose", params.getPurpose());
				if (params.getExtraJson() != null) {
					for (String s : params.getExtraJson().keySet()) {
						if (params.getExtraJson().get(s) instanceof String
								|| params.getExtraJson().get(s) instanceof Number
								|| params.getExtraJson().get(s) instanceof Boolean
								|| params.getExtraJson().get(s) instanceof Character) {
							formBodyBuilder.addFormDataPart(s, params.getExtraJson().get(s).toString());
						}
						else if (params.getExtraJson().get(s) instanceof Date) {
							Date date = (Date) params.getExtraJson().get(s);
							formBodyBuilder.addFormDataPart(s, String.valueOf(date.getTime()));
						}
					}
				}
				MultipartBody multipartBody = formBodyBuilder.build();
				return fileApi.uploadFile(multipartBody);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
		return this.zAiClient.executeRequest(request, supplier, FileApiResponse.class);
	}

	@Override
	public FileDelResponse deleteFile(FileDelRequest request) {
		RequestSupplier<FileDelRequest, FileDeleted> supplier = (params -> fileApi.deletedFile(params.getFileId()));
		return this.zAiClient.executeRequest(request, supplier, FileDelResponse.class);
	}

	@Override
	public QueryFileApiResponse listFiles(FileListParams queryFilesRequest) {
		RequestSupplier<FileListParams, QueryFileResult> supplier = (params) -> fileApi.queryFileList(params.getAfter(),
				params.getPurpose(), params.getOrder(), params.getLimit());
		return this.zAiClient.executeRequest(queryFilesRequest, supplier, QueryFileApiResponse.class);
	}

	@Override
	public HttpxBinaryResponseContent retrieveFileContent(String fileId) throws IOException {
		return fileWrapper(fileApi.fileContent(fileId));
	}

	private HttpxBinaryResponseContent fileWrapper(retrofit2.Call<ResponseBody> response) throws IOException {
		Response<ResponseBody> execute = response.execute();
		if (!execute.isSuccessful() || execute.body() == null) {
			throw new IOException("Failed to get the file content");
		}
		return new HttpxBinaryResponseContent(execute);
	}

}
