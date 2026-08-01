package ai.z.openapi.service.layoutparsing;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.layoutparsing.LayoutParsingApi;
import ai.z.openapi.utils.RequestSupplier;

public class LayoutParsingServiceImpl implements LayoutParsingService {

	private final AbstractAiClient zAiClient;

	private final LayoutParsingApi layoutParsingApi;

	public LayoutParsingServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.layoutParsingApi = zAiClient.retrofit().create(LayoutParsingApi.class);
	}

	@Override
	public LayoutParsingResponse layoutParsing(LayoutParsingCreateParams request) {
		validateParams(request);
		RequestSupplier<LayoutParsingCreateParams, LayoutParsingResult> supplier = layoutParsingApi::layoutParsing;
		return this.zAiClient.executeRequest(request, supplier, LayoutParsingResponse.class);
	}

	private void validateParams(LayoutParsingCreateParams request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getModel() == null) {
			throw new IllegalArgumentException("model cannot be null");
		}
		if (request.getFile() == null) {
			throw new IllegalArgumentException("file cannot be null");
		}
	}

}
