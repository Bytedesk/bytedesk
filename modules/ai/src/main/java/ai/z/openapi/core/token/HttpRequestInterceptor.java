package ai.z.openapi.core.token;

import ai.z.openapi.core.config.ZaiConfig;
import ai.z.openapi.utils.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * OkHttp Interceptor that adds an authorization token header
 */
public class HttpRequestInterceptor implements Interceptor {

	private final ZaiConfig config;

	public HttpRequestInterceptor(ZaiConfig config) {
		Objects.requireNonNull(config.getApiKey(), "Z.ai token required");
		this.config = config;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		String accessToken;
		if (this.config.isDisableTokenCache()) {
			accessToken = this.config.getApiKey();
		}
		else {
			TokenManager tokenManager = GlobalTokenManager.getTokenManagerV4();
			accessToken = tokenManager.getToken(this.config);
		}
		String source_channel = "z-ai-sdk-java";
		if (StringUtils.isNotEmpty(config.getSource_channel())) {
			source_channel = config.getSource_channel();
		}
		Request.Builder request = chain.request()
			.newBuilder()
			.header("Authorization", "Bearer " + accessToken)
			.header("x-source-channel", source_channel)
			.header("Zai-SDK-Ver", "0.3.3");
		if (Objects.nonNull(config.getCustomHeaders())) {
			for (Map.Entry<String, String> entry : config.getCustomHeaders().entrySet()) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
		return chain.proceed(request.build());
	}

}
