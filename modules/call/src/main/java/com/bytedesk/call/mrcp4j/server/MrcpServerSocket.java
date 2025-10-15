/*
 * MRCP4J - Java API implementation of MRCPv2 specification
 *
 * Copyright (C) 2005-2006 SpeechForge - http://www.speechforge.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact: ngodfredsen@users.sourceforge.net
 *
 */
package com.bytedesk.call.mrcp4j.server;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import java.net.InetSocketAddress;

import com.bytedesk.call.mrcp4j.common.MrcpEventName;
import com.bytedesk.call.mrcp4j.common.MrcpRequestState;
import com.bytedesk.call.mrcp4j.common.MrcpResourceType;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;
import com.bytedesk.call.mrcp4j.server.delegator.RecogOnlyRequestDelegator;
import com.bytedesk.call.mrcp4j.server.delegator.RecorderRequestDelegator;
import com.bytedesk.call.mrcp4j.server.delegator.SpeakVerifyRequestDelegator;
import com.bytedesk.call.mrcp4j.server.delegator.SpeechSynthRequestDelegator;
import com.bytedesk.call.mrcp4j.server.delegator.VoiceEnrollmentRequestDelegator;
import com.bytedesk.call.mrcp4j.server.mina.IoTextLoggingFilter;
import com.bytedesk.call.mrcp4j.server.provider.RecogOnlyRequestHandler;
import com.bytedesk.call.mrcp4j.server.provider.RecorderRequestHandler;
import com.bytedesk.call.mrcp4j.server.provider.SpeakVerifyRequestHandler;
import com.bytedesk.call.mrcp4j.server.provider.SpeechSynthRequestHandler;
import com.bytedesk.call.mrcp4j.server.provider.VoiceEnrollmentRequestHandler;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class MrcpServerSocket {

    private static MrcpCodecFactory CODEC_FACTORY = new MrcpCodecFactory();

    private MrcpRequestProcessorImpl _requestProcessorImpl;
    private IoAcceptor _acceptor;
    private int _port;

    /**
     * Creates a MRCP server socket, bound to the specified port
     * 
     * @param port the port number to bind to
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public MrcpServerSocket(int port) throws IOException {
        _port = port;

        _requestProcessorImpl = new MrcpRequestProcessorImpl();

        // Create acceptor
        _acceptor = new NioSocketAcceptor();
        
        // Add logging filter
        _acceptor.getFilterChain().addLast("logger", new IoTextLoggingFilter());
        
        // Add codec filter
        _acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(CODEC_FACTORY));
        
        // Set handler
        _acceptor.setHandler(new MrcpProtocolHandler(_requestProcessorImpl));
        
        // Bind to port
        _acceptor.bind(new InetSocketAddress(port));

        if (log.isDebugEnabled()) {
            log.debug("MRCPv2 protocol provider listening on port " + port);
        }

    }

    /**
     * TODOC
     * @return Returns the port.
     */
    public int getPort() {
        return _port;
    }

    public void openChannel(String channelID, RecogOnlyRequestHandler requestHandler) {
        validateChannelID(channelID, RecogOnlyRequestHandler.RESOURCE_TYPES);
        openChannel(channelID, new RecogOnlyRequestDelegator(requestHandler));
    }

    public void openChannel(String channelID, VoiceEnrollmentRequestHandler requestHandler) {
        validateChannelID(channelID, VoiceEnrollmentRequestHandler.RESOURCE_TYPES);
        openChannel(channelID, new VoiceEnrollmentRequestDelegator(requestHandler));
    }

    public void openChannel(String channelID, SpeechSynthRequestHandler requestHandler) {
        validateChannelID(channelID, SpeechSynthRequestHandler.RESOURCE_TYPES);
        openChannel(channelID, new SpeechSynthRequestDelegator(requestHandler));
    }

    public void openChannel(String channelID, SpeakVerifyRequestHandler requestHandler) {
        validateChannelID(channelID, SpeakVerifyRequestHandler.RESOURCE_TYPES);
        openChannel(channelID, new SpeakVerifyRequestDelegator(requestHandler));
    }

    public void openChannel(String channelID, RecorderRequestHandler requestHandler) {
        validateChannelID(channelID, RecorderRequestHandler.RESOURCE_TYPES);
        openChannel(channelID, new RecorderRequestDelegator(requestHandler));
    }
    
    private static void validateChannelID(String channelID, MrcpResourceType[] expected) {
        MrcpResourceType actual = MrcpResourceType.fromChannelID(channelID);
        for (MrcpResourceType type : expected) {
            if (type.equals(actual)) {
                return;
            }
        }

        throw new IllegalArgumentException(
            "Incorrect channel resource type for specified request handler: " + channelID
        );
    }

    private void openChannel(String channelID, MrcpRequestHandler requestHandler) {
        _requestProcessorImpl.registerRequestHandler(channelID, requestHandler);
    }

    public void closeChannel(String channelID) {
        _requestProcessorImpl.unregisterRequestHandler(channelID);
    }

    public void dispose() {
        if (_acceptor != null) {
            _acceptor.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 32416;
        String channelID = "32AECB23433801@speechrecog";

        MrcpServerSocket serverSocket = new MrcpServerSocket(port);
        serverSocket.openChannel(channelID, new BogusRequestHandler());

        if (log.isDebugEnabled()) {
            log.debug("MRCP server socket listening on port " + port);
        }
    }

    private static class BogusRequestHandler implements MrcpRequestHandler {

        public MrcpResponse handleRequest(MrcpRequest request, MrcpSession session) {
            MrcpResponse response = session.createResponse(MrcpResponse.STATUS_SUCCESS, MrcpRequestState.IN_PROGRESS);
            new BogusEventThread(session).start();
            return response;
        }

    }

    private static class BogusEventThread extends Thread {

        private MrcpSession _session;

        BogusEventThread(MrcpSession session) {
            _session = session;
        }

        @Override
        public void run() {

            MrcpEvent event = _session.createEvent(MrcpEventName.RECOGNITION_COMPLETE, MrcpRequestState.COMPLETE);

            try {
                _session.postEvent(event);
            } catch (TimeoutException e){
                log.warn("TimeoutException", e);
            }
        }

    }

}