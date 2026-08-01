package com.bytedesk.core.socket.mqtt.handler;

import java.io.IOException;
import java.net.SocketException;
import java.util.Locale;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class MqttPipelineExceptionHandler extends ChannelInboundHandlerAdapter {

    public static final MqttPipelineExceptionHandler INSTANCE = new MqttPipelineExceptionHandler();

    private MqttPipelineExceptionHandler() {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause == null) {
            log.warn("MqttPipelineExceptionHandler received null cause, closing channel");
            ctx.close();
            return;
        }

        if (isExpectedDisconnect(cause)) {
            log.debug("MqttPipelineExceptionHandler remote peer disconnected: {}", cause.toString());
        } else {
            log.warn("MqttPipelineExceptionHandler caught unhandled exception, closing channel", cause);
        }
        ctx.close();
    }

    private boolean isExpectedDisconnect(Throwable cause) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof SocketException || current instanceof IOException) {
                String message = current.getMessage();
                if (message == null) {
                    return true;
                }
                String normalized = message.toLowerCase(Locale.ROOT);
                if (normalized.contains("connection reset")
                        || normalized.contains("broken pipe")
                        || normalized.contains("forcibly closed")
                        || normalized.contains("timed out")
                        || normalized.contains("can't assign requested address")
                        || normalized.contains("cannot assign requested address")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }
}