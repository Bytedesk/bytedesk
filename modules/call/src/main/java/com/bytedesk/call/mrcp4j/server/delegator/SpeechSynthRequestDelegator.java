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
package com.bytedesk.call.mrcp4j.server.delegator;

import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;
import com.bytedesk.call.mrcp4j.message.request.StopRequest;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequestFactory.UnimplementedRequest;
import com.bytedesk.call.mrcp4j.server.MrcpRequestHandler;
import com.bytedesk.call.mrcp4j.server.MrcpSession;
import com.bytedesk.call.mrcp4j.server.provider.SpeechSynthRequestHandler;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class SpeechSynthRequestDelegator extends GenericRequestDelegator implements MrcpRequestHandler {

    private SpeechSynthRequestHandler _requestHandler;

    public SpeechSynthRequestDelegator(SpeechSynthRequestHandler requestHandler) {
        super(requestHandler);
        _requestHandler = requestHandler;
    }

    public MrcpResponse handleRequest(MrcpRequest request, MrcpSession session) {
        MrcpResponse response = null;

        switch (request.getMethodName()) {
        case SET_PARAMS:
            response = setParams(request, session);
            break;

        case GET_PARAMS:
            response = getParams(request, session);
            break;

        case SPEAK:
            response = speak(request, session);
            break;

        case STOP:
            response = stop(request, session);
            break;

        case PAUSE:
            response = pause(request, session);
            break;

        case RESUME:
            response = resume(request, session);
            break;

        case BARGE_IN_OCCURRED:
            response = bargeInOccurred(request, session);
            break;

        case CONTROL:
            response = control(request, session);
            break;

        case DEFINE_LEXICON:
            response = defineLexicon(request, session);
            break;

        default:
            throw new IllegalArgumentException("Request method does not correspond to this resource type!");

        }

        return response;
    }

    private MrcpResponse speak(MrcpRequest request, MrcpSession session) {
        return _requestHandler.speak(((UnimplementedRequest) request), session);
    }

    private MrcpResponse stop(MrcpRequest request, MrcpSession session) {
        return _requestHandler.stop(((StopRequest) request), session);
    }

    private MrcpResponse pause(MrcpRequest request, MrcpSession session) {
        return _requestHandler.pause(((UnimplementedRequest) request), session);
    }

    private MrcpResponse resume(MrcpRequest request, MrcpSession session) {
        return _requestHandler.resume(((UnimplementedRequest) request), session);
    }

    private MrcpResponse bargeInOccurred(MrcpRequest request, MrcpSession session) {
        return _requestHandler.bargeInOccurred(((UnimplementedRequest) request), session);
    }

    private MrcpResponse control(MrcpRequest request, MrcpSession session) {
        return _requestHandler.control(((UnimplementedRequest) request), session);
    }

    private MrcpResponse defineLexicon(MrcpRequest request, MrcpSession session) {
        return _requestHandler.defineLexicon(((UnimplementedRequest) request), session);
    }
}
