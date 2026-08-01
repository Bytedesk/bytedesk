package ai.z.openapi.service.voiceclone;

/**
 * Voice clone service interface for voice cloning operations. This service provides
 * methods for creating, deleting, and listing voice clones.
 */
public interface VoiceCloneService {

	/**
	 * Creates a new voice clone based on the provided audio sample and parameters.
	 * @param request the voice clone creation request containing voice name, sample audio
	 * text, target text, and audio file information
	 * @return VoiceCloneResponse containing the voice clone result and metadata
	 */
	VoiceCloneResponse cloneVoice(VoiceCloneRequest request);

	/**
	 * Deletes an existing voice clone by voice.
	 * @param request the voice deletion request containing the voice to delete
	 * @return VoiceDeleteResponse containing the deletion result and timestamp
	 */
	VoiceDeleteResponse deleteVoice(VoiceDeleteRequest request);

	/**
	 * Retrieves a list of available voice clones with optional filtering.
	 * @param request the voice list request containing optional voice type and name
	 * filters
	 * @return VoiceListResponse containing the filtered list of voice data
	 */
	VoiceListResponse listVoice(VoiceListRequest request);

}