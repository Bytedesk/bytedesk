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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.bytedesk.call.mrcp4j.common.MrcpEventName;
import com.bytedesk.call.mrcp4j.common.MrcpRequestState;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.header.ChannelIdentifier;
import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;
import com.bytedesk.call.mrcp4j.util.ObjectWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class MrcpRequestProcessorImpl implements MrcpRequestProcessor {

    private Map<String, MrcpRequestHandler> _requestHandlers =
        Collections.synchronizedMap(new HashMap<String, MrcpRequestHandler>());

    private Map<String, MrcpSessionImpl> _sessions =
        Collections.synchronizedMap(new HashMap<String, MrcpSessionImpl>());


    public MrcpResponse processRequest(MrcpRequest request) {
        log.debug("MrcpRequestProcessorImpl.processRequest()...");

        MrcpResponse response = null;

        MrcpSessionImpl session = new MrcpSessionImpl(request);

        try {
            ChannelIdentifier channelIdentifier = request.getChannelIdentifier();
            if (channelIdentifier == null) { // channel-identifier header missing
                response = session.createResponse(MrcpResponse.STATUS_MANDATORY_HEADER_MISSING, MrcpRequestState.COMPLETE);
            } else {
                MrcpRequestHandler requestHandler = _requestHandlers.get(channelIdentifier.toString());
                if (requestHandler == null) { // no available request handler
                    response = session.createResponse(
                            MrcpResponse.STATUS_RESOURCE_NOT_ALLOCATED, MrcpRequestState.COMPLETE);
                } else {
                    synchronized (session) {
                        Exception cause = null;
                        try {
                            response = requestHandler.handleRequest(request, session);
                        } catch (Exception e) {
                            cause = e;
                        }
                        if (response == null) {
                            if (cause == null) {
                                log.warn("processRequest(): got NULL response from request handler!");
                            } else {
                                log.warn("processRequest(): got exception from request handler: ", cause);
                            }
                            response = session.createResponse(
                                    MrcpResponse.STATUS_SERVER_INTERNAL_ERROR, MrcpRequestState.COMPLETE);
                        } else {
                            log.debug("MrcpRequestProcessorImpl got response from request handler.");
                            session._ready = true;
                            if (response.getRequestState().equals(MrcpRequestState.COMPLETE)) {
                                session._complete = true;
                            } else {
                                _sessions.put(getRequestKey(request), session);
                            }
                        }
                    }
                }
            }
        } catch (IllegalValueException e) { // invalid channel-identifier header value
            response = session.createResponse(MrcpResponse.STATUS_ILLEGAL_VALUE_FOR_HEADER, MrcpRequestState.COMPLETE);
            response.addHeader(request.getHeader(MrcpHeaderName.CHANNEL_IDENTIFIER));
        }


        return response;
    }

    public MrcpRequestHandler registerRequestHandler(String channelID, MrcpRequestHandler requestHandler) {
        return _requestHandlers.put(channelID, requestHandler);
    }

    public MrcpRequestHandler unregisterRequestHandler(String channelID) {
        return _requestHandlers.remove(channelID);
    }

    // TODOC: Once this method has returned a null event or an event with MrcpRequestState.COMPLETE
    //        the method can no longer be called for the same request or an IllegalStateException will be thrown.
    public MrcpEvent getNextEvent(MrcpRequest request) {

        //lookup event acceptor for this request
        String requestKey = getRequestKey(request);
        MrcpSessionImpl session = _sessions.get(requestKey);
        if (session == null) {
            throw new IllegalStateException("The specified request has already been completed.");
        }

        // retrieve event
        log.debug("getNextEvent(): waiting to take event from queue...");
        MrcpEvent event = session.takeEvent();
        log.debug("getNextEvent(): got event from queue...");

        // remove event acceptor if request is completed by this event
        if (event == null || event.getRequestState().equals(MrcpRequestState.COMPLETE)) {
            log.debug("getNextEvent(): request is complete.");
            _sessions.remove(requestKey);
        }

        return event;
    }

    private static String getRequestKey(MrcpRequest request) {
        StringBuilder sb = new StringBuilder(request.getHeader(MrcpHeaderName.CHANNEL_IDENTIFIER).getValueString());
        sb.append(':').append(request.getRequestID());
        return sb.toString();
    }

    private static class MrcpSessionImpl implements MrcpSession {

        private BlockingQueue<ObjectWrapper<MrcpEvent>> _eventQueue = new SynchronousQueue<ObjectWrapper<MrcpEvent>>();
        private Object _lock = new Object();
        boolean _ready = false;
        boolean _complete = false;

        private MrcpRequest _request;

        private MrcpSessionImpl(MrcpRequest request) {
            _request = request;
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.server.MrcpSession#createResponse(short, com.bytedesk.call.mrcp4j.MrcpRequestState)
         */
        public MrcpResponse createResponse(short statusCode, MrcpRequestState requestState) {
            // TODO: validate args
            MrcpResponse response = new MrcpResponse();
            response.setVersion(_request.getVersion());
            response.setMessageLength(-1);
            response.setRequestID(_request.getRequestID());
            response.setStatusCode(statusCode);
            response.setRequestState(requestState);
            response.addHeader(_request.getHeader(MrcpHeaderName.CHANNEL_IDENTIFIER));
            return response;
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.server.MrcpSession#createEvent(com.bytedesk.call.mrcp4j.MrcpEventName, com.bytedesk.call.mrcp4j.MrcpRequestState)
         */
        public MrcpEvent createEvent(MrcpEventName eventName, MrcpRequestState requestState) {
            if (eventName == null) {
                throw new NullPointerException("Event name argument was null!");
            }
            if (requestState == null) {
                throw new NullPointerException("Request state argument was null!");
            }

            MrcpEvent event = new MrcpEvent();
            event.setVersion(_request.getVersion());
            event.setMessageLength(-1);
            event.setEventName(eventName);
            event.setRequestID(_request.getRequestID());
            event.setRequestState(requestState);
            event.addHeader(_request.getHeader(MrcpHeaderName.CHANNEL_IDENTIFIER));
            return event;
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.server.MrcpSession#postEvent(com.bytedesk.call.mrcp4j.message.MrcpEvent)
         */
        public synchronized void postEvent(MrcpEvent event)
          throws TimeoutException, IllegalStateException, NullPointerException {
            if (!_ready) {
                throw new IllegalStateException(
                    "Events cannot be posted until a response has been returned to the MRCP client!"
                );
            }
            if (_complete) {
                throw new IllegalStateException(
                    "Events cannot be posted after a request has reached COMPLETE state!"
                );
            }
            if (event == null) {
                throw new NullPointerException("Event argument was null!");
            }

            BlockingQueue<ObjectWrapper<MrcpEvent>> eventQueue = null;
            synchronized (_lock) {
                eventQueue = _eventQueue;
            }
            if (eventQueue == null) {
                // TODO: change to more appropriate exception type.
                throw new TimeoutException(
                    "The MrcpSession has expired due to a period of inactivity."
                );
            }

            try {
                eventQueue.put(new ObjectWrapper<MrcpEvent>(event));
                _complete = event.getRequestState().equals(MrcpRequestState.COMPLETE);
//                if (_complete) {
//                    _eventQueue.put(new ObjectWrapper<MrcpEvent>(null));
//                }
            } catch (InterruptedException e){
                // TODO: change to more appropriate exception type.
                throw (TimeoutException) new TimeoutException(e.getMessage()).initCause(e);
            }
        }

        /**
         * TODOC
         * @return
         */
        MrcpEvent takeEvent() {
            ObjectWrapper<MrcpEvent> eventWrapper = null;
            try {
                eventWrapper = _eventQueue.poll(300, TimeUnit.SECONDS);
                if (eventWrapper == null) {
                    // make sure no postEvent() in progress
                    synchronized (_lock) {
                        eventWrapper = _eventQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (eventWrapper == null) {
                            _eventQueue = null;
                        }
                    }
                }
            } catch (InterruptedException e){
                // TODO: propagate exception?
                log.warn("takeEvent(): interrupted: ", e);
            }
            return (eventWrapper == null) ? null : eventWrapper.getObject();
        }

    }

}
