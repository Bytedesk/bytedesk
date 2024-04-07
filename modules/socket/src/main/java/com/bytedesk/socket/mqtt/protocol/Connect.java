package com.bytedesk.socket.mqtt.protocol;

// import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.util.Iterator;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.bytedesk.core.redis.RedisUserService;
// import com.bytedesk.core.constant.StatusConsts;
// import com.bytedesk.core.constant.TypeConsts;
// import com.bytedesk.core.model.entity.Group;
// import com.bytedesk.core.model.entity.Thread;
// import com.bytedesk.core.model.entity.User;
// import com.bytedesk.core.model.entity.WorkGroup;
// import com.bytedesk.core.redis.RedisConnectService;
// import com.bytedesk.core.redis.RedisHostService;
// import com.bytedesk.core.redis.RedisRoutingRoundRobin;
// import com.bytedesk.core.redis.RedisStatisticService;
// import com.bytedesk.core.redis.RedisUserService;
// import com.bytedesk.core.service.GroupService;
// import com.bytedesk.core.service.MessageService;
// import com.bytedesk.core.service.ThreadService;
// import com.bytedesk.core.service.UserService;
// import com.bytedesk.core.util.JpaUtil;
import com.bytedesk.socket.mqtt.handler.MqttIdleStateHandler;
import com.bytedesk.socket.mqtt.model.MqttSession;
import com.bytedesk.socket.mqtt.model.MqttSubscribe;
import com.bytedesk.socket.mqtt.service.*;
import com.bytedesk.socket.mqtt.util.MqttConsts;
import com.bytedesk.socket.redis.RedisMessageCacheOfflineService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import java.util.Date;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

/**
 *
 * @author jackning
 */
@Slf4j
@AllArgsConstructor
public class Connect {

    // private final MqttMqService mqttMqService;

    private final MqttAuthService mqttAuthService;

    private final MqttSessionStoreService mqttSessionStoreService;

    private final MqttDupPublishMessageStoreService mqttDupPublishMessageStoreService;

    private final MqttDupPubRelMessageStoreService mqttDupPubRelMessageStoreService;

    private final MqttSubscribeStoreService mqttSubscribeStoreService;

    private final MqttClientIdStoreService mqttClientIdStoreService;

    // private final UserService userService;

    // private final GroupService groupService;

    // private final ThreadService threadService;

    private final RedisUserService redisUserService;

    // private final RedisRoutingRoundRobin redisRoutingRoundRobin;

    // private final RedisStatisticService redisStatisticService;

    // private final RedisHostService redisHostService;

    // private final RedisConnectService redisConnectService;

    // private final MessageService messageService;

    private final RedisMessageCacheOfflineService redisMessageCacheOfflineService;

    /**
     * TODO: 重构，每个功能独立成一个函数，精简此函数体
     * TODO: 将用户的连接状态、平台版本缓存到redis中
     * TODO: 用户上下线通知相应客户端
     * 
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

        final String clientId = mqttConnectMessage.payload().clientIdentifier();
        final boolean isCleanSession = mqttConnectMessage.variableHeader().isCleanSession();

        // clientId为空或null的情况, 这里要求客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
        if (!StringUtils.hasLength(clientId)) {
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
        if (mqttSessionStoreService.containsKey(clientId)) {
            // log.debug("clientId {} already exist", clientId);
            final MqttSession sessionStore = mqttSessionStoreService.get(clientId);
            if (sessionStore != null) {
                // FIXME: java.lang.NullPointerException: null
                final Channel previous = sessionStore.getChannel();
                final Boolean cleanSession = sessionStore.isCleanSession();
                // 一个账号可以同时登录多个不同客户端，但同一个客户端同时仅能够登录一个，
                // 多余需要踢掉线（不同终端后不会互踢，但是两个相同终端（例如两个 iOS 端登录）会互踢。）
                if (cleanSession.booleanValue()) {
                    mqttSessionStoreService.remove(clientId);
                    mqttSubscribeStoreService.removeForClient(clientId);
                    mqttDupPublishMessageStoreService.removeByClient(clientId);
                    mqttDupPubRelMessageStoreService.removeByClient(clientId);
                }
                // TODO: 修改消息类型 CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD
                // final MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                // new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                // new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
                // previous.writeAndFlush(connAckMessage);
                previous.close();
            }
        }

        // 处理遗嘱信息
        final MqttSession mqttSession = new MqttSession(clientId, channel, isCleanSession, null);
        if (mqttConnectMessage.variableHeader().isWillFlag()) {
            log.debug("send will message");
            final MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false,
                            MqttQoS.valueOf(mqttConnectMessage.variableHeader().willQos()),
                            mqttConnectMessage.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(mqttConnectMessage.payload().willTopic(), 0),
                    Unpooled.buffer().writeBytes(mqttConnectMessage.payload().willMessageInBytes()));
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

        // 至此存储会话消息及返回接受客户端连接
        mqttSessionStoreService.put(clientId, mqttSession);

        // 将clientId存储到channel的map中
        channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).set(clientId);
        channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_USERNAME)).set(username);
        channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_PASSWORD)).set(password);

        // 回复 CONNACK 消息给当前客户端
        final Boolean sessionPresent = mqttSessionStoreService.containsKey(clientId) && !isCleanSession;
        final MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent), null);
        channel.writeAndFlush(okResp);

        // 存储clientId
        mqttClientIdStoreService.put(clientId);

        // 用户clientId格式: uid/client
        final String uid = clientId.split("/")[0];
        // 判断是否已经缓存
        if (redisUserService.hasTopics(uid)) {
            // 是否被禁用
            if (redisUserService.isDisabled(uid)) {
                return;
            }
            // 读取缓存
            Set<String> topics = redisUserService.getTopics(uid);
            Iterator<String> iterator = topics.iterator();
            while (iterator.hasNext()) {
            String topic = iterator.next();
            subscribe(clientId, topic);
        }
        } else {
            // 读取数据库MySQL
            subscribeFromMysql(channel, clientId, uid);
        }
        // 更新在线状态
        updateConnectedStatus(uid);

        // redis查询离线消息，并推送
        sendOfflineMessage(uid);
        log.debug("CONNECTED - clientId: {}", clientId);
    }

    private void subscribeFromMysql(final Channel channel, final String clientId, final String uid) {

        // 用户验证登录成功之后，自动订阅uid主题topic
        subscribe(clientId, uid);

        // TODO: 从redis缓存中读取
        // final Optional<User> userOptional = userService.findByUid(uid);
        // if (userOptional.isPresent()) {

        //     if (userOptional.get().isEnabled()) {
        //         redisUserService.enable(uid);
        //     } else {
        //         redisUserService.disable(uid);
        //     }

        //     // 检查user.enabled是否为true，否则中断连接
        //     // 检测账号是否已经到期，如果已经到期，则断开连接
        //     // TODO: 从Redis读取
        //     if ((!userOptional.get().isEnabled()
        //         // || (userOptional.get().getValidateUntilDate() != null
        //                     // && userOptional.get().getValidateUntilDate().before(new Date()))
        //         )) {
        //         final MqttConnAckMessage connAckMessage = (MqttConnAckMessage)
        //         MqttMessageFactory.newMessage(
        //         new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE,
        //         false, 0),
        //         new
        //         MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED,
        //         false),
        //         null);
        //         channel.writeAndFlush(connAckMessage);
        //         channel.close();
        //     return;
        // }

        // 订阅subDomain主题
        // subscribe(clientId, userOptional.get().getSubDomain());

        // TODO: 合并以下多次数据库查询为一次查询

        // 客服：订阅工作组通知消息
        // final Set<WorkGroup> workGroups = userOptional.get().getWorkGroups();
        // final Iterator<WorkGroup> iteratorWorkGroups = workGroups.iterator();
        // while (iteratorWorkGroups.hasNext()) {
        //     final WorkGroup workGroup = iteratorWorkGroups.next();
        //     subscribe(clientId, workGroup.getWid());
        //     //
        //     redisUserService.addWorkGroup(uid, workGroup.getWid());
        // }

        // TODO: 每当用户被分配会话，或者加入群组之后，系统发送通知，此mqtt模块监听通知，帮助用户添加订阅
        // final List<Group> groupList = groupService.findByDismissedAndMembersContains(false, userOptional.get());
        // final Iterator<Group> iterator = groupList.iterator();
        // while (iterator.hasNext()) {
        //     final Group group = iterator.next();
        //     subscribe(clientId, group.getGid());
        // }

        // 客服：重新订阅未结束客服会话
        // List<Thread> threadList = threadService.findByAgentAndClosed(userOptional.get(), false);
        // final Iterator<Thread> threadIterator = threadList.iterator();
        // while (threadIterator.hasNext()) {
        //     final Thread thread = threadIterator.next();
        //     // 仅针对客服会话
        //     if (!thread.getType().equals(TypeConsts.THREAD_TYPE_CONTACT) && !thread.getType().equals(TypeConsts.THREAD_TYPE_GROUP)) {
        //     subscribe(clientId, thread.getTopic());
        //     }
        // }

        // 访客：订阅未结束会话
        // List<Thread> visitorThreadList = threadService.findByVisitorAndClosed(userOptional.get(), false);
        // final Iterator<Thread> visitorThreadIterator = visitorThreadList.iterator();
        // while (visitorThreadIterator.hasNext()) {
        // final Thread thread = visitorThreadIterator.next();
        // subscribe(clientId, thread.getTopic());
        // }

        // 添加到客服set集合， 在isAgent函数里面缓存
        // if (userService.isAgent(userOptional.get())) {
        // // redisUserService.addAgent(uid);
        // }

        // 缓存必要用户信息
        // redisUserService.setAgentInfo(uid, userOptional.get());

        // TODO: 订阅Channel
        // 将在线用户记录入redis，在线计数+1，并利用定时任务记录每分钟在线数
        // eventPublisher.publishUserConnected(userOptional.get());
        // sendDupMessage(isCleanSession);
        // }
    }

    private void subscribe(String clientId, String topic) {
        final String uid = clientId.split("/")[0];
        redisUserService.addTopic(uid, topic);
        log.debug("clientid {}, topic {}", clientId, topic);
        //
        final MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        final MqttSubscribe subscribeStore = new MqttSubscribe(clientId, topic, mqttQoS.value());
        mqttSubscribeStoreService.put(topic, subscribeStore);
    }

    // 更新在线状态
    private void updateConnectedStatus(String uid) {
        log.debug("connected {}", uid);
        // redisConnectService.cancelDelayDisconnect(uid);
        // //
        // if (redisUserService.isAgent(uid)) {
        // //
        // if (!redisUserService.isNoAcceptStatus(uid)) {
        // // 只有客服处于接待状态，才会更新下列数据
        // Set<String> workGroupSet = redisUserService.getWorkGroups(uid);
        // Iterator<String> iterator = workGroupSet.iterator();
        // while (iterator.hasNext()) {
        // String wid = iterator.next();
        // boolean isExist = redisRoutingRoundRobin.isAgentExist(wid, uid);
        // if (!isExist) {
        // // 将客服添加到队列
        // redisRoutingRoundRobin.addAgent(wid, uid);
        // }
        // }
        // // 缓存客服在线缓存
        // // TODO: 考虑同一个用户，登录多个客户端的情况
        // redisConnectService.addConnectedAgent(uid);
        // // 读取缓存
        // User user = redisUserService.getAgentInfo(uid);
        // if (user != null) {
        // // 统计数据：增加在线客服数量
        // redisStatisticService.addOnlineAgent(user.getSubDomain(), uid);
        // // 初始化客服可接待数量，FIXME: 数量有待减掉当前正在进行中会话
        // // redisService.initAgentIdleCount(uid, user.getMaxThreadCount());
        // }
        // }
        // // 更新管理员所有客服列表信息
        // redisUserService.updateAgentConnected(uid);
        // // 设置连接状态为在线
        // userService.updateConnectionStatusByUid(StatusConsts.USER_STATUS_CONNECTED,
        // uid);
        // } else {
        // // 暂时用不到，没必要打开
        // // addConnectedVisitor(user);
        // // 通知客服端访客上线
        // messageService.notifyConnectedUid(uid);
        // }
        // //
        // redisConnectService.addConnected(uid);
        // // 存储当前用户长连接所在服务器
        // redisHostService.setUserHost(uid, JpaUtil.getServerIp());
    }

    // 发送离线消息
    private void sendOfflineMessage(String uid) {
        while (redisMessageCacheOfflineService.length(uid) > 0) {
            byte[] messageBytes = redisMessageCacheOfflineService.pop(uid);
            if (messageBytes != null) {
                // log.debug("send offline message to {}", uid);
                // 发送给mq
                // messageService.sendProtobufBytesToMqOffline(messageBytes);
            }
        }
    }

    // private void sendDupMessage(boolean isCleanSession) {
        // 如果cleanSession为0, 需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
        // if (!isCleanSession) {
            // log.debug("send dup message");
            // final List<MqttDupPublishMessage> dupPublishMessageStoreList = mqttDupPublishMessageStoreService.get(clientId);
            // final List<MqttDupPubRelMessage> dupPubRelMessageStoreList = mqttDupPubRelMessageStoreService.get(clientId);
            // dupPublishMessageStoreList.forEach(dupPublishMessage -> {
                // final MqttPublishMessage publishMessage = (MqttPublishMessage)
                // MqttMessageFactory.newMessage(
                // new MqttFixedHeader(MqttMessageType.PUBLISH, true,
                // MqttQoS.valueOf(dupPublishMessage.getMqttQoS()), false, 0),
                // new MqttPublishVariableHeader(dupPublishMessage.getTopic(),
                // dupPublishMessage.getMessageId()),
                // Unpooled.buffer().writeBytes(dupPublishMessage.getMessageBytes()));
                // channel.writeAndFlush(publishMessage);
                // });
                // dupPubRelMessageStoreList.forEach(dupPubRelMessage -> {
                // final MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                // new MqttFixedHeader(MqttMessageType.PUBREL,true,
                // MqttQoS.AT_MOST_ONCE,false,0),
                // MqttMessageIdVariableHeader.from(dupPubRelMessage.getMessageId()),
                // null
                // );
                // channel.writeAndFlush(pubRelMessage);
            // });
        // }
    // }

}
