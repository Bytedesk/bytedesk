package ai.z.openapi.service.image;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.images.ImagesApi;
import ai.z.openapi.service.model.AsyncResultRetrieveParams;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Image service implementation
 */
public class ImageServiceImpl implements ImageService {

	private final AbstractAiClient zAiClient;

	private final ImagesApi imagesApi;

	public ImageServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.imagesApi = zAiClient.retrofit().create(ImagesApi.class);
	}

	@Override
	public ImageResponse createImage(CreateImageRequest createImageRequest) {
		RequestSupplier<CreateImageRequest, ImageResult> supplier = imagesApi::createImage;
		return this.zAiClient.executeRequest(createImageRequest, supplier, ImageResponse.class);
	}

	@Override
	public AsyncImageResponse createImageAsync(CreateImageRequest createImageRequest) {
		RequestSupplier<CreateImageRequest, AsyncImageResult> supplier = imagesApi::createImageAsync;
		return this.zAiClient.executeRequest(createImageRequest, supplier, AsyncImageResponse.class);
	}

	@Override
	public AsyncImageResponse queryAsyncResult(String taskId) {
		AsyncResultRetrieveParams request = new AsyncResultRetrieveParams(taskId);
		RequestSupplier<AsyncResultRetrieveParams, AsyncImageResult> supplier = (params) -> imagesApi
			.queryAsyncResult(params.getTaskId());
		return zAiClient.executeRequest(request, supplier, AsyncImageResponse.class);
	}

}