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
package com.bytedesk.call.mrcp4j.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.bytedesk.call.mrcp4j.message.MrcpMessage;
import com.bytedesk.call.mrcp4j.message.header.ChannelIdentifier;
import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides an endpoint for communication between the MRCPv2 client and the MRCPv2 server (for internal library use only).
 *
 * <p>This class is intended for internal use by the MRCP4J implementation code.  Please see
 * {@link com.bytedesk.call.mrcp4j.client.MrcpProvider#createChannel(java.lang.String, java.net.InetAddress, int, java.lang.String)}
 * for constructing an {@link com.bytedesk.call.mrcp4j.client.MrcpChannel} that can be used to send control messages to the media
 * resource on the MRCP server.</p>
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class MrcpSocket {

    private MrcpRequestEncoder _requestEncoder = new MrcpRequestEncoder();
    Map<ChannelIdentifier , MrcpMessageHandler> _handlers = Collections.synchronizedMap(new HashMap<ChannelIdentifier , MrcpMessageHandler>());

    private Socket _socket;
    BufferedReader _in;
    private PrintWriter _out;

    MrcpSocket(InetAddress host, int port) throws IOException {
        _socket = new Socket(host, port);
        _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream())));
        new ReadThread().start();
    }

    public void sendRequest(MrcpRequest request) throws IOException {
        try {
            _requestEncoder.encode(request, _out);
            _out.flush();
        } catch (IOException e){
            // TODO: may need to reset socket here...
            log.debug("IOException", e);
            throw e;
        }
    }

    public void addMessageHandler(ChannelIdentifier channelID, MrcpMessageHandler handler) {
        _handlers.put(channelID, handler);
    }

    public void removeMessageHandler(ChannelIdentifier channelID) {
        _handlers.remove(channelID);
    }
    
    private class ReadThread extends Thread {

        private MrcpMessageDecoder _messageDecoder = new MrcpMessageDecoder();

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            boolean run = true;
            while (run) {
                // TODO: switch to nio so that the following statement doesn't block forever when the socket is closed
                try {
                    MrcpMessage message = _messageDecoder.decode(_in);
                    ChannelIdentifier channelID = message.getChannelIdentifier();
                    MrcpMessageHandler handler = _handlers.get(channelID);
                    if (handler != null) {
                        handler.handleMessage(message);
                    } else if (log.isDebugEnabled()) {
                        log.debug("No handler found for channel: " + channelID);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    run = false;
                    log.warn("IOException", e);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    log.warn("ParseException", e);
                } catch (IllegalValueException e) {
                    // TODO Auto-generated catch block
                    log.warn("IllegalValueException", e);
                }
            }
        }

    }

}