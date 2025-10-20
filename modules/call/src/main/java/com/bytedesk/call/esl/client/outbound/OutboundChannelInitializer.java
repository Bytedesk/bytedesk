package com.bytedesk.call.esl.client.outbound;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import com.bytedesk.call.esl.client.transport.message.EslFrameDecoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OutboundChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final IClientHandlerFactory clientHandlerFactory;
    private ExecutorService callbackExecutor = Executors.newSingleThreadExecutor();

    public OutboundChannelInitializer(IClientHandlerFactory clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public OutboundChannelInitializer setCallbackExecutor(ExecutorService callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
        return this;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // Add the text line codec combination first
        pipeline.addLast("encoder", new StringEncoder());
        // Note that outbound mode requires the decoder to treat many 'headers' as body lines
        pipeline.addLast("decoder", new EslFrameDecoder(8092, true));

        // now the outbound client logic
        pipeline.addLast("clientHandler",
                new OutboundClientHandler(
                        clientHandlerFactory.createClientHandler(),
                        callbackExecutor));
    }
}
