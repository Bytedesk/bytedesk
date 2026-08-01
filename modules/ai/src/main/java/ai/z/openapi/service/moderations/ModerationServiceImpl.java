package ai.z.openapi.service.moderations;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.moderations.ModerationApi;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Implementation of ModerationService
 */
public class ModerationServiceImpl implements ModerationService {

	private final AbstractAiClient zAiClient;

	private final ModerationApi moderationApi;

	public ModerationServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.moderationApi = zAiClient.retrofit().create(ModerationApi.class);
	}

	@Override
	public ModerationResponse createModeration(ModerationCreateParams request) {
		RequestSupplier<ModerationCreateParams, ModerationResult> supplier = moderationApi::createModeration;
		return this.zAiClient.executeRequest(request, supplier, ModerationResponse.class);
	}

}