package com.bytedesk.core.socket.mqtt.protocol;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import org.springframework.util.StringUtils;

import com.bytedesk.core.socket.mqtt.MqttService;
import com.bytedesk.core.socket.mqtt.MqttAuthService;
import com.bytedesk.core.socket.mqtt.MqttConsts;
import com.bytedesk.core.socket.mqtt.MqttSession;
import com.bytedesk.core.socket.mqtt.MqttSessionService;
import com.bytedesk.core.socket.mqtt.handler.MqttIdleStateHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class Connect {

    private final MqttAuthService mqttAuthService;

    private final MqttSessionService mqttSessionService;

    private final MqttService mqttService;

    /**
     * 处理连接请求
     * @param channel
     * @param mqttConnectMessage
     */
    public void processConnect(final Channel channel, final MqttConnectMessage mqttConnectMessage) {
        // log.debug("processConnect {}", mqttConnectMessage.toString());

        // 消息解码器出现异常
        if (mqttConnectMessage.decoderResult().isFailure()) {
            final Throwable cause = mqttConnectMessage.decoderResult().cause();
            log.error("cause {}", cause.toString());
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                final MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory
                        .newMessage(new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                                new MqttConnAckVariableHeader(
                                        MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false),
                                null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的clientId
                final MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED,
                                false),
                        null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                return;
            }
            channel.close();
            return;
        }

        // 
        final String clientId = mqttConnectMessage.payload().clientIdentifier();
        final Boolean isCleanSession = mqttConnectMessage.variableHeader().isCleanSession();
        // clientId为空或null的情况, 这里要求客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
        if (!StringUtils.hasText(clientId)) {
            final MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false),
                    null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }

        // 用户名和密码验证, 这里要求客户端连接时必须提供用户名和密码, 不管是否设置用户名标志和密码标志为1, 此处没有参考标准协议实现
        final String username = mqttConnectMessage.payload().userName();
        final String password = mqttConnectMessage.payload().passwordInBytes() == null ? null
                : new String(mqttConnectMessage.payload().passwordInBytes(), CharsetUtil.UTF_8);
        if (!mqttAuthService.checkValid(username, password)) {
            final MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD,
                            false),
                    null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }

        // 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
        if (mqttSessionService.containsKey(clientId)) {
            // log.debug("clientId {} already exist", clientId);
            final MqttSession sessionStore = mqttSessionService.get(clientId);
            if (sessionStore != null) {
                // FIXME: java.lang.NullPointerException: null
                final Channel previous = sessionStore.getChannel();
                final Boolean cleanSession = sessionStore.getCleanSession();
                // 一个账号可以同时登录多个不同客户端，但同一个客户端同时仅能够登录一个，
                // 多余需要踢掉线（不同终端后不会互踢，但是两个相同终端（例如两个 iOS 端登录）会互踢。）
                if (cleanSession.booleanValue()) {
                    mqttSessionService.remove(clientId);
                    // mqttSubscribeStoreService.removeForClient(clientId);
                    // topicService.removeClientId(clientId);
                    // mqttDupPublishMessageStoreService.removeByClient(clientId);
                    // mqttDupPubRelMessageStoreService.removeByClient(clientId);
                    // bytedeskEventPublisher.publishMqttDisconnectedEvent(clientId);
                }
                // TODO: 修改消息类型 CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD
                // final MqttConnAckMessage connAckMessage = (MqttConnAckMessage)
                // MqttMessageFactory.newMessage(
                // new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE,
                // false, 0),
                // new
                // MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD,
                // false), null);
                // previous.writeAndFlush(connAckMessage);
                previous.close();
            }
        }

        // 异常断开：网络中断、程序崩溃等情况，Broker 会自动发布遗嘱消息
        final MqttSession mqttSession = new MqttSession(clientId, channel, isCleanSession, null);
        // 检查连接消息中是否设置了遗嘱标志
        if (mqttConnectMessage.variableHeader().isWillFlag()) {
            log.debug("send will message");
            // 创建遗嘱消息
            final MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false,
                            MqttQoS.valueOf(mqttConnectMessage.variableHeader().willQos()), // 遗嘱消息的QoS等级
                            mqttConnectMessage.variableHeader().isWillRetain(), 0), // 是否保留消息
                    new MqttPublishVariableHeader(mqttConnectMessage.payload().willTopic(), 0), // 遗嘱主题
                    Unpooled.buffer().writeBytes(mqttConnectMessage.payload().willMessageInBytes())); // 遗嘱内容
            // 将遗嘱消息保存到会话中
            mqttSession.setWillMessage(willMessage);
        }

        // 处理连接心跳包
        if (mqttConnectMessage.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            channel.pipeline().addFirst("idle", new MqttIdleStateHandler(0, 0,
                    Math.round(mqttConnectMessage.variableHeader().keepAliveTimeSeconds() * 1.5f)));
        }

        // 回复 CONNACK 消息给当前客户端
        final Boolean sessionPresent = mqttSessionService.containsKey(clientId) && !isCleanSession;
        final MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent), null);
        channel.writeAndFlush(okResp);

        // 将clientId存储到channel的map中
        channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).set(clientId);
        channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_USERNAME)).set(username);
        channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_PASSWORD)).set(password);

        // 至此存储会话消息及返回接受客户端连接
        mqttSessionService.put(clientId, mqttSession);
        // 存储clientId
        mqttService.publishMqttConnectedEvent(clientId);

        // 用户clientId格式: uid/client/deviceUid
        // final String uid = clientId.split("/")[0];
        // 判断是否已经缓存
        // subscribe(clientId, uid);
        // subscribe(clientId, uid + "/#");
        // // 更新在线状态
        // updateConnectedStatus(uid);
        // // redis查询离线消息，并推送
        // sendOfflineMessage(uid);
        // log.debug("CONNECTED - clientId: {}", clientId);
    }

    // private void subscribe(String clientId, String topic) {
    // // final String uid = clientId.split("/")[0];
    // // redisUserService.addTopic(uid, topic);
    // log.debug("clientId {}, topic {}", clientId, topic);
    // //
    // final MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
    // final MqttSubscribe subscribeStore = new MqttSubscribe(clientId, topic,
    // mqttQoS.value());
    // mqttSubscribeStoreService.put(topic, subscribeStore);
    // }

    // // 更新在线状态
    // private void updateConnectedStatus(String uid) {
    // log.debug("connected {}", uid);
    // }

    // 发送离线消息
    // private void sendOfflineMessage(String uid) {
    // // while (redisMessageCacheOfflineService.length(uid) > 0) {
    // // byte[] messageBytes = redisMessageCacheOfflineService.pop(uid);
    // // if (messageBytes != null) {
    // // // log.debug("send offline message to {}", uid);
    // // // 发送给mq
    // // // messageService.sendProtobufBytesToMqOffline(messageBytes);
    // // }
    // // }
    // }

}
