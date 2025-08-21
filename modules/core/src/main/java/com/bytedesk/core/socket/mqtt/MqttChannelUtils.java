package com.bytedesk.core.socket.mqtt;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;
import org.springframework.lang.NonNull;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;

/**
 * Various utilities for working with channels
 *
 * @author Dominik Obermaier
 * @author Christoph Schäbel
 */
public class MqttChannelUtils {

    private MqttChannelUtils() {
        // This is a utility class, don't instantiate it!
    }

    /**
     * Fetches the clientId from the channel attributes of the passed channel
     */
    public static String getClientId(final @NonNull Channel channel) {
        return (String) channel.attr(AttributeKey.valueOf(MqttConsts.MQTT_CLIENT_ID)).get();
    }

    public static void sendPubAckMessage(Channel channel, int messageId) {
        if (messageId <= 0 || messageId > 65535) {
            // 根据MQTT规范，消息ID必须在1-65535之间
            // log.warn("Invalid message ID for PUBACK: {}, ignoring", messageId);
            return;
        }
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null);
        channel.writeAndFlush(pubAckMessage);
    }

    public static void sendPubRecMessage(Channel channel, int messageId) {
        if (messageId <= 0 || messageId > 65535) {
            // 根据MQTT规范，消息ID必须在1-65535之间
            // log.warn("Invalid message ID for PUBREC: {}, ignoring", messageId);
            return;
        }
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null);
        channel.writeAndFlush(pubRecMessage);
    }

    public static Optional<String> getChannelIP(final Channel channel) {
        final Optional<InetAddress> inetAddress = getChannelAddress(channel);
        if (inetAddress.isPresent()) {
            return Optional.ofNullable(inetAddress.get().getHostAddress());
        }
        return Optional.empty();
    }
    
    public static Optional<InetAddress> getChannelAddress(final Channel channel) {
    
        final Optional<SocketAddress> socketAddress = Optional.ofNullable(channel.remoteAddress());
        if (socketAddress.isPresent()) {
            final SocketAddress sockAddress = socketAddress.get();
            //If this is not an InetAddress, we're treating this as if there's no address
            if (sockAddress instanceof InetSocketAddress) {
            return Optional.ofNullable(((InetSocketAddress) sockAddress).getAddress());
            }
        }
        return Optional.empty();
    }
    
    
    // public static ClientToken tokenFromChannel(@NonNull final Channel channel, @NonNull final Long disconnectTimestamp) {
    //     checkNotNull(disconnectTimestamp, "disconnectTimestamp must not be null");
    //     return getClientToken(channel, disconnectTimestamp);
    // }
    
    // public static ClientToken tokenFromChannel(@NonNull final Channel channel) {
        // return getClientToken(channel, null);
    // }
    
    // public static boolean messagesInFlight(@NonNull final Channel channel) {
    //     final boolean inFlightMessagesSent = channel.attr(ChannelAttributes.IN_FLIGHT_MESSAGES_SENT).get() != null;
    //     if (!inFlightMessagesSent) {
    //         return true;
    //     }
    //     final AtomicInteger inFlightMessages = channel.attr(ChannelAttributes.IN_FLIGHT_MESSAGES).get();
    //     if (inFlightMessages == null) {
    //         return false;
    //     }
    //     return inFlightMessages.get() > 0;
    // }
    
    // private static ClientToken getClientToken(@NonNull final Channel channel, @Nullable final Long disconnectTimestamp) {
    //     checkNotNull(channel, "channel must not be null");
    //     final String clientId = getClientId(channel);
        
    //     //These things can all be null!
    //     final String username = channel.attr(ChannelAttributes.AUTH_USERNAME).get();
    //     final byte[] password = channel.attr(ChannelAttributes.AUTH_PASSWORD).get();
    //     final SslClientCertificate sslCert = channel.attr(ChannelAttributes.AUTH_CERTIFICATE).get();
        
    //     final Listener listener = channel.attr(ChannelAttributes.LISTENER).get();
    //     final Optional<Long> disconnectTimestampOptional = Optional.fromNullable(disconnectTimestamp);
        
    //     final ClientToken clientToken = new ClientToken(clientId,
    //             username,
    //             password,
    //             sslCert,
    //             false,
    //             getChannelAddress(channel).orNull(),
    //             listener,
    //             disconnectTimestampOptional
    //     );
        
    //     final Boolean authenticated = channel.attr(ChannelAttributes.AUTHENTICATED_OR_AUTHENTICATION_BYPASSED).get();
    //     clientToken.setAuthenticated(authenticated != null ? authenticated : false);
        
    //     return clientToken;
    // }

}
