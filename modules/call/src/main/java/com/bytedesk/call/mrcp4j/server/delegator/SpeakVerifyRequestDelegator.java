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
import com.bytedesk.call.mrcp4j.server.provider.SpeakVerifyRequestHandler;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class SpeakVerifyRequestDelegator extends GenericRequestDelegator implements MrcpRequestHandler {

    private SpeakVerifyRequestHandler _requestHandler;

    public SpeakVerifyRequestDelegator(SpeakVerifyRequestHandler requestHandler) {
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

        case START_SESSION:
            response = startSession(request, session);
            break;

        case END_SESSION:
            response = endSession(request, session);
            break;

        case QUERY_VOICEPRINT:
            response = queryVoiceprint(request, session);
            break;

        case DELETE_VOICEPRINT:
            response = deleteVoiceprint(request, session);
            break;

        case VERIFY:
            response = verify(request, session);
            break;

        case VERIFY_FROM_BUFFER:
            response = verifyFromBuffer(request, session);
            break;

        case VERIFY_ROLLBACK:
            response = verifyRollback(request, session);
            break;

        case STOP:
            response = stop(request, session);
            break;

        case CLEAR_BUFFER:
            response = clearBuffer(request, session);
            break;

        case START_INPUT_TIMERS:
            response = startInputTimers(request, session);
            break;

        case GET_INTERMEDIATE_RESULT:
            response = getIntermediateResult(request, session);
            break;

        default:
            throw new IllegalArgumentException("Request method does not correspond to this resource type!");

        }

        return response;
    }

    private MrcpResponse startSession(MrcpRequest request, MrcpSession session) {
        return _requestHandler.startSession(((UnimplementedRequest) request), session);
    }

    private MrcpResponse endSession(MrcpRequest request, MrcpSession session) {
        return _requestHandler.endSession(((UnimplementedRequest) request), session);
    }

    private MrcpResponse queryVoiceprint(MrcpRequest request, MrcpSession session) {
        return _requestHandler.queryVoiceprint(((UnimplementedRequest) request), session);
    }

    private MrcpResponse deleteVoiceprint(MrcpRequest request, MrcpSession session) {
        return _requestHandler.deleteVoiceprint(((UnimplementedRequest) request), session);
    }

    private MrcpResponse verify(MrcpRequest request, MrcpSession session) {
        return _requestHandler.verify(((UnimplementedRequest) request), session);
    }

    private MrcpResponse verifyFromBuffer(MrcpRequest request, MrcpSession session) {
        return _requestHandler.verifyFromBuffer(((UnimplementedRequest) request), session);
    }

    private MrcpResponse verifyRollback(MrcpRequest request, MrcpSession session) {
        return _requestHandler.verifyRollback(((UnimplementedRequest) request), session);
    }

    private MrcpResponse stop(MrcpRequest request, MrcpSession session) {
        return _requestHandler.stop(((StopRequest) request), session);
    }

    private MrcpResponse clearBuffer(MrcpRequest request, MrcpSession session) {
        return _requestHandler.clearBuffer(((UnimplementedRequest) request), session);
    }

    private MrcpResponse startInputTimers(MrcpRequest request, MrcpSession session) {
        return _requestHandler.startInputTimers(((UnimplementedRequest) request), session);
    }

    private MrcpResponse getIntermediateResult(MrcpRequest request, MrcpSession session) {
        return _requestHandler.getIntermediateResult(((UnimplementedRequest) request), session);
    }

}
