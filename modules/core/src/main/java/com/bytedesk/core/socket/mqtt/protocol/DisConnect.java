package com.bytedesk.core.socket.mqtt.protocol;

// import java.util.Iterator;
// import java.util.Set;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

import com.bytedesk.core.socket.mqtt.service.MqttSessionService;
import com.bytedesk.core.socket.mqtt.util.ChannelUtils;
import com.bytedesk.core.socket.service.MqService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 断开连接
 */
@Slf4j
@AllArgsConstructor
public class DisConnect {

    private final MqttSessionService mqttSessionStoreService;

    private final MqService mqService;

    /**
     * TODO: 清空redis中缓存的用户连接状态
     * 
     * @param channel
     * @param mqttMessage
     */
    public void processDisConnect(final Channel channel, final MqttMessage mqttMessage) {
        // log.debug("processDisConnect {}", mqttMessage.toString());
        //
        String clientId = ChannelUtils.getClientId(channel);
        log.info("processDisConnect {}", clientId);
        // final MqttSession sessionStore = mqttSessionStoreService.get(clientId);
        // if (sessionStore != null && sessionStore.isCleanSession()){
        // mqttSubscribeStoreService.removeForClient(clientId);
        // mqttDupPublishMessageStoreService.removeByClient(clientId);
        // mqttDupPubRelMessageStoreService.removeByClient(clientId);
        // }
        // 更新离线状态
        // updateDisconnectedStatus(clientId);
        // final Optional<User> userOptional = userService.findByUidCache(uid);
        // if (userOptional.isPresent()) {
        // eventPublisher.publishUserDisconnected(userOptional.get());
        // }
        mqttSessionStoreService.remove(clientId);
        // mqttClientIdStoreService.remove(clientId);
        // topicService.removeClientId(clientId);
        mqService.publishMqttDisconnectedEvent(clientId);

        channel.close();

        // FIXME:io.netty.channel.DefaultChannelPipeline : An exceptionCaught() event
        // was fired, and it reached at the tail of the pipeline. It usually means the
        // last handler in the pipeline did not handle the exception.
        // java.lang.NullPointerException: null
        // at
        // com.bytedesk.mqtt.protocol.DisConnect.processDisConnect(DisConnect.java:68)
        // ~[xiaper-spring-boot-mqtt-2.3.0-SNAPSHOT.jar!/:2.3.0-SNAPSHOT]
        // log.debug("DISCONNECT - clientId: {}", clientId);
        //

    }

    // 延迟执行，如果客服在此时间段之内重新连接，则不执行
    // private void updateDisconnectedStatus(String clientId) {
    // // 用户离线
    // // final String uid = clientId.split("/")[0];
    // // log.debug("DisConnect disconnected {}", uid);
    // // redisConnectService.setDelayDisconnect(uid);
    // //
    // // if (redisUserService.isAgent(uid)) {
    // // //
    // // Set<String> workGroupSet = redisUserService.getWorkGroups(uid);
    // // Iterator<String> iterator = workGroupSet.iterator();
    // // while (iterator.hasNext()) {
    // // String wid = iterator.next();
    // // // 将客服从缓存redis中删除
    // // redisRoutingRoundRobin.removeRoundRobinAgent(wid, uid);
    // // }
    // // // 清除客服在线缓存
    // // // TODO: 考虑同一个用户，登录多个客户端的情况
    // // redisConnectService.deleteConnectedAgent(uid);
    // // //
    // // User user = redisUserService.getAgentInfo(uid);
    // // if (user != null) {
    // // // 统计数据：减少在线客服数量
    // // redisStatisticService.removeOnlineAgent(user.getSubDomain(), uid);
    // // }
    // // // 清空空闲数量
    // // // redisService.removeAgentIdleCount(uid);
    // // } else {
    // // //
    // // // deleteConnectedVisitor(user);
    // // // TODO: 减少在线访客数量
    // // // redisStatisticService.removeOnlineVisitor();
    // // // 通知客服端访客离线
    // // messageService.notifyDisconnectedUid(uid);
    // // }
    // // //
    // // redisConnectService.disconnected(uid);
    // // // 设置连接状态为离线
    // //
    // userService.updateConnectionStatusByUid(StatusConsts.USER_STATUS_DISCONNECTED,
    // // uid);
    // // // 将当前用户长连接从所在服务器删除
    // // redisHostService.removeUserHost(uid);
    // }

}
