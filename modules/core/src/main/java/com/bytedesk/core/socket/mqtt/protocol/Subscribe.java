package com.bytedesk.core.socket.mqtt.protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

import com.bytedesk.core.event.BytedeskEventPublisher;
// import com.bytedesk.core.topic.TopicService;
// import com.bytedesk.core.redis.RedisUserService;
// import com.bytedesk.core.socket.mqtt.model.MqttRetainMessage;
// import com.bytedesk.core.socket.mqtt.model.MqttSubscribe;
// import com.bytedesk.core.socket.mqtt.service.MqttSubscribeService;
import com.bytedesk.core.socket.mqtt.util.ChannelUtils;
import com.bytedesk.core.socket.mqtt.util.MqttUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * SUBSCRIBE连接处理
 */
@Slf4j
@AllArgsConstructor
public class Subscribe {

    // private final MqttMessageIdService mqttMessageIdService;

    // private final MqttSubscribeService mqttSubscribeStoreService;

    // private final MqttRetainMessageStoreService mqttRetainMessageStoreService;

    // private final RedisUserService redisUserService;

    // private final TopicService topicService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    public void processSubscribe(Channel channel, MqttSubscribeMessage mqttSubscribeMessage) {
        // log.debug("processSubscribe {}", mqttSubscribeMessage.toString());
        //
        List<MqttTopicSubscription> topicSubscriptions = mqttSubscribeMessage.payload().topicSubscriptions();
        if (MqttUtil.validTopicFilter(topicSubscriptions)) {

            // TODO: 增加权限验证，某些用户不允许订阅某些topic，如：用户唯一uid主题topic，只有用户本人可以订阅
            // The new spec changes that and added a new error (0x80) in the MQTT SUBACK
            // message, so clients can react on forbidden subscriptions.
            //
            String clientId = ChannelUtils.getClientId(channel);
            log.info("subscribe clientId {}", clientId);
            // 用户clientId格式: uid/client
            // final String uid = clientId.split("/")[0];
            //
            List<Integer> mqttQoSList = new ArrayList<>();
            topicSubscriptions.forEach(topicSubscription -> {

                String topicFilter = topicSubscription.topicFilter(); // topicSubscription.topicName();
                MqttQoS mqttQoS = topicSubscription.qualityOfService();
                // MqttSubscribe subscribeStore = new MqttSubscribe(clientId, topicFilter,
                // mqttQoS.value());
                //
                // mqttSubscribeStoreService.put(topicFilter, subscribeStore);
                // topicService.subscribe(topicFilter, clientId);
                bytedeskEventPublisher.publishMqttSubscribeEvent(topicFilter, clientId);
                //
                mqttQoSList.add(mqttQoS.value());
                // 添加缓存
                // redisUserService.addTopic(uid, topicFilter);
                log.debug("SUBSCRIBE - clientId: {}, topFilter: {}, QoS: {}", clientId, topicFilter, mqttQoS.value());
            });

            // 回复 SUBACK 消息给当前客户端
            MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(mqttSubscribeMessage.variableHeader().messageId()),
                    new MqttSubAckPayload(mqttQoSList));
            channel.writeAndFlush(subAckMessage);

            // 发布当前topic的retain消息给当前客户端
            // topicSubscriptions.forEach(topicSubscription -> {
            // String topicFilter = topicSubscription.topicName();
            // MqttQoS mqttQoS = topicSubscription.qualityOfService();
            // this.sendRetainMessage(channel, topicFilter, mqttQoS);
            // });

        } else {
            channel.close();
        }
    }

    // 发布当前topic的retain消息给当前客户端
    // private void sendRetainMessage(Channel channel, String topicFilter, MqttQoS
    // mqttQoS) {
    // //
    // // List<MqttRetainMessage> retainMessageStores =
    // mqttRetainMessageStoreService.search(topicFilter);
    // // retainMessageStores.forEach(retainMessage -> {
    // // //
    // // MqttQoS respQoS = retainMessage.getMqttQoS() > mqttQoS.value() ? mqttQoS
    // // : MqttQoS.valueOf(retainMessage.getMqttQoS());
    // // //
    // // String clientId = ChannelUtils.getClientId(channel);

    // // if (respQoS == MqttQoS.AT_MOST_ONCE) {
    // // //
    // // MqttPublishMessage publishMessage = (MqttPublishMessage)
    // MqttMessageFactory.newMessage(
    // // new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
    // // new MqttPublishVariableHeader(retainMessage.getTopic(), 0),
    // // Unpooled.buffer().writeBytes(retainMessage.getMessageBytes()));
    // // //

    // // log.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}",
    // // clientId, retainMessage.getTopic(),
    // // respQoS.value());

    // // channel.writeAndFlush(publishMessage);

    // // } else if (respQoS == MqttQoS.AT_LEAST_ONCE || respQoS ==
    // MqttQoS.EXACTLY_ONCE) {
    // // //
    // // int messageId = mqttMessageIdService.getNextMessageId();
    // // MqttPublishMessage publishMessage = (MqttPublishMessage)
    // MqttMessageFactory.newMessage(
    // // new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
    // // new MqttPublishVariableHeader(retainMessage.getTopic(), messageId),
    // // Unpooled.buffer().writeBytes(retainMessage.getMessageBytes()));
    // // //
    // // log.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}",
    // clientId, retainMessage.getTopic(),
    // // respQoS.value(), messageId);

    // // channel.writeAndFlush(publishMessage);
    // // }
    // // });

    // }

}
