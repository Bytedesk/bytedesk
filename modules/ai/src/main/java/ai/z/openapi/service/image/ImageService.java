package ai.z.openapi.service.image;

/**
 * Image service interface
 */
public interface ImageService {

	/**
	 * Creates an image based on the provided generation request.
	 * @param createImageRequest the image generation request
	 * @return ImageResponse containing the generated image result
	 */
	ImageResponse createImage(CreateImageRequest createImageRequest);

	/**
	 * Asynchronously creates an image based on the provided generation request. Returns
	 * immediately with a task ID that can be used to query the status.
	 * @param createImageRequest the image generation request
	 * @return AsyncImageResponse containing the task ID and initial status
	 */
	AsyncImageResponse createImageAsync(CreateImageRequest createImageRequest);

	/**
	 * Queries the status and result of an async image generation task.
	 * @param taskId the async task ID returned by createImageAsync
	 * @return AsyncImageResponse containing task status and results when completed
	 */
	AsyncImageResponse queryAsyncResult(String taskId);

}