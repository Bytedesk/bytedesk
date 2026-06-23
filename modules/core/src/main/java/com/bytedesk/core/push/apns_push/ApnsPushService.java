package com.bytedesk.core.push.apns_push;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.TextContent;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.push.apns_p12.ApnsP12Entity;
import com.bytedesk.core.push.apns_p12.ApnsP12RestService;
import com.bytedesk.core.push.apns_token.ApnsTokenEntity;
import com.bytedesk.core.push.apns_token.ApnsTokenRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.google.common.base.Strings;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Pushy
 * 
 * @see <a href=
 *      "https://github.com/relayrides/pushy/wiki/Best-practices">pushy</a>
 * @see <a href="https://github.com/jchambers/pushy">pushy2</a>
 * @see <a href="https://juejin.im/entry/5b4d4eba5188257bcc165e07">juejin</a>
 *
 * @author kefux.com on 2019/3/18
 */
@Slf4j
@Service
@AllArgsConstructor
public class ApnsPushService {

    private final EventLoopGroup apnsEventLoopGroup = new NioEventLoopGroup(1);

    private final Map<String, ApnsClient> apnsClientCache = new ConcurrentHashMap<>();

    private final ApnsTokenRestService apnsTokenRestService;

    private final ApnsP12RestService apnsP12RestService;

    private final ApnsPushRepository apnsPushRepository;

    private final UidUtils uidUtils;

    @Transactional
    public void pushMessageToUser(String receiverUid, MessageProtobuf message) {
        if (!StringUtils.hasText(receiverUid) || message == null) {
            log.debug("Skip APNS push because receiverUid or message is empty, receiverUid={}, messageUid={}",
                    receiverUid, message != null ? message.getUid() : null);
            return;
        }

        ThreadProtobuf thread = message.getThread();
        UserProtobuf sender = message.getUser();

        List<ApnsTokenEntity> apnsTokens = apnsTokenRestService.findByUserUid(receiverUid);
        if (apnsTokens == null || apnsTokens.isEmpty()) {
            log.info(
                    "Skip APNS push because no token found, receiverUid={}, messageUid={}, threadUid={}, threadType={}, senderUid={}",
                    receiverUid,
                    message.getUid(),
                    thread != null ? thread.getUid() : null,
                    thread != null ? thread.getType() : null,
                    sender != null ? sender.getUid() : null);
            return;
        }

        String title = buildNotificationTitle(message);
        String body = buildNotificationBody(message);

        log.info(
                "Prepare APNS push, receiverUid={}, messageUid={}, threadUid={}, threadType={}, senderUid={}, tokenCount={}",
                receiverUid,
                message.getUid(),
                thread != null ? thread.getUid() : null,
                thread != null ? thread.getType() : null,
                sender != null ? sender.getUid() : null,
                apnsTokens.size());

        for (ApnsTokenEntity apnsToken : apnsTokens) {
            pushWithToken(apnsToken, title, body, message);
        }
    }

    @Transactional
    public ApnsPushEntity pushWithToken(ApnsTokenEntity apnsToken, String title, String content, MessageProtobuf message) {
        ApnsPushEntity record = buildPushRecord(apnsToken, title, content, message);
        return pushWithTokenInternal(apnsToken, title, content, record);
    }

    /**
     * Push a notification to a user's iOS devices without requiring a MessageProtobuf.
     * Used for business notifications such as ticket status changes.
     *
     * @param receiverUid the user uid to push to
     * @param title       notification title
     * @param body        notification body
     * @param ticketUid   related ticket uid (for tracking), can be null
     */
    @Transactional
    public void pushNotificationToUser(String receiverUid, String title, String body, String ticketUid) {
        if (!StringUtils.hasText(receiverUid)) {
            log.debug("Skip APNS notification push because receiverUid is empty");
            return;
        }

        List<ApnsTokenEntity> apnsTokens = apnsTokenRestService.findByUserUid(receiverUid);
        if (apnsTokens == null || apnsTokens.isEmpty()) {
            log.info("Skip APNS notification push because no token found, receiverUid={}", receiverUid);
            return;
        }

        log.info("Prepare APNS notification push, receiverUid={}, tokenCount={}, ticketUid={}",
                receiverUid, apnsTokens.size(), ticketUid);

        for (ApnsTokenEntity apnsToken : apnsTokens) {
            ApnsPushEntity record = buildNotificationPushRecord(apnsToken, title, body, ticketUid);
            record = apnsPushRepository.save(record);
            pushWithTokenInternal(apnsToken, title, body, record);
        }
    }

    @Transactional
    public ApnsPushEntity pushWithTokenInternal(ApnsTokenEntity apnsToken, String title, String content, ApnsPushEntity record) {
        record = apnsPushRepository.save(record);

        log.debug("Create APNS push record, recordUid={}, messageUid={}, receiver={}, token={}, p12Uid={}",
                record.getUid(),
                record.getMessageUid(),
                record.getReceiver(),
                maskToken(record.getDeviceToken()),
                record.getP12Uid());

        if (apnsToken == null || !StringUtils.hasText(apnsToken.getToken())) {
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), "APNS token is missing");
        }

        if (!StringUtils.hasText(apnsToken.getP12Uid())) {
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), "APNS p12 binding(p12Uid) is missing");
        }

        Optional<ApnsP12Entity> apnsP12Optional = apnsP12RestService.findByUid(apnsToken.getP12Uid());
        if (apnsP12Optional.isEmpty()) {
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), "APNS p12 certificate not found");
        }

        ApnsP12Entity apnsP12 = apnsP12Optional.get();
        record.setP12Uid(apnsP12.getUid());
        record.setBundleId(apnsP12.getBundleId());
        record.setSandbox(apnsP12.getSandbox());
        apnsPushRepository.save(record);

        log.debug("Resolved APNS certificate, recordUid={}, p12Uid={}, bundleId={}, sandbox={}",
            record.getUid(),
            apnsP12.getUid(),
            apnsP12.getBundleId(),
            apnsP12.getSandbox());

        if (!Boolean.TRUE.equals(apnsP12.getEnabled())) {
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), "APNS p12 certificate is disabled");
        }

        if (!StringUtils.hasText(apnsP12.getBundleId()) || !StringUtils.hasText(apnsP12.getP12Url())) {
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), "APNS p12 certificate is incomplete");
        }

        return push(apnsToken.getToken(), title, content, 1, apnsP12, record);
    }

    /**
     * 推送一条消息
     * payloadBuilder.setContentAvailable(false);
     *
     * @param deviceToken      token
     * @param nickname         title
     * @param content          content
     * @param bundleIdentifier bundle id
     * @param p12Url           url
     * @param p12Password      password
     */
    public ApnsPushEntity push(String deviceToken, String nickname, String content, int badgeNumber, ApnsP12Entity apnsP12,
            ApnsPushEntity record) {
        if (Strings.isNullOrEmpty(deviceToken)) {
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), "device token is empty");
        }

        ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
        payloadBuilder.setAlertBody(content);
        payloadBuilder.setAlertTitle(nickname);
        payloadBuilder.setBadgeNumber(badgeNumber);
        payloadBuilder.setSound("default");

        String payload = payloadBuilder.build();
        final String token = TokenUtil.sanitizeTokenString(deviceToken);
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, apnsP12.getBundleId(), payload);

        log.debug("Send APNS notification, recordUid={}, messageUid={}, receiver={}, token={}, bundleId={}, payloadLength={}",
            record.getUid(),
            record.getMessageUid(),
            record.getReceiver(),
            maskToken(token),
            apnsP12.getBundleId(),
            payload.length());

        long startTime = System.currentTimeMillis();
        try {
            ApnsClient apnsClient = getApnsClient(apnsP12);
            if (apnsClient == null) {
                return markPushResult(record, false, PushStatusEnum.ERROR.name(), "failed to create APNS client");
            }

            final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture = apnsClient
                    .sendNotification(pushNotification);

            sendNotificationFuture.whenComplete((pushNotificationResponse, throwable) -> {
                try {
                    if (throwable != null) {
                        log.error("Failed to send APNS push notification asynchronously, recordUid={}, messageUid={}, elapsedMs={}",
                                record.getUid(),
                                record.getMessageUid(),
                                System.currentTimeMillis() - startTime,
                                throwable);
                        markPushResult(record, false, PushStatusEnum.ERROR.name(), throwable.getMessage());
                        return;
                    }

                    if (pushNotificationResponse != null && pushNotificationResponse.isAccepted()) {
                        log.info("APNS push success, recordUid={}, messageUid={}, receiver={}, token={}, bundleId={}, elapsedMs={}",
                                record.getUid(),
                                record.getMessageUid(),
                                record.getReceiver(),
                                maskToken(token),
                                apnsP12.getBundleId(),
                                System.currentTimeMillis() - startTime);
                        markPushResult(record, true, PushStatusEnum.SUCCESS.name(), "APNS push accepted");
                        return;
                    }

                    String rejectionReason = pushNotificationResponse != null
                            ? pushNotificationResponse.getRejectionReason().orElse("APNS push rejected")
                            : "APNS push rejected";
                    log.error("APNS push rejected, recordUid={}, messageUid={}, receiver={}, token={}, reason={}, elapsedMs={}",
                            record.getUid(),
                            record.getMessageUid(),
                            record.getReceiver(),
                            maskToken(token),
                            rejectionReason,
                            System.currentTimeMillis() - startTime);
                    if (pushNotificationResponse != null) {
                        pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                            log.error("APNS token invalid, recordUid={}, token={}, since={}",
                                    record.getUid(),
                                    maskToken(token),
                                    timestamp);
                        });
                    }
                    markPushResult(record, false, PushStatusEnum.ERROR.name(), rejectionReason);
                } catch (Exception callbackException) {
                    log.error("Failed to finalize APNS push result, recordUid={}, messageUid={}",
                            record.getUid(),
                            record.getMessageUid(),
                            callbackException);
                }
            });

            log.debug("APNS push queued asynchronously, recordUid={}, messageUid={}, receiver={}, token={}",
                    record.getUid(),
                    record.getMessageUid(),
                    record.getReceiver(),
                    maskToken(token));
            return record;

        } catch (final Exception e) {
            log.error("Failed to send APNS push notification, messageUid={}", record.getMessageUid(), e);
            return markPushResult(record, false, PushStatusEnum.ERROR.name(), e.getMessage());
        }

    }

    private ApnsClient getApnsClient(ApnsP12Entity apnsP12) {
        String cacheKey = buildApnsClientCacheKey(apnsP12);
        ApnsClient cachedClient = apnsClientCache.get(cacheKey);
        if (cachedClient != null) {
            log.debug("Reuse APNS client from cache, p12Uid={}, bundleId={}, sandbox={}",
                    apnsP12.getUid(),
                    apnsP12.getBundleId(),
                    apnsP12.getSandbox());
            return cachedClient;
        }

        synchronized (apnsClientCache) {
            cachedClient = apnsClientCache.get(cacheKey);
            if (cachedClient != null) {
                log.debug("Reuse APNS client from cache after lock, p12Uid={}, bundleId={}, sandbox={}",
                        apnsP12.getUid(),
                        apnsP12.getBundleId(),
                        apnsP12.getSandbox());
                return cachedClient;
            }

            ApnsClient newClient = createApnsClient(apnsP12);
            if (newClient != null) {
                apnsClientCache.put(cacheKey, newClient);
                log.info("Created APNS client cache entry, p12Uid={}, bundleId={}, sandbox={}",
                        apnsP12.getUid(),
                        apnsP12.getBundleId(),
                        apnsP12.getSandbox());
            }
            return newClient;
        }
    }

    private ApnsClient createApnsClient(ApnsP12Entity apnsP12) {
        String apnsServer = Boolean.TRUE.equals(apnsP12.getSandbox()) ? ApnsClientBuilder.DEVELOPMENT_APNS_HOST
                : ApnsClientBuilder.PRODUCTION_APNS_HOST;

        try (InputStream inputStream = openP12InputStream(apnsP12.getP12Url())) {
            return new ApnsClientBuilder().setApnsServer(apnsServer)
                    .setClientCredentials(inputStream, apnsP12.getP12Password())
                    .setConcurrentConnections(1)
                    .setEventLoopGroup(apnsEventLoopGroup)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create APNS client for p12 {}", apnsP12.getUid(), e);
            return null;
        }
    }

    private InputStream openP12InputStream(String p12Url) throws Exception {
        if (!StringUtils.hasText(p12Url)) {
            throw new IllegalArgumentException("p12Url is required");
        }

        if (p12Url.startsWith("http://") || p12Url.startsWith("https://") || p12Url.startsWith("file:")) {
            return URI.create(p12Url).toURL().openStream();
        }

        return Files.newInputStream(Path.of(p12Url));
    }

    private ApnsPushEntity buildPushRecord(ApnsTokenEntity apnsToken, String title, String content, MessageProtobuf message) {
        UserProtobuf sender = message != null ? message.getUser() : null;
        ThreadProtobuf thread = message != null ? message.getThread() : null;
        return ApnsPushEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(apnsToken != null ? apnsToken.getOrgUid() : null)
                .userUid(apnsToken != null ? apnsToken.getUserUid() : null)
                .name(title)
                .sender(sender != null ? sender.getUid() : null)
                .receiver(apnsToken != null ? apnsToken.getUserUid() : null)
                .deviceToken(apnsToken != null ? apnsToken.getToken() : null)
                .p12Uid(apnsToken != null ? apnsToken.getP12Uid() : null)
                .messageUid(message != null ? message.getUid() : null)
                .threadUid(thread != null ? thread.getUid() : null)
                .content(content)
                .type(ApnsPushTypeEnum.MESSAGE.name())
                .status(PushStatusEnum.PENDING.name())
                .channel(ChannelEnum.IOS.name())
                .build();
    }

    /**
     * Build a push record for business notifications (e.g., ticket status changes).
     * Does not require a MessageProtobuf.
     */
    private ApnsPushEntity buildNotificationPushRecord(ApnsTokenEntity apnsToken, String title, String content, String ticketUid) {
        return ApnsPushEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(apnsToken != null ? apnsToken.getOrgUid() : null)
                .userUid(apnsToken != null ? apnsToken.getUserUid() : null)
                .name(title)
                .receiver(apnsToken != null ? apnsToken.getUserUid() : null)
                .deviceToken(apnsToken != null ? apnsToken.getToken() : null)
                .p12Uid(apnsToken != null ? apnsToken.getP12Uid() : null)
                .messageUid(ticketUid)
                .content(content)
                .type(ApnsPushTypeEnum.TICKET.name())
                .status(PushStatusEnum.PENDING.name())
                .channel(ChannelEnum.IOS.name())
                .build();
    }

    private ApnsPushEntity markPushResult(ApnsPushEntity record, boolean sendSuccess, String status, String sendMessage) {
        record.setSendSuccess(sendSuccess);
        record.setStatus(status);
        record.setSendMessage(sendMessage);
        log.debug("Update APNS push result, recordUid={}, status={}, sendSuccess={}, message={}",
                record.getUid(),
                status,
                sendSuccess,
                sendMessage);
        return apnsPushRepository.save(record);
    }

    private void closeApnsClient(ApnsClient apnsClient) {
        if (apnsClient == null) {
            return;
        }
        try {
            apnsClient.close().get();
        } catch (Exception e) {
            log.warn("Failed to close APNS client cleanly", e);
        }
    }

    private String buildApnsClientCacheKey(ApnsP12Entity apnsP12) {
        return String.join("|",
                defaultString(apnsP12.getUid()),
                defaultString(apnsP12.getBundleId()),
                defaultString(apnsP12.getP12Url()),
                defaultString(apnsP12.getP12Password()),
                String.valueOf(Boolean.TRUE.equals(apnsP12.getSandbox())));
    }

    private String defaultString(String value) {
        return value != null ? value : "";
    }

    @PreDestroy
    public void shutdownApnsResources() {
        for (ApnsClient apnsClient : apnsClientCache.values()) {
            closeApnsClient(apnsClient);
        }
        apnsClientCache.clear();
        apnsEventLoopGroup.shutdownGracefully();
    }

    private String buildNotificationTitle(MessageProtobuf message) {
        if (message == null || message.getUser() == null || !StringUtils.hasText(message.getUser().getNickname())) {
            return "新消息";
        }
        return message.getUser().getNickname();
    }

    private String buildNotificationBody(MessageProtobuf message) {
        if (message == null || message.getType() == null) {
            return "您有一条新消息";
        }

        String body;
        switch (message.getType()) {
            case TEXT:
                TextContent textContent = TextContent.fromJson(message.getContent());
                body = textContent != null ? textContent.getText() : message.getContent();
                break;
            case IMAGE:
                body = "[图片]";
                break;
            case FILE:
            case DOCUMENT:
                body = "[文件]";
                break;
            case AUDIO:
            case VOICE:
                body = "[语音]";
                break;
            case VIDEO:
                body = "[视频]";
                break;
            default:
                body = message.getContent();
                break;
        }

        if (!StringUtils.hasText(body)) {
            body = "您有一条新消息";
        }
        return body.length() > 120 ? body.substring(0, 120) : body;
    }

    private String maskToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        if (token.length() <= 8) {
            return "****";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }


}
