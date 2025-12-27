package com.bytedesk.core.push.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Pushy
 * 
 * @see <a href=
 *      "https://github.com/relayrides/pushy/wiki/Best-practices">pushy</a&gt;
 * @see <a href="https://github.com/jchambers/pushy">pushy2</a&gt;
 * @see <a href="https://juejin.im/entry/5b4d4eba5188257bcc165e07">juejin</a&gt;
 *
 * @author kefux.com on 2019/3/18
 */
@Slf4j
@Service
// Annotation-specified bean name 'pushApnsService' for bean class 
// [com.bytedesk.core.push.ios.PushApnsService] conflicts with existing, non-compatible bean definition of same name
public class PushApnsService {

    // private static final Semaphore semaphore = new Semaphore(10000);

    public void pushToBytedeskiOSRelease(String deviceToken, String nickname, String content) {
        // log.info("push token {}, nickname {}, content {}", deviceToken, nickname,
        // content);
        // TODO: 不通过配置文件，修改为通过mysql存储参数设置
        // 导出p12过程: https://blog.csdn.net/north1989/article/details/112795215
        // String bundleIdentifier = "com.kefux.app";
        // String p12Url = "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/apns/production/distribute.p12";
        // String p12Password = "E8U7Km3lhaUE1ZQq";
        // int unreadCount = 1;
        //
        // push(deviceToken, nickname, content, unreadCount, bundleIdentifier, StatusConsts.IOS_BUILD_RELEASE, p12Url,
        //         p12Password);
    }

    public void pushToBytedeskiOSDebug(String deviceToken, String nickname, String content) {
        // log.info("push token {}, nickname {}, content {}", deviceToken, nickname,
        // content);
        // TODO: 不通过配置文件，修改为通过mysql存储参数设置
        // 导出p12过程: https://blog.csdn.net/north1989/article/details/112795215
        // String bundleIdentifier = "com.kefux.app";
        // String p12Url = "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/apns/production/development.p12";
        // String p12Password = "E8U7Km3lhaUE1ZQq";
        // int unreadCount = 1;
        //
        // push(deviceToken, nickname, content, unreadCount, bundleIdentifier, StatusConsts.IOS_BUILD_DEBUG, p12Url,
        //         p12Password);
    }

    /**
     * 推送一条消息
     * payloadBuilder.setContentAvailable(false);
     *
     * @param deviceToken      token
     * @param nickname         title
     * @param content          content
     * @param bundleIdentifier bundle id
     * @param build            debug or release
     * @param p12Url           url
     * @param p12Password      password
     */
    public void push(String deviceToken, String nickname, String content, int badgeNumber, String bundleIdentifier,
            String build, String p12Url, String p12Password) {
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
                build, p12Url, p12Password).sendNotification(pushNotification);
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
            log.error("推送失败 Failed to send push notification.", e);
        }

    }

    private ApnsClient getApnsClient(String build, String p12Url, String p12Password) {

        // String apnsServer = build.equals(StatusConsts.IOS_BUILD_DEBUG) ? ApnsClientBuilder.DEVELOPMENT_APNS_HOST
        //         : ApnsClientBuilder.PRODUCTION_APNS_HOST;

        ApnsClient apnsClient = null;

        // try {

        //     EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        //     apnsClient = new ApnsClientBuilder().setApnsServer(apnsServer)
        //             .setClientCredentials(new URL(p12Url).openStream(), p12Password)
        //             .setConcurrentConnections(4)
        //             .setEventLoopGroup(eventLoopGroup)
        //             .build();

        // } catch (Exception e) {
        //     log.error("ios get pushy apns client failed!");
        // }

        return apnsClient;
    }


}
