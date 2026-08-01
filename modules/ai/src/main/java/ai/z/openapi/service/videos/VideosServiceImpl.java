package ai.z.openapi.service.videos;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.videos.VideosApi;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.utils.RequestSupplier;
import ai.z.openapi.utils.StringUtils;

/**
 * Implementation of VideosService
 */
public class VideosServiceImpl implements VideosService {

	private final AbstractAiClient zAiClient;

	private final VideosApi videosApi;

	public VideosServiceImpl(AbstractAiClient client) {
		this.zAiClient = client;
		this.videosApi = client.retrofit().create(VideosApi.class);
	}

	@Override
	public VideosResponse videoGenerations(VideoCreateParams request) {
		validateParams(request);
		RequestSupplier<VideoCreateParams, VideoObject> supplier = videosApi::videoGenerations;
		return zAiClient.executeRequest(request, supplier, VideosResponse.class);
	}

	@Override
	public VideosResponse videoGenerationsResult(String taskId) {
		validateTaskId(taskId);
		AsyncResultRetrieveParams request = new AsyncResultRetrieveParams(taskId);
		RequestSupplier<AsyncResultRetrieveParams, VideoObject> supplier = (params) -> videosApi
			.videoGenerationsResult(params.getTaskId());
		return zAiClient.executeRequest(request, supplier, VideosResponse.class);
	}

	private void validateParams(VideoCreateParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (StringUtils.isEmpty(request.getModel())) {
			throw new IllegalArgumentException("request model cannot be null or empty");
		}
		if (StringUtils.isEmpty(request.getPrompt())) {
			throw new IllegalArgumentException("request prompt cannot be null or empty");
		}
	}

	private void validateTaskId(String taskId) {
		if (StringUtils.isEmpty(taskId)) {
			throw new IllegalArgumentException("taskId cannot be null or empty");
		}
	}

}