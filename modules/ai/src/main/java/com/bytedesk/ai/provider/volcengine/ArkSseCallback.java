package com.bytedesk.ai.provider.volcengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volcengine.ark.runtime.Const;
import com.volcengine.ark.runtime.SSEFormatException;
import com.volcengine.ark.runtime.exception.ArkAPIError;
import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.utils.ResponseDecryptUtil;
import com.volcengine.ark.runtime.utils.SSE;

import io.reactivex.FlowableEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * 方舟 SSE 流式响应回调（ArkServiceFactory 专用）。
 *
 * <p>逐行移植自 ark-runtime 0.17.0 的
 * {@code com.volcengine.ark.runtime.utils.ResponseBodyCallback}
 * （Apache License 2.0，© ByteDance Ltd. / affiliates），仅一处差异：
 * Jackson {@link ObjectMapper} 改为 {@link ArkServiceFactory#defaultObjectMapper()}。</p>
 *
 * <p>原因：官方 {@code ResponseBodyCallback} 的静态初始化调用
 * {@code ArkService.defaultObjectMapper()}，而 {@code ArkService} 静态块引用
 * {@code PropertyNamingStrategy.SNAKE_CASE}（Jackson 2.21 移除），在本项目
 * Jackson 2.21.x 下首次加载即抛 {@code NoSuchFieldError}。故此处替换为本地实现，
 * 其余解析/解密/错误处理行为与官方完全一致。</p>
 */
class ArkSseCallback implements Callback<ResponseBody> {

    private static final ObjectMapper MAPPER = ArkServiceFactory.defaultObjectMapper();

    private final FlowableEmitter<SSE> emitter;

    private final boolean emitDone;

    private byte[] key;

    private byte[] nonce;

    private boolean isEncrypted;

    ArkSseCallback(FlowableEmitter<SSE> emitter, boolean emitDone) {
        this.emitter = emitter;
        this.emitDone = emitDone;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        BufferedReader reader = null;

        String requestId = "";
        try {
            requestId = response.headers().get(Const.SERVER_REQUEST_HEADER);
            if (requestId == null || requestId.isEmpty()) {
                Headers headers = response.raw().request().headers();
                requestId = headers.get(Const.CLIENT_REQUEST_HEADER);
            }
        } catch (Exception ignored) {
            // 保留状态码兜底，请求 ID 留空
        }

        try {
            Headers responseHeaders = response.raw().headers();
            String encryptedHeader = responseHeaders.get("X-Is-Encrypted");
            if ("true".equals(encryptedHeader)) {
                this.isEncrypted = true;

                String keyHeader = responseHeaders.get("X-Decryption-Key");
                if (keyHeader != null) {
                    this.key = Base64.getDecoder().decode(keyHeader);
                }

                String nonceHeader = responseHeaders.get("X-Decryption-Nonce");
                if (nonceHeader != null) {
                    this.nonce = Base64.getDecoder().decode(nonceHeader);
                }
            }
        } catch (Exception ignored) {
            // 忽略解密参数读取错误，保持原有配置
        }

        try {
            if (!response.isSuccessful()) {
                HttpException e = new HttpException(response);
                ResponseBody errorBody = response.errorBody();
                String responseBody = null;
                try {
                    if (errorBody != null) {
                        responseBody = errorBody.string();
                    }
                } catch (IOException ignored) {
                    // Preserve status and request ID even if the body cannot be read.
                }
                ArkAPIError error = ArkAPIError.fromResponseBody(MAPPER, responseBody, e.getMessage());
                throw new ArkHttpException(error, e, e.code(), requestId);
            }

            ResponseBody body = response.body();
            if (body == null) {
                ArkAPIError error = ArkAPIError.fromResponseBody(MAPPER, null, "Empty response body");
                throw new ArkHttpException(error, null, response.code(), requestId);
            }
            InputStream in = body.byteStream();
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            SSE sse = null;
            String eventLine = "";

            while (!emitter.isCancelled() && (line = reader.readLine()) != null) {
                if (line.startsWith("event:")) {
                    // Capture the SSE event type; if the server also carries
                    // it in the JSON payload (like the ark-managed-agents
                    // stream endpoints do), SSE.resolveType prefers this
                    // explicit line.
                    eventLine = line.substring("event:".length()).trim();
                    continue;
                } else if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    try {
                        ArkAPIError err = MAPPER.readValue(data, ArkAPIError.class);
                        if (err.getError() != null) {
                            throw new ArkHttpException(err, null, -1, requestId);
                        }
                    } catch (ArkHttpException e) {
                        throw e;
                    } catch (Exception ignored) {
                        // 非 error 载荷，按普通数据继续
                    }

                    if (data.startsWith("[DONE]")) {
                        break;
                    }

                    // 解密数据（如果需要）
                    String processedData = data;
                    if (isEncrypted && key != null && nonce != null) {
                        try {
                            processedData = ResponseDecryptUtil.decryptStreamChunk(data, key, nonce);
                        } catch (Exception ignored) {
                            // 如果解密失败，使用原始数据
                        }
                    }
                    // Resolve type from the SSE event: line first, then
                    // fall back to peeking the JSON payload's top-level
                    // `type` field so callers can dispatch on sse.getType()
                    // without re-parsing sse.getData() themselves.
                    String resolvedType = SSE.resolveType(eventLine, processedData);
                    sse = new SSE(processedData, resolvedType);
                } else if (line.equals("") && sse != null) {
                    if (sse.isDone()) {
                        if (emitDone) {
                            emitter.onNext(sse);
                        }
                        break;
                    }

                    emitter.onNext(sse);
                    sse = null;
                    eventLine = "";
                } else {
                    throw new SSEFormatException("Invalid sse format! " + line);
                }
            }

            emitter.onComplete();

        } catch (Throwable t) {
            onFailure(call, t);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        emitter.onError(t);
    }
}
