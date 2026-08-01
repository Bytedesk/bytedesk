package ai.z.openapi.service.fileparsing;

/**
 * File parsing service interface
 */
public interface FileParsingService {

	/**
	 * Submits a file parsing task to the server.
	 * @param request The file parsing upload request
	 * @return FileParsingUploadResp containing the parsing task info
	 */
	FileParsingResponse createParseTask(FileParsingUploadReq request);

	/**
	 * Retrieves the result of a parsing task.
	 * @param request The parsing result query request (can include taskId, formatType
	 * etc)
	 * @return FileParsingDownloadResp containing the result content
	 */
	FileParsingDownloadResponse getParseResult(FileParsingDownloadReq request);

	/**
	 * Executes a synchronous file parsing operation. Uploads a file and immediately
	 * returns the parsing result, using the specified tool and file type.
	 * @param request The file parsing upload request (contains file path, tool type, file
	 * type, etc.)
	 * @return FileParsingDownloadResponse containing the parsed content and status
	 */
	FileParsingDownloadResponse syncParse(FileParsingUploadReq request);

}