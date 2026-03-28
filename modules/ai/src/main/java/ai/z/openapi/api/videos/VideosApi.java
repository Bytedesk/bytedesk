package ai.z.openapi.api.videos;

import ai.z.openapi.service.videos.VideoCreateParams;
import ai.z.openapi.service.videos.VideoObject;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Videos API for AI-powered video generation Powered by CogVideo models using advanced
 * Transformer and 3D Causal VAE architecture Supports both text-to-video and
 * image-to-video generation with exceptional quality Features natural camera movements,
 * semantic coherence, and photorealistic visual output Configurable parameters include
 * quality, audio generation, size, and frame rate (fps)
 */
public interface VideosApi {

	/**
	 * Generate videos from text or image prompts using CogVideoX Creates high-quality
	 * videos with natural camera movements and semantic coherence Supports text-to-video
	 * generation with detailed prompt descriptions Supports image-to-video generation
	 * using image_url parameter for enhanced control
	 * @param request Video generation parameters including prompt, image_url, quality,
	 * with_audio, size, and fps
	 * @return Video generation task information with processing status and result URLs
	 */
	@POST("videos/generations")
	Single<VideoObject> videoGenerations(@Body VideoCreateParams request);

	/**
	 * Retrieve the result of an asynchronous video generation Gets the generated video
	 * result using the task ID from video generation request Video generation is
	 * typically asynchronous due to computational complexity
	 * @param id Task ID returned from video generation request
	 * @return Generated video URLs with metadata including duration, resolution, and
	 * audio information
	 */
	@GET("async-result/{id}")
	Single<VideoObject> videoGenerationsResult(@Path("id") String id);

}
