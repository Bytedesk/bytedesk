package ai.z.openapi.api.images;

import ai.z.openapi.service.image.AsyncImageResult;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResult;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Images API for AI-powered image generation models using advanced Transformer
 * architecture Delivers high-quality text-to-image generation with performance comparable
 * to industry leaders Features optimized diffusion model with enhanced noise planning for
 * superior image quality Supports various image styles, sizes, and generation parameters
 * with Chinese text rendering capabilities
 */
public interface ImagesApi {

	/**
	 * Generate images from text prompts using CogView-3-Plus Creates high-quality images
	 * based on textual descriptions with advanced semantic understanding Supports complex
	 * scene composition, lighting effects, and accurate Chinese character rendering
	 * Optimized for both artistic creation and practical image generation needs
	 * @param request Image generation parameters including prompt, size, style, quality,
	 * and model selection
	 * @return Generated image URLs with metadata including generation parameters and
	 * quality metrics
	 */
	@POST("images/generations")
	Single<ImageResult> createImage(@Body CreateImageRequest request);

	/**
	 * Asynchronously generate images from text prompts. Submits an image generation task
	 * and returns immediately with a task ID. Use queryAsyncResult to poll for completion
	 * status and retrieve results.
	 * @param request Image generation parameters including prompt, size, style, quality,
	 * and model selection
	 * @return Async task information including task ID and initial status
	 */
	@POST("async/images/generations")
	Single<AsyncImageResult> createImageAsync(@Body CreateImageRequest request);

	/**
	 * Query the status and result of an async image generation task.
	 * @param id The async task ID returned by createImageAsync
	 * @return Task status and generated image URLs when completed
	 */
	@GET("async-result/{id}")
	Single<AsyncImageResult> queryAsyncResult(@Path("id") String id);

}
