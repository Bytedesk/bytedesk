package com.bytedesk.core.socket.mqtt.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import com.bytedesk.core.socket.mqtt.MqttConsts;
import com.bytedesk.core.socket.mqtt.MqttSession;
import com.bytedesk.core.socket.mqtt.protocol.ProtocolProcess;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
@ChannelHandler.Sharable
public class MqttTransportHandler extends SimpleChannelInboundHandler<MqttMessage> {

    // FIXME: 直接使用Autowired初始化，会报错null
    private ProtocolProcess protocolProcess;

    public MqttTransportHandler(ProtocolProcess protocolProcess) {
        this.protocolProcess = protocolProcess;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        // TODO: 可用于禁止某些ip连接服务器
        // log.debug("客户端ip:port {}", channelHandlerContext.channel().remoteAddress());
        // log.debug("channelRead0 {}", mqttMessage.toString());
        //
        if (mqttMessage.decoderResult().isFailure()) {
            //
            Throwable cause = mqttMessage.decoderResult().cause();
            // FIXME: cause java.lang.IllegalArgumentException: unknown message type: 0
            // FIXME: cause io.netty.handler.codec.mqtt.MqttIdentifierRejectedException:
            log.error("cause {}", cause.toString());
            //
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // log.error("MqttUnacceptableProtocolVersionException");
                channelHandlerContext.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(
                                MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false),
                        null));
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // log.error("MqttIdentifierRejectedException");
                channelHandlerContext.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED,
                                false),
                        null));
            } else {
                // FIXME: cause java.lang.IllegalArgumentException: invalid QoS: 3
                // FIXME: cause io.netty.handler.codec.DecoderException: non-zero remaining
                // payload bytes: 71 (PUBACK)
                // FIXME: cause java.lang.IllegalArgumentException: unknown message type: 0
                log.error("mqtt other error");
            }
            channelHandlerContext.close();
            return;
        }

        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                protocolProcess.connect().processConnect(channelHandlerContext.channel(),
                        (MqttConnectMessage) mqttMessage);
                break;
            case CONNACK:
                break;
            case PUBLISH:
                protocolProcess.publish().processPublish(channelHandlerContext.channel(),
                        (MqttPublishMessage) mqttMessage);
                break;
            case PUBACK:
                protocolProcess.pubAck().processPubAck(channelHandlerContext.channel(),
                        (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBREC:
                protocolProcess.pubRec().processPubRec(channelHandlerContext.channel(),
                        (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBREL:
                protocolProcess.pubRel().processPubRel(channelHandlerContext.channel(),
                        (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBCOMP:
                protocolProcess.pubComp().processPubComp(channelHandlerContext.channel(),
                        (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case SUBSCRIBE:
                protocolProcess.subscribe().processSubscribe(channelHandlerContext.channel(),
                        (MqttSubscribeMessage) mqttMessage);
                break;
            case SUBACK:
                // TODO: 回执
                break;
            case UNSUBSCRIBE:
                protocolProcess.unSubscribe().processUnSubscribe(channelHandlerContext.channel(),
                        (MqttUnsubscribeMessage) mqttMessage);
                break;
            case UNSUBACK:
                // TODO: 回执
                break;
            case PINGREQ:
                protocolProcess.pingReq().processPingReq(channelHandlerContext.channel(), mqttMessage);
                break;
            case PINGRESP:
                // TODO: 回执
                break;
            case DISCONNECT:
                protocolProcess.disConnect().processDisConnect(channelHandlerContext.channel(), mqttMessage);
                break;
            default:
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // String clientId = (String)ctx.channel().attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
        // FIXME: clientId is null ?
        // log.debug("channelActive " + clientId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        String clientId = (String) ctx.channel().attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
        if (clientId == null) {
            return;
        }
        // FIXME: 直接杀掉客户端进程，会执行此处： channelInactive 7673A1EB-3E85-4941-9863-5C499DA3EC0B,
        // 此处执行will message
        // log.debug("channelInactive " + clientId);
        // final MqttSession sessionStore =
        // protocolProcess.getMqttSessionStoreService().get(clientId);
        // if (sessionStore != null && sessionStore.isCleanSession()) {
        // protocolProcess.getMqttSubscribeStoreService().removeForClient(clientId);
        // protocolProcess.getMqttDupPublishMessageStoreService().removeByClient(clientId);
        // protocolProcess.getMqttDupPubRelMessageStoreService().removeByClient(clientId);
        // }
        // 更新离线状态
        // updateDisconnectedStatus(clientId);
        //
        // final Optional<User> userOptional =
        // protocolProcess.getUserService().findByUidCache(uid);
        // if (userOptional.isPresent()) {
        // protocolProcess.getEventPublisher().publishUserDisconnected(userOptional.get());
        // }
        //
        // log.info("channelInactive {}", clientId);
        // FIXME: 客户端异常断开的情况，需要清理数据。目前会造成数据不一致，待完善
        // protocolProcess.getMqttSessionStoreService().remove(clientId);
        // protocolProcess.getMqttClientIdStoreService().remove(clientId);
        // protocolProcess.getTopicService().removeClientId(clientId);
        // protocolProcess.getBytedeskEventPublisher().publishMqttDisconnectedEvent(clientId);
        //
        // 非客户端调用接口断开
        // log.debug("DISCONNECT - channelInactive - clientId: {}", clientId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        // FIXME: 异常断开，发送will topic消息 java.io.IOException: Connection reset by peer
        // FIXME: 异常断开，发送will topic消息 java.lang.ArrayIndexOutOfBoundsException
        log.error("TODO: 异常断开，发送will topic消息 {}", cause.toString());
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            log.error("cause instanceof IOException");
            channelHandlerContext.close();
        } else {
            // FIXME: DefaultChannelPipeline : An exceptionCaught() event was fired, and it
            // reached
            // at the tail of the pipeline. It usually means the last handler in the
            // pipeline did not handle the exception.
            log.error("cause other");
            super.exceptionCaught(channelHandlerContext, cause);
        }
    }

    /**
     * user event
     * 当 ChannelnboundHandler.fireUserEventTriggered()方法被调用时被调用，因为一个 POJO 被传经了
     * ChannelPipeline
     * TODO: 搞清楚调用时机？
     * 通过MqttIdleStateHandler触发userEventTriggered
     *
     * @param ctx   context
     * @param event event
     * @throws Exception exp
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        //
        if (event instanceof IdleStateEvent) {
            // log.debug("userEventTriggered idleStateEvent");
            //
            IdleStateEvent idleStateEvent = (IdleStateEvent) event;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                //
                Channel channel = ctx.channel();
                String clientId = (String) channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
                // 发送遗嘱消息
                if (this.protocolProcess.getMqttSessionStoreService().containsKey(clientId)) {
                    MqttSession sessionStore = this.protocolProcess.getMqttSessionStoreService().get(clientId);
                    if (sessionStore.getWillMessage() != null) {
                        log.debug("TODO: 发送遗嘱消息");
                        // TODO: 兼容
                        this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
                    }
                }
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, event);
            // log.debug("userEventTriggered {}", event.getClass());
        }
    }

    // TODO: 延迟执行，如果客服在此时间段之内重新连接，则不执行
    public void updateDisconnectedStatus(String clientId) {
        // 用户离线
        // final String uid = clientId.split("/")[0];
        // log.error("MqttTransportHandler disconnected clientId {}", clientId);
        // protocolProcess.getRedisConnectService().setDelayDisconnect(uid);
        //
        // if (protocolProcess.getRedisUserService().isAgent(uid)) {
        // //
        // Set<String> workGroupSet =
        // protocolProcess.getRedisUserService().getWorkGroups(uid);
        // Iterator<String> iterator = workGroupSet.iterator();
        // while (iterator.hasNext()) {
        // String wid = iterator.next();
        // // 将客服从缓存redis中删除
        // protocolProcess.getRedisRoutingRoundRobin().removeRoundRobinAgent(wid, uid);
        // }
        // // 清除客服在线缓存
        // // TODO: 考虑同一个用户，登录多个客户端的情况
        // protocolProcess.getRedisConnectService().deleteConnectedAgent(uid);
        // //
        // User user = protocolProcess.getRedisUserService().getAgentInfo(uid);
        // if (user != null) {
        // // 统计数据：减少在线客服数量
        // protocolProcess.getRedisStatisticService().removeOnlineAgent(user.getSubDomain(),
        // uid);
        // }
        // // 清空空闲数量
        // // protocolProcess.getRedisService().removeAgentIdleCount(uid);
        // } else {
        // // deleteConnectedVisitor(user);
        // // TODO: 减少在线访客数量
        // // redisStatisticService.removeOnlineVisitor();
        // // 通知客服端访客离线
        // protocolProcess.getMessageService().notifyDisconnectedUid(uid);
        // }
        // //
        // protocolProcess.getRedisConnectService().disconnected(uid);
        // // 设置连接状态为离线
        // protocolProcess.getUserService().updateConnectionStatusByUid(StatusConsts.USER_STATUS_DISCONNECTED,
        // uid);
        // // 将当前用户长连接从所在服务器删除
        // protocolProcess.getRedisHostService().removeUserHost(uid);
    }

}
