package com.bytedesk.core.socket.mqtt.initializer.websocket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.ReferenceCountUtil;

class WebSocketCloseHandshakeTest {

    @Test
    void serverInitiatedCloseCanBeAcknowledgedByPeer() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new HttpServerCodec(),
                new HttpObjectAggregator(65536),
                new WebSocketServerProtocolHandler("/ws"));
        try {
            HttpRequest handshake = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/ws");
            handshake.headers()
                    .set(HttpHeaderNames.HOST, "localhost")
                    .set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
                    .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
                    .set(HttpHeaderNames.SEC_WEBSOCKET_KEY, "dGhlIHNhbXBsZSBub25jZQ==")
                    .set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, "13");
            channel.writeInbound(handshake);
            ReferenceCountUtil.release(channel.readOutbound());

            channel.writeOutbound(new CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE));

            assertDoesNotThrow(() ->
                    channel.writeInbound(new CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE)));
        } finally {
            channel.finishAndReleaseAll();
        }
    }
}
