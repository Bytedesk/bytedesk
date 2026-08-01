package ai.z.openapi.service.web_reader;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.web_reader.WebReaderApi;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Web reader service implementation
 */
public class WebReaderServiceImpl implements WebReaderService {

	private final AbstractAiClient zAiClient;

	private final WebReaderApi webReaderApi;

	public WebReaderServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.webReaderApi = zAiClient.retrofit().create(WebReaderApi.class);
	}

	@Override
	public WebReaderResponse createWebReader(WebReaderRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		request.validate();
		RequestSupplier<WebReaderRequest, WebReaderResult> supplier = webReaderApi::reader;
		return this.zAiClient.executeRequest(request, supplier, WebReaderResponse.class);
	}

}