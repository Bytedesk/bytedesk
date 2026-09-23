/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-21 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.push.service.PushProviderProperties;
import com.bytedesk.core.push.service.PushProviderProperties.Aliyun;
import com.bytedesk.core.system_config.SystemConfigConsts;
import com.bytedesk.core.system_config.SystemConfigRestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云移动推送（EMAS Push）供应商实现
 *
 * <p>接入方式：HTTP OpenAPI（RPC 风格，签名版本 1.0），不引入阿里云 Java SDK，
 * 依赖仅 JDK HttpClient + Jackson（项目已有），避免为单一能力引入 SDK 全家桶。
 *
 * <p>推送维度：账号（Target=ACCOUNT + TargetValue=userUid）。
 * 移动端登录后 aliyun_push.bindAccount(userUid) 在阿里云侧完成绑定，
 * 服务端无需维护 deviceId 映射（deviceId 仅作 PushDeviceEntity 对账记录，
 * 见 com.bytedesk.core.push_device 包）。
 *
 * <p>凭据来源（AppKey 双链路打通）：
 * AppKey 优先取平台级 SystemConfig 覆盖值（后台「系统配置→推送配置」
 * push.aliyun.android.appKey，与移动端 /api/v1/push/config 同源），
 * 回退 push.properties（bytedesk.push.aliyun.android-app-key，命名带 android
 * 明确仅兑底 Android 通道）；
 * AccessKeyId/AccessKeySecret 仅 properties——Cloud Push API 走 RAM 授权的
 * RPC 签名，AppSecret 仅用于移动端 SDK 初始化，服务端推送不消费。
 *
 * @see <a href="https://help.aliyun.com/document_detail/480853.html">Push API</a>
 * @see <a href="https://help.aliyun.com/document_detail/441100.html">RPC 签名</a>
 */
@Slf4j
@Service
public class AliyunPushDeviceService implements PushDeviceService {

    private static final String PUSH_API_VERSION = "2016-08-01";
    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String SIGNATURE_VERSION = "1.0";
    private static final String DEFAULT_REGION_ID = "cn-hangzhou";
    /** Cloud Push API 必填：NOTICE=通知（通知栏弹出+厂商通道）；MESSAGE=透传消息（进程存活才收到） */
    private static final String PUSH_TYPE_NOTICE = "NOTICE";
    private static final DateTimeFormatter ISO8601_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final PushProviderProperties pushProviderProperties;
    private final SystemConfigRestService systemConfigRestService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public AliyunPushDeviceService(PushProviderProperties pushProviderProperties,
            SystemConfigRestService systemConfigRestService) {
        this.pushProviderProperties = pushProviderProperties;
        this.systemConfigRestService = systemConfigRestService;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String getProviderName() {
        return PushProviderProperties.PROVIDER_ALIYUN;
    }

    @Override
    public boolean isAvailable() {
        return isServerPushConfigured(resolveEffectiveAppKey());
    }

    /**
     * 解析服务端推送生效 AppKey：SystemConfig 覆盖值优先，回退 properties。
     *
     * <p>账号维度推送按平台区分 AppKey（EMAS 中 Android/iOS 为独立应用），
     * 本通道面向 Android（iOS 走 apns-direct），故取 Android AppKey。
     * SystemConfig 读取失败时静默回退，不影响 properties 配置的存量部署。
     */
    private String resolveEffectiveAppKey() {
        try {
            Map<String, String> overrides = systemConfigRestService
                    .getOverrideValues(SystemConfigConsts.PLATFORM_CONFIG_ORG_UID);
            String dbAppKey = overrides.get(SystemConfigConsts.KEY_PUSH_ALIYUN_ANDROID_APP_KEY);
            if (StringUtils.hasText(dbAppKey)) {
                return dbAppKey;
            }
        } catch (Exception e) {
            log.warn("Resolve aliyun appKey from system config failed, fallback to properties: {}",
                    e.getMessage());
        }
        return pushProviderProperties.getAliyun().getAndroidAppKey();
    }

    /**
     * 服务端 Cloud Push API 凭据是否齐备：
     * AppKey 可来自 SystemConfig 覆盖或 properties；AK/SK 仅 properties（RPC 签名必需）。
     * 注意：AppSecret 服务端推送不消费（仅移动端 SDK 初始化用），不参与判定。
     */
    private boolean isServerPushConfigured(String appKey) {
        Aliyun config = pushProviderProperties.getAliyun();
        return StringUtils.hasText(appKey)
                && StringUtils.hasText(config.getAccessKeyId())
                && StringUtils.hasText(config.getAccessKeySecret());
    }

    /** 缺失凭据的具体指引（供调用方/管理后台测试推送直接展示） */
    private String describeMissingCredentials(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return "aliyun appKey missing: configure '推送配置' in admin SystemConfig (push.aliyun.android.appKey)"
                    + " or bytedesk.push.aliyun.app-key in push.properties";
        }
        return "aliyun AccessKey missing: Cloud Push API requires account AccessKey for RPC signing,"
                + " configure bytedesk.push.aliyun.access-key-id / access-key-secret in push.properties"
                + " (AppSecret in SystemConfig is for mobile SDK init only)";
    }

    @Override
    public PushDeviceResult pushToUser(String userUid, String title, String body, Map<String, String> extras) {
        if (!StringUtils.hasText(userUid) || !StringUtils.hasText(title)) {
            return PushDeviceResult.failure(getProviderName(), "userUid or title is empty");
        }
        Aliyun config = pushProviderProperties.getAliyun();
        String appKey = resolveEffectiveAppKey();
        if (!isServerPushConfigured(appKey)) {
            return PushDeviceResult.failure(getProviderName(), describeMissingCredentials(appKey));
        }

        long startTime = System.currentTimeMillis();
        try {
            Map<String, String> params = buildPushParams(config, appKey, userUid, title, body, extras);
            String response = executeSignedPost(config, params);
            PushDeviceResult result = parseResponse(response);

            if (result.isSuccess()) {
                log.info("Aliyun push success, receiver={}, threadUid={}, elapsedMs={}, messageId={}",
                        userUid, extras != null ? extras.get("threadUid") : null,
                        System.currentTimeMillis() - startTime, result.getMessageId());
            } else {
                log.error("Aliyun push failed, receiver={}, threadUid={}, elapsedMs={}, error={}",
                        userUid, extras != null ? extras.get("threadUid") : null,
                        System.currentTimeMillis() - startTime, result.getError());
            }
            return result;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Aliyun push error, receiver={}, error={}", userUid, e.getMessage(), e);
            return PushDeviceResult.failure(getProviderName(), e.getMessage());
        } catch (Exception e) {
            log.error("Aliyun push error, receiver={}, error={}", userUid, e.getMessage(), e);
            return PushDeviceResult.failure(getProviderName(), e.getMessage());
        }
    }

    /** 组装 Push API 业务参数（公共参数在签名时统一合入；appKey 为解析后的生效值） */
    private Map<String, String> buildPushParams(Aliyun config, String appKey, String userUid,
            String title, String body, Map<String, String> extras) throws IOException {
        Map<String, String> params = new TreeMap<>();
        params.put("Action", "Push");
        params.put("AppKey", appKey);
        // 账号维度推送：移动端 bindAccount(userUid)
        params.put("Target", "ACCOUNT");
        params.put("TargetValue", userUid);
        params.put("DeviceType", config.getDeviceType());
        // Push API 必填参数：NOTICE=通知（通知栏弹出，走厂商通道）/ MESSAGE=透传消息。
        // 坐席业务通知场景固定 NOTICE（漏传报 MissingPushType: PushType is mandatory）
        params.put("PushType", PUSH_TYPE_NOTICE);
        params.put("Title", title);
        params.put("Body", body == null ? "" : body);
        // Android 通知形态 + 点击跳转
        params.put("AndroidNotifyType", config.getAndroidNotifyType());
        if (StringUtils.hasText(config.getAndroidActivity())) {
            params.put("AndroidActivity", config.getAndroidActivity());
        }
        // iOS APNs 环境映射，避免开发环境推送到生产证书
        if ("IOS".equalsIgnoreCase(config.getDeviceType()) || "ALL".equalsIgnoreCase(config.getDeviceType())) {
            params.put("iOSApnsEnv", config.getIosApnsEnv());
            params.put("iOSRemind", "true");
        }
        // 业务扩展字段：点击通知后透传给 App（threadUid/type 等）
        if (extras != null && !extras.isEmpty()) {
            Map<String, Object> extParameters = Map.of("bytedesk", extras);
            params.put("ExtParameters", objectMapper.writeValueAsString(extParameters));
        }
        return params;
    }

    /**
     * 执行 RPC 签名请求（签名版本 1.0）：
     * stringToSign = POST&%2F&percentEncode(canonicalQuery)，
     * signature = Base64(HmacSha1(accessKeySecret + "&", stringToSign))。
     */
    private String executeSignedPost(Aliyun config, Map<String, String> bizParams) throws Exception {
        Map<String, String> signedParams = new TreeMap<>(bizParams);
        // 公共参数
        signedParams.put("Format", "JSON");
        signedParams.put("Version", PUSH_API_VERSION);
        signedParams.put("AccessKeyId", config.getAccessKeyId());
        signedParams.put("SignatureMethod", SIGNATURE_METHOD);
        signedParams.put("Timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(ISO8601_FORMATTER));
        signedParams.put("SignatureVersion", SIGNATURE_VERSION);
        signedParams.put("SignatureNonce", UUID.randomUUID().toString());
        signedParams.put("RegionId", DEFAULT_REGION_ID);

        // 1. 规范化查询串（参数按字典序）
        StringBuilder canonicalQuery = new StringBuilder();
        for (Map.Entry<String, String> entry : signedParams.entrySet()) {
            if (canonicalQuery.length() > 0) {
                canonicalQuery.append('&');
            }
            canonicalQuery.append(percentEncode(entry.getKey()))
                    .append('=')
                    .append(percentEncode(entry.getValue()));
        }

        // 2. 计算签名
        StringBuilder stringToSign = new StringBuilder()
                .append("POST").append('&')
                .append(percentEncode("/")).append('&')
                .append(percentEncode(canonicalQuery.toString()));
        String signature = sign(config.getAccessKeySecret() + "&", stringToSign.toString());

        // 3. 发起请求
        String requestBody = canonicalQuery + "&Signature=" + percentEncode(signature);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + config.getEndpoint() + "/"))
                .timeout(Duration.ofSeconds(Math.max(1, config.getTimeoutSeconds())))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /** 解析 OpenAPI 响应：成功时无 Code 字段，失败时含 Code/Message */
    private PushDeviceResult parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("Code")) {
                String code = root.path("Code").asText();
                String message = root.path("Message").asText();
                return PushDeviceResult.failure(getProviderName(), code + ": " + message);
            }
            return PushDeviceResult.success(
                    getProviderName(),
                    root.path("MessageId").asText(null),
                    root.path("RequestId").asText(null));
        } catch (Exception e) {
            return PushDeviceResult.failure(getProviderName(),
                    "parse response failed: " + e.getMessage() + ", body=" + abbreviate(responseBody));
        }
    }

    private String sign(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }

    /** RFC3986 百分号编码（阿里云 RPC 签名要求：+ → %20、* → %2A、%7E → ~） */
    private String percentEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            throw new IllegalArgumentException("percentEncode failed: " + e.getMessage(), e);
        }
    }

    private String abbreviate(String text) {
        if (text == null) {
            return null;
        }
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }
}
