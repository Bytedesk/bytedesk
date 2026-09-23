package com.bytedesk.core.push_apns;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PreDestroy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.TextContent;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.push_apns_p12.ApnsP12Entity;
import com.bytedesk.core.push_apns_p12.ApnsP12RestService;
import com.bytedesk.core.push_apns_token.ApnsTokenEntity;
import com.bytedesk.core.push_apns_token.ApnsTokenRestService;
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
public class PushApnsService {

    /**
     * 注意：Pushy 0.15.x 通过事件循环组的「类名」映射 Netty 通道实现（ClientChannelClassUtil），
     * 不识别 Netty 4.2 新增的 MultiThreadIoEventLoopGroup，会抛出
     * "No datagram channel class found for event loop group type: io.netty.channel.MultiThreadIoEventLoopGroup"。
     * 在 Pushy 发布兼容 Netty 4.2 的版本之前，这里必须继续使用 NioEventLoopGroup
     * （Netty 4.2 中已标记 @Deprecated 但仍可用），不能改为 MultiThreadIoEventLoopGroup。
     */
    @SuppressWarnings("deprecation")
    private final EventLoopGroup apnsEventLoopGroup = new NioEventLoopGroup(1);

    private final Map<String, ApnsClient> apnsClientCache = new ConcurrentHashMap<>();

    private final ApnsTokenRestService apnsTokenRestService;

    private final ApnsP12RestService apnsP12RestService;

    private final PushApnsRepository apnsPushRepository;

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
    public PushApnsEntity pushWithToken(ApnsTokenEntity apnsToken, String title, String content, MessageProtobuf message) {
        PushApnsEntity record = buildPushRecord(apnsToken, title, content, message);
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
            PushApnsEntity record = buildNotificationPushRecord(apnsToken, title, body, ticketUid);
            record = apnsPushRepository.save(record);
            pushWithTokenInternal(apnsToken, title, body, record);
        }
    }

    /**
     * 同步版通知推送（用于管理后台测试推送）：发送后轮询等待真实推送结果，便于调用方立即拿到成败。
     * 注意：与 {@link #pushNotificationToUser(String, String, String, String)} 不同，本方法不加事务，
     * 保证推送记录立即落库可见，且等待轮询不占用长事务。
     *
     * @param timeoutMillis 等待最终结果的超时时间（毫秒），超时后返回当前记录（可能仍为 PENDING）
     * @return 最后一条推送记录（含最终 status/sendMessage）；无 token 等场景返回 null
     */
    public PushApnsEntity pushNotificationToUserForResult(String receiverUid, String title, String body, String ticketUid,
            long timeoutMillis) {
        if (!StringUtils.hasText(receiverUid)) {
            log.debug("Skip APNS notification push because receiverUid is empty");
            return null;
        }

        List<ApnsTokenEntity> apnsTokens = apnsTokenRestService.findByUserUid(receiverUid);
        if (apnsTokens == null || apnsTokens.isEmpty()) {
            log.info("Skip APNS notification push because no token found, receiverUid={}", receiverUid);
            return null;
        }

        log.info("Prepare APNS notification push (wait result), receiverUid={}, tokenCount={}, ticketUid={}",
                receiverUid, apnsTokens.size(), ticketUid);

        PushApnsEntity lastRecord = null;
        for (ApnsTokenEntity apnsToken : apnsTokens) {
            PushApnsEntity record = buildNotificationPushRecord(apnsToken, title, body, ticketUid);
            record = apnsPushRepository.save(record);
            lastRecord = pushWithTokenInternal(apnsToken, title, body, record);
        }

        if (lastRecord == null || !StringUtils.hasText(lastRecord.getUid())) {
            return lastRecord;
        }
        return waitForPushResult(lastRecord.getUid(), timeoutMillis);
    }

    /**
     * 轮询等待推送记录进入终态（非 PENDING），超时或被中断时返回当前记录
     */
    private PushApnsEntity waitForPushResult(String recordUid, long timeoutMillis) {
        final long deadline = System.currentTimeMillis() + timeoutMillis;
        while (true) {
            PushApnsEntity record = apnsPushRepository.findByUid(recordUid).orElse(null);
            if (record == null) {
                return null;
            }
            if (!PushStatusEnum.PENDING.name().equals(record.getStatus())) {
                return record;
            }
            if (System.currentTimeMillis() >= deadline) {
                log.warn("Timed out waiting for APNS push result, recordUid={}, status={}", recordUid, record.getStatus());
                return record;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return record;
            }
        }
    }

    @Transactional
    public PushApnsEntity pushWithTokenInternal(ApnsTokenEntity apnsToken, String title, String content, PushApnsEntity record) {
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
        // 注意：必须接收 save 返回值。非事务调用链下 merge 会返回新的托管实例（版本号已+1），
        // 若丢弃返回值，后续异步回调闭包持有的仍是旧版本实例，最终写回会触发乐观锁冲突
        // （Row was already updated or deleted by another transaction）
        record = apnsPushRepository.save(record);

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
    public PushApnsEntity push(String deviceToken, String nickname, String content, int badgeNumber, ApnsP12Entity apnsP12,
            PushApnsEntity record) {
        if (!StringUtils.hasText(deviceToken)) {
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

    private PushApnsEntity buildPushRecord(ApnsTokenEntity apnsToken, String title, String content, MessageProtobuf message) {
        UserProtobuf sender = message != null ? message.getUser() : null;
        ThreadProtobuf thread = message != null ? message.getThread() : null;
        return PushApnsEntity.builder()
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
                .type(PushApnsTypeEnum.MESSAGE.name())
                .status(PushStatusEnum.PENDING.name())
                .channel(ChannelEnum.IOS.name())
                .build();
    }

    /**
     * Build a push record for business notifications (e.g., ticket status changes).
     * Does not require a MessageProtobuf.
     */
    private PushApnsEntity buildNotificationPushRecord(ApnsTokenEntity apnsToken, String title, String content, String ticketUid) {
        return PushApnsEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(apnsToken != null ? apnsToken.getOrgUid() : null)
                .userUid(apnsToken != null ? apnsToken.getUserUid() : null)
                .name(title)
                .receiver(apnsToken != null ? apnsToken.getUserUid() : null)
                .deviceToken(apnsToken != null ? apnsToken.getToken() : null)
                .p12Uid(apnsToken != null ? apnsToken.getP12Uid() : null)
                .messageUid(ticketUid)
                .content(content)
                .type(PushApnsTypeEnum.TICKET.name())
                .status(PushStatusEnum.PENDING.name())
                .channel(ChannelEnum.IOS.name())
                .build();
    }

    private PushApnsEntity markPushResult(PushApnsEntity record, boolean sendSuccess, String status, String sendMessage) {
        // 异步回调（Netty 线程）可能持有过期版本的实体实例，统一按 uid 重新加载最新行再写入结果，
        // 避免乐观锁冲突（Row was already updated or deleted by another transaction）；
        // 极端并发下仍撞锁时短暂重试
        PushApnsEntity target = record;
        if (StringUtils.hasText(record.getUid())) {
            target = apnsPushRepository.findByUid(record.getUid()).orElse(record);
        }
        target.setSendSuccess(sendSuccess);
        target.setStatus(status);
        target.setSendMessage(sendMessage);
        log.debug("Update APNS push result, recordUid={}, status={}, sendSuccess={}, message={}",
                target.getUid(),
                status,
                sendSuccess,
                sendMessage);
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                return apnsPushRepository.save(target);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock while updating APNS push result, recordUid={}, attempt={}",
                        target.getUid(), attempt + 1);
                PushApnsEntity latest = apnsPushRepository.findByUid(target.getUid()).orElse(null);
                if (latest == null) {
                    return null;
                }
                latest.setSendSuccess(sendSuccess);
                latest.setStatus(status);
                latest.setSendMessage(sendMessage);
                target = latest;
            }
        }
        log.error("Failed to update APNS push result after retries, recordUid={}, status={}", target.getUid(), status);
        return target;
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
