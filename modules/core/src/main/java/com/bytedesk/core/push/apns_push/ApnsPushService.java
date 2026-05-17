package com.bytedesk.core.push.apns_push;

import com.bytedesk.core.config.properties.BytedeskProperties;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.net.URI;

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
public class ApnsPushService {

    public void pushToBytedeskiOSRelease(String deviceToken, String nickname, String content) {
        // log.info("push token {}, nickname {}, content {}", deviceToken, nickname,
        // content);
        // 导出p12过程: https://blog.csdn.net/north1989/articlde/details/112795215
        String bundleIdentifier = "com.kefux.im";
        String p12Url = "";
        String p12Password = "";
        int unreadCount = 1;
        //
        push(deviceToken, nickname, content, unreadCount, bundleIdentifier, p12Url,
                p12Password);
    }

    public void pushToBytedeskiOSDebug(String deviceToken, String nickname, String content) {
        // log.info("push token {}, nickname {}, content {}", deviceToken, nickname,
        // content);
        // 导出p12过程: https://blog.csdn.net/north1989/article/details/112795215
        String bundleIdentifier = "com.kefux.im";
        String p12Url = "";
        String p12Password = "";
        int unreadCount = 1;
        //
        push(deviceToken, nickname, content, unreadCount, bundleIdentifier, p12Url,
                p12Password);
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
    public void push(String deviceToken, String nickname, String content, int badgeNumber, String bundleIdentifier,
            String p12Url, String p12Password) {
        //
        if (Strings.isNullOrEmpty(deviceToken)) {
            return;
        }
        ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
        payloadBuilder.setAlertBody(content);
        payloadBuilder.setAlertTitle(nickname);
        payloadBuilder.setBadgeNumber(badgeNumber);
        payloadBuilder.setSound("default");

        String payload = payloadBuilder.build();
        //
        final String token = TokenUtil.sanitizeTokenString(deviceToken);
        // topic: app's bundle identifier 需要务必填写正确
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, bundleIdentifier, payload);
        //
        final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture = getApnsClient(
                p12Url, p12Password).sendNotification(pushNotification);
        try {

            PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = sendNotificationFuture
                    .get();

            if (pushNotificationResponse.isAccepted()) {
                log.info("推送成功");
            } else {
                log.error("推送失败 Notification rejected by the APNs gateway: "
                        + pushNotificationResponse.getRejectionReason());
                pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                    log.error("\t…and the token is invalid as of " + timestamp);
                });
            }

        } catch (final Exception e) {
            log.error("推送失败 Failed to send push notification.");
            e.printStackTrace();
        }

    }

    private ApnsClient getApnsClient(String p12Url, String p12Password) {

        String apnsServer = BytedeskProperties.getInstance().getDebug() ? ApnsClientBuilder.DEVELOPMENT_APNS_HOST
                : ApnsClientBuilder.PRODUCTION_APNS_HOST;

        ApnsClient apnsClient = null;

        try {

            EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
            apnsClient = new ApnsClientBuilder().setApnsServer(apnsServer)
                    .setClientCredentials(URI.create(p12Url).toURL().openStream(), p12Password)
                    .setConcurrentConnections(4)
                    .setEventLoopGroup(eventLoopGroup)
                    .build();

        } catch (Exception e) {
            log.error("ios get pushy apns client failed!");
            e.printStackTrace();
        }

        return apnsClient;
    }


}
