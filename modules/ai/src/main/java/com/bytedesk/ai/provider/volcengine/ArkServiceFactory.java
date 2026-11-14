package com.bytedesk.ai.provider.volcengine;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.volcengine.ark.runtime.Const;
import com.volcengine.ark.runtime.exception.ArkAPIError;
import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.interceptor.AuthenticationInterceptor;
import com.volcengine.ark.runtime.interceptor.RetryInterceptor;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.models.chat.ChatCompletionResponse;
import com.volcengine.ark.runtime.models.chat.ChatCompletionStreamResponse;
import com.volcengine.ark.runtime.service.ArkApi;
import com.volcengine.ark.runtime.service.ArkBaseService;
import com.volcengine.ark.runtime.utils.SSE;
import com.volcengine.ark.runtime.utils.UnknownVariantHandler;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * 方舟官方 SDK 客户端工厂。
 *
 * <p>与官方 {@code ArkService}（com.volcengine:ark-runtime 0.17.0）功能等价、
 * 实现逐行对齐，仅有一处差异：Jackson 命名策略改用
 * {@link PropertyNamingStrategies#SNAKE_CASE}。</p>
 *
 * <p>原因：官方 0.17.0 的 {@code ArkService} 静态初始化引用
 * {@code PropertyNamingStrategy.SNAKE_CASE}（Jackson 2.13 起废弃、2.21 移除），
 * 本项目 Jackson 2.21.2 下该类无法加载（NoSuchFieldError）。官方在
 * volcengine-java-sdk 2.0.0 已修复此问题，但新版 ark-runtime 尚未跟进
 * （见 volcengine/volcengine-java-sdk#126、#132）。{@code ArkApi}、拦截器、
 * 模型类（{@code models.chat.*}）本身均安全，故此处仅替换便捷包装类；
 * 同理 SDK 的 {@code ResponseBodyCallback} 静态初始化会回调
 * {@code ArkService.defaultObjectMapper()}，改用本地移植
 * {@link ArkSseCallback}；{@code RequestIdInterceptor} 依赖被排除的
 * commons-lang 2.x，改用本地移植 {@link ArkRequestIdInterceptor}。</p>
 *
 * <p>用法与官方一致：{@code ArkServiceFactory.createService(apiKey, baseUrl)}
 * 返回的 {@link ArkApi} + 本工厂的静态方法完成同步/流式调用。</p>
 */
public final class ArkServiceFactory {

    private ArkServiceFactory() {
    }

    /** 与官方 ArkService.defaultObjectMapper() 一致（仅 SNAKE_CASE 引用修复）。 */
    public static ObjectMapper defaultObjectMapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addHandler(new UnknownVariantHandler())
                .defaultPropertyInclusion(JsonInclude.Value.construct(
                        JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }

    /** 与官方 ArkService.defaultApiKeyClient() 对齐（apiKey 鉴权 + 重试；
     * RequestIdInterceptor 因 commons-lang 2.x 依赖换成本地移植，见 {@link ArkRequestIdInterceptor}）。 */
    public static OkHttpClient defaultApiKeyClient(String apiKey) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(apiKey))
                .addInterceptor(new ArkRequestIdInterceptor())
                .addInterceptor(new RetryInterceptor(ArkBaseService.DEFAULT_RETRY_TIMES))
                .connectionPool(new OkHttpClient().connectionPool())
                .readTimeout(ArkBaseService.DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static ArkApi createApi(String apiKey, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ensureTrailingSlash(baseUrl))
                .client(defaultApiKeyClient(apiKey))
                .addConverterFactory(JacksonConverterFactory.create(defaultObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(ArkApi.class);
    }

    /** 同步对话补全（与官方 ArkService.createChatCompletion 等价，含 HttpException 翻译）。 */
    public static ChatCompletionResponse createChatCompletion(ArkApi api, ChatCompletionRequest request) {
        return execute(api.createChatCompletion(request, request.getModel(), new HashMap<>()));
    }

    /** 流式对话补全（与官方 ArkService.streamChatCompletion 等价：stream=true + SSE 解析）。 */
    public static Flowable<ChatCompletionStreamResponse> streamChatCompletion(
            ArkApi api, ChatCompletionRequest request) {
        request.setStream(true);
        return stream(api.streamChatCompletion(request, request.getModel(), new HashMap<>()),
                ChatCompletionStreamResponse.class);
    }

    /** 与官方 ArkService.execute(Single) 一致（HttpException → ArkHttpException）。 */
    public static <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            throw translateHttpException(e);
        }
    }

    /** 与官方 ArkService.stream(Call, Class) 一致。 */
    public static <T> Flowable<T> stream(Call<okhttp3.ResponseBody> apiCall, Class<T> cl) {
        return stream(apiCall).map(sse -> defaultObjectMapper().readValue(sse.getData(), cl));
    }

    /** 与官方 ArkService.stream(Call) 一致（仅 ResponseBodyCallback 换成本地 ArkSseCallback）。 */
    public static Flowable<SSE> stream(Call<okhttp3.ResponseBody> apiCall) {
        return Flowable.create(
                emitter -> apiCall.enqueue(new ArkSseCallback(emitter, false)),
                BackpressureStrategy.BUFFER);
    }

    /** 与官方 ArkService.translateHttpException 一致。 */
    private static ArkHttpException translateHttpException(HttpException exception) {
        String requestId = "";
        String responseBody = null;
        try {
            Response<?> response = exception.response();
            if (response != null) {
                requestId = response.headers().get(Const.SERVER_REQUEST_HEADER);
                if (responseIdBlank(requestId)) {
                    requestId = response.raw() != null && response.raw().request().header(Const.CLIENT_REQUEST_HEADER) != null
                            ? response.raw().request().header(Const.CLIENT_REQUEST_HEADER) : "";
                }
            }
        } catch (Exception ignored) {
            // 保留状态码与请求 ID 兜底
        }
        try {
            Response<?> response = exception.response();
            if (response != null) {
                okhttp3.ResponseBody errorBody = response.errorBody();
                if (errorBody != null) {
                    responseBody = errorBody.string();
                }
            }
        } catch (IOException ignored) {
            // 读取失败时保留状态码
        }
        ArkAPIError error = ArkAPIError.fromResponseBody(defaultObjectMapper(), responseBody, exception.getMessage());
        return new ArkHttpException(error, exception, exception.code(), requestId);
    }

    private static boolean responseIdBlank(String requestId) {
        return requestId == null || requestId.isEmpty();
    }

    private static String ensureTrailingSlash(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return ArkBaseService.BASE_URL + "/";
        }
        String trimmed = baseUrl.trim();
        return trimmed.endsWith("/") ? trimmed : trimmed + "/";
    }
}
