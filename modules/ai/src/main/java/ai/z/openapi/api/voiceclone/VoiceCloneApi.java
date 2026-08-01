package ai.z.openapi.api.voiceclone;

import ai.z.openapi.service.voiceclone.VoiceCloneRequest;
import ai.z.openapi.service.voiceclone.VoiceCloneResult;
import ai.z.openapi.service.voiceclone.VoiceDeleteRequest;
import ai.z.openapi.service.voiceclone.VoiceDeleteResult;
import ai.z.openapi.service.voiceclone.VoiceListResult;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Voice Clone API for voice cloning and management operations. Provides endpoints for
 * creating voice clones from audio samples, deleting existing voice clones, and listing
 * available voice clones.
 */
public interface VoiceCloneApi {

	/**
	 * Creates a new voice clone from provided audio sample and parameters. Uses advanced
	 * neural voice cloning technology to generate a custom voice model that can
	 * synthesize speech matching the characteristics of the provided sample.
	 * @param request voice clone creation parameters including voice name, sample text,
	 * target text, and audio file reference
	 * @return voice clone creation result with voice and preview file information
	 */
	@POST("voice/clone")
	Single<VoiceCloneResult> cloneVoice(@Body VoiceCloneRequest request);

	/**
	 * Deletes an existing voice clone by voice. Permanently removes the voice model and
	 * associated data from the system. This operation cannot be undone.
	 * @param request voice deletion parameters containing the voice to delete
	 * @return voice deletion result with confirmation and deletion timestamp
	 */
	@POST("voice/delete")
	Single<VoiceDeleteResult> deleteVoice(@Body VoiceDeleteRequest request);

	/**
	 * Retrieves a list of available voice clones with optional filtering. Returns
	 * metadata and details for voice clones including voice, names, types, download URLs,
	 * and creation timestamps.
	 * @param voiceType optional voice type filter
	 * @param voiceName optional voice name filter
	 * @return list of voice clone data with comprehensive metadata
	 */
	@GET("voice/list")
	Single<VoiceListResult> listVoices(@Query("voiceType") String voiceType, @Query("voiceName") String voiceName);

	/**
	 * Retrieves a list of available voice clones with optional filtering and Request-Id
	 * header. Returns metadata and details for voice clones including voice, names,
	 * types, download URLs, and creation timestamps.
	 * @param voiceType optional voice type filter
	 * @param voiceName optional voice name filter
	 * @param requestId unique request identifier for tracking and monitoring
	 * @return list of voice clone data with comprehensive metadata
	 */
	@GET("voice/list")
	Single<VoiceListResult> listVoices(@Query("voiceType") String voiceType, @Query("voiceName") String voiceName,
			@Header("Request-Id") String requestId);

}