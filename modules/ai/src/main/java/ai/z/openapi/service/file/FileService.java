package ai.z.openapi.service.file;

import ai.z.openapi.core.response.HttpxBinaryResponseContent;
import java.io.IOException;

/**
 * File service interface
 */
public interface FileService {

	/**
	 * Uploads a file to the server.
	 * @param request the file upload request
	 * @return FileApiResponse containing the upload result
	 */
	FileApiResponse uploadFile(FileUploadParams request);

	/**
	 * Delete file by file ID.
	 * @param request the file id to delete.
	 * @return FileDelResponse containing the delete result
	 */
	FileDelResponse deleteFile(FileDelRequest request);

	/**
	 * Lists all files.
	 * @param queryFilesRequest FileListParams containing the query parameters for listing
	 * files
	 * @return QueryFileApiResponse containing the list of files
	 */
	QueryFileApiResponse listFiles(FileListParams queryFilesRequest);

	/**
	 * Retrieves the content of a specific file.
	 * @param fileId the ID of the file to retrieve
	 * @return HttpxBinaryResponseContent containing the file content
	 * @throws IOException if an I/O error occurs
	 */
	HttpxBinaryResponseContent retrieveFileContent(String fileId) throws IOException;

}