package com.bytedesk.ai.provider.volcengine;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import com.volcengine.ark.runtime.exception.ArkAPIError;
import com.volcengine.ark.runtime.exception.ArkHttpException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 方舟请求 ID / User-Agent 拦截器（ArkServiceFactory 专用）。
 *
 * <p>逐行移植自 ark-runtime 0.17.0 的
 * {@code com.volcengine.ark.runtime.interceptor.RequestIdInterceptor}
 * （Apache License 2.0，© ByteDance Ltd. / affiliates），仅一处差异：
 * 请求 ID 随机后缀由 commons-lang 2.x {@code RandomStringUtils.randomAlphanumeric(20)}
 * 改为 JDK 实现（字符集与语义一致）。</p>
 *
 * <p>原因：官方 {@code RequestIdInterceptor} 依赖 {@code org.apache.commons.lang.RandomStringUtils}
 * （commons-lang 2.x），该项目为排除冲突依赖 {@code volcengine-java-sdk-ark} 后类路径上不存在，
 * 首次请求即抛 {@code NoClassDefFoundError}。拦截顺序与官方
 * {@code ArkService.defaultApiKeyClient} 一致：Authentication → RequestId → Retry。</p>
 */
class ArkRequestIdInterceptor implements Interceptor {

    private static final String VERSION = loadVersion();

    ArkRequestIdInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String clientRequestId = chain.request().header("X-Client-Request-Id");
        if (clientRequestId == null || clientRequestId.length() == 0) {
            builder.header("X-Client-Request-Id", genRequestId());
        }
        builder.header("User-Agent", getUserAgent());

        Request request = builder.build();
        try {
            return chain.proceed(request);
        } catch (Exception e) {
            String requestId = request.header("X-Client-Request-Id");
            ArkAPIError error = new ArkAPIError(new ArkAPIError.ArkErrorDetails(
                    e.getMessage(), "", "", ""));
            throw new ArkHttpException(error, e, ArkHttpException.INTERNAL_SERVICE_CODE, requestId);
        }
    }

    /** 与官方一致：yyyyMMddhhmmss 时间戳 + 20 位随机字母数字。 */
    private static String genRequestId() {
        return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + randomAlphanumeric(20);
    }

    /** commons-lang 2.x RandomStringUtils.randomAlphanumeric 的 JDK 等价实现。 */
    private static String randomAlphanumeric(int count) {
        final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(count);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** 与官方一致：ark-runtime-java/<version>/(<java>;<os>;<arch>)。 */
    private static String getUserAgent() {
        String os = System.getProperty("os.name") + "-" + System.getProperty("os.version");
        String javaVersion = "java-" + System.getProperty("java.version");
        String arch = System.getProperty("os.arch");
        return String.format("%s/%s/(%s;%s;%s)", "ark-runtime-java", VERSION, javaVersion, os, arch);
    }

    /** 与官方 SdkVersion.loadVersion 一致：读 sdk-version.properties，失败回退 dev。 */
    private static String loadVersion() {
        try (InputStream in = ArkRequestIdInterceptor.class
                .getResourceAsStream("/com/volcengine/ark/runtime/sdk-version.properties")) {
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                String version = properties.getProperty("version", "").trim();
                if (!version.isEmpty() && !version.contains("${")) {
                    return version;
                }
            }
        } catch (IOException ignored) {
            // 回退 dev
        }
        return "dev";
    }
}
