package ai.z.openapi.service.voiceclone;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.voiceclone.VoiceCloneApi;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.utils.RequestSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Voice clone service implementation for voice cloning operations. This service provides
 * methods for creating, deleting, and listing voice clones using the VoiceClone API
 * endpoints.
 */
public class VoiceCloneServiceImpl implements VoiceCloneService {

	protected static final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();

	private final AbstractAiClient zAiClient;

	private final VoiceCloneApi voiceCloneApi;

	public VoiceCloneServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.voiceCloneApi = zAiClient.retrofit().create(VoiceCloneApi.class);
	}

	@Override
	public VoiceCloneResponse cloneVoice(VoiceCloneRequest request) {
		validateCreateVoiceParams(request);
		RequestSupplier<VoiceCloneRequest, VoiceCloneResult> supplier = voiceCloneApi::cloneVoice;
		return this.zAiClient.executeRequest(request, supplier, VoiceCloneResponse.class);
	}

	@Override
	public VoiceDeleteResponse deleteVoice(VoiceDeleteRequest request) {
		validateDeleteVoiceParams(request);
		RequestSupplier<VoiceDeleteRequest, VoiceDeleteResult> supplier = voiceCloneApi::deleteVoice;
		return this.zAiClient.executeRequest(request, supplier, VoiceDeleteResponse.class);
	}

	@Override
	public VoiceListResponse listVoice(VoiceListRequest request) {
		RequestSupplier<VoiceListRequest, VoiceListResult> supplier = (params) -> {
			String voiceType = params != null ? params.getVoiceType() : null;
			String voiceName = params != null ? params.getVoiceName() : null;
			String requestId = params != null ? params.getRequestId() : "";

			return voiceCloneApi.listVoices(voiceType, voiceName, requestId);
		};
		return this.zAiClient.executeRequest(request, supplier, VoiceListResponse.class);
	}

	private void validateCreateVoiceParams(VoiceCloneRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getVoiceName() == null || request.getVoiceName().trim().isEmpty()) {
			throw new IllegalArgumentException("voice name cannot be null or empty");
		}
		if (request.getInput() == null || request.getInput().trim().isEmpty()) {
			throw new IllegalArgumentException("input cannot be null or empty");
		}
		if (request.getFileId() == null || request.getFileId().trim().isEmpty()) {
			throw new IllegalArgumentException("file ID cannot be null or empty");
		}
	}

	private void validateDeleteVoiceParams(VoiceDeleteRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getVoice() == null || request.getVoice().trim().isEmpty()) {
			throw new IllegalArgumentException("voice cannot be null or empty");
		}
	}

}