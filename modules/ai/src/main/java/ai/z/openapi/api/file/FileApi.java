package ai.z.openapi.api.file;

import ai.z.openapi.service.file.File;
import ai.z.openapi.service.file.FileDeleted;
import ai.z.openapi.service.file.QueryFileResult;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * File Management API for document and data handling Provides file upload, retrieval,
 * deletion, and content access capabilities Supports various file formats for
 * fine-tuning, knowledge base, and other AI tasks
 */
public interface FileApi {

	/**
	 * Upload a file to the platform Stores files for use in fine-tuning, knowledge base,
	 * or other AI operations
	 * @param multipartBody File data with metadata including purpose and format
	 * @return File information including ID, name, size, and upload status
	 */
	@POST("files")
	Single<File> uploadFile(@Body MultipartBody multipartBody);

	/**
	 * Delete a file from the platform Permanently removes the file and all associated
	 * data
	 * @param fileId Unique identifier of the file to delete
	 * @return Confirmation of file deletion with status information
	 */
	@DELETE("files/{file_id}")
	Single<FileDeleted> deletedFile(@Path("file_id") String fileId);

	/**
	 * Query and list files with filtering options Retrieves a paginated list of files
	 * with optional filtering by purpose and ordering
	 * @param after Cursor for pagination to get files after this point
	 * @param purpose Filter files by their intended purpose (e.g., fine-tune, assistants)
	 * @param order Sort order for the file list (e.g., created_at)
	 * @param limit Maximum number of files to return per page
	 * @return Paginated list of files with metadata
	 */
	@GET("files")
	Single<QueryFileResult> queryFileList(@Query("after") String after, @Query("purpose") String purpose,
			@Query("order") String order, @Query("limit") Integer limit);

	/**
	 * Download file content Streams the actual file content for download or processing
	 * @param fileId Unique identifier of the file to download
	 * @return Streaming file content in original format
	 */
	@Streaming
	@GET("files/{file_id}/content")
	Call<ResponseBody> fileContent(@Path("file_id") String fileId);

}
