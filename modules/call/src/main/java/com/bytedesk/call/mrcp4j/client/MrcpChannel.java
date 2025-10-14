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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bytedesk.call.mrcp4j.MrcpMethodName;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpMessage;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.header.ChannelIdentifier;
import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeader;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequestFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides all primary functionality required for an MRCPv2 client to interact with an MRCPv2 resource.  Through an instance of this class
 * clients can construct and send MRCP requests, receive responses and be notified of events triggered by the MRCP resource.
 * <p>
 * To construct a {@code MrcpChannel} instance use {@link com.bytedesk.call.mrcp4j.client.MrcpProvider#createChannel(java.lang.String, java.net.InetAddress, int, java.lang.String)}.
 * </p>
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class MrcpChannel implements MrcpMessageHandler {
    
    private MrcpResponse _response = new MrcpResponse();
    private Object _responseLock = new Object();
    private List<MrcpEventListener> _listeners = Collections.synchronizedList(new ArrayList<MrcpEventListener>());

    private ChannelIdentifier _channelID;
    private MrcpSocket _socket;
    private long _requestID;

    MrcpChannel(String channelID, MrcpSocket socket) throws IllegalValueException {
        _channelID = (ChannelIdentifier) MrcpHeaderName.CHANNEL_IDENTIFIER.createHeaderValue(channelID);
        _socket = socket;
        _requestID = 1;//System.currentTimeMillis();
        socket.addMessageHandler(_channelID, this);
    }

    /**
     * Retrieves the channel ID associated with this channel.
     * @return the channel ID associated with this channel.
     */
    public ChannelIdentifier getChannelID() {
        return _channelID;
    }

    /*public String getType() {
        return null;
    }*/

    /**
     * Creates a request object associated with this channel.  The request object can then be passed
     * to {@link com.bytedesk.call.mrcp4j.client.MrcpChannel#sendRequest(com.bytedesk.call.mrcp4j.message.request.MrcpRequest)}
     * (after setting content or other parameters) in order to actually invoke the request on the MRCP
     * resource accessed by this channel.
     * 
     * @param  methodName   name of the method the desired request object should represent.
     * @return              request object representing the specified method call.
     */ 
    public MrcpRequest createRequest(MrcpMethodName methodName) {

        MrcpRequest request = MrcpRequestFactory.createRequest(methodName);

        // mrcp-version
        request.setVersion(MrcpMessage.MRCP_VERSION_2_0);

        // message-length not yet known

        // request-id
        // TODO: set this when message sent instead (to guarantee sequence)
        synchronized (this) {
            request.setRequestID(_requestID++);
        }

        MrcpHeader header = MrcpHeaderName.CHANNEL_IDENTIFIER.constructHeader(_channelID);
        request.addHeader(header);

        return request;
    }
    
    public MrcpRequest createVendorSpecificRequest(String methodName) {
    	
    	MrcpRequest request = MrcpRequestFactory.createVendorSpecificRequest(methodName);
    	
    	// mrcp-version
        request.setVersion(MrcpMessage.MRCP_VERSION_2_0);

        // message-length not yet known

        // request-id
        // TODO: set this when message sent instead (to guarantee sequence)
        synchronized (this) {
            request.setRequestID(_requestID++);
        }

        MrcpHeader header = MrcpHeaderName.CHANNEL_IDENTIFIER.constructHeader(_channelID);
        request.addHeader(header);
    	
    	return request;
    }

    /**
     * Invokes a request on the MRCP resource associated with this channel.
     * @param  request                 specification of the request to be invoked.
     * @return                         the response provided by the MRCP resource to the specified request.
     * @throws IOException             if an I/O error occurs.
     * @throws MrcpInvocationException           if the MRCP resource returned a response error code
     * @throws InterruptedException    if another thread interrupted the current thread while the current thread
     *                                 was waiting for a response from the MRCP resource.
     */
    public synchronized MrcpResponse sendRequest(MrcpRequest request)
      throws IOException, MrcpInvocationException, InterruptedException {

    	log.debug("Sending request: "+request.toString());
        MrcpResponse response = null;
        synchronized (_responseLock) {
            _response = null;
            _socket.sendRequest(request);
            while (_response == null) {
                try {
                    _responseLock.wait();
                } catch (InterruptedException e) {
                    //TODO: register response to be discarded if received later
                    throw e;
                }
            }
            response = _response;
        }
        log.debug("Got resp: "+response.toString());
        //TODO: validate that the response matches the request

        if (response.getStatusCode() > 299) {
            throw new MrcpInvocationException(response);
        }
        return response;
    }

    /**
     * Registers an event listener that will be notified of any MRCP events received on this channel.
     * @param listener instance to be notified of MRCP events received on this channel.
     */
    public void addEventListener(MrcpEventListener listener) {
        _listeners.add(listener);
    }

    /**
     * Unregisters an event listener that may have been registered to receive MRCP events from this channel.
     * @param listener instance to be removed from listeners to events received on this channel.
     */
    public void removeEventListener(MrcpEventListener listener) {
        _listeners.remove(listener);
    }

    /* (non-Javadoc)
     * @see com.bytedesk.call.mrcp4j.client.MrcpMessageHandler#handleMessage(com.bytedesk.call.mrcp4j.message.MrcpMessage)
     */
    public void handleMessage(MrcpMessage message) {
        if (message instanceof MrcpResponse) {
            log.debug("got a response in handler: "+message.toString());
            synchronized (_responseLock) {
            	log.debug("response lock set");
                if (_response == null) { // sendRequest is waiting for a response
                    _response = (MrcpResponse) message;
                    _responseLock.notifyAll();
                } else {
                    log.warn("Unexpected response received when no request was being sent!");
                    log.warn("Message: "+message.toString());
                }
            }
        } else {
        	log.info("Got an event: "+((MrcpEvent) message).toString());
            synchronized (_listeners) {
                for (MrcpEventListener listener : _listeners) {
                    listener.eventReceived((MrcpEvent) message);
                }
            }
        }
    }

}