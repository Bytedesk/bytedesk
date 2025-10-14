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
import com.bytedesk.call.mrcp4j.message.request.RecordRequest;
import com.bytedesk.call.mrcp4j.message.request.StartInputTimersRequest;
import com.bytedesk.call.mrcp4j.message.request.StopRequest;
import com.bytedesk.call.mrcp4j.server.MrcpRequestHandler;
import com.bytedesk.call.mrcp4j.server.MrcpSession;
import com.bytedesk.call.mrcp4j.server.provider.RecorderRequestHandler;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class RecorderRequestDelegator extends GenericRequestDelegator implements MrcpRequestHandler {

    private RecorderRequestHandler _requestHandler;

    public RecorderRequestDelegator(RecorderRequestHandler requestHandler) {
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

        case RECORD:
            response = record(request, session);
            break;

        case STOP:
            response = stop(request, session);
            break;

        case START_INPUT_TIMERS:
            response = startInputTimers(request, session);
            break;

        default:
            throw new IllegalArgumentException("Request method does not correspond to this resource type!");

        }

        return response;
    }

    private MrcpResponse record(MrcpRequest request, MrcpSession session) {
        return _requestHandler.record(((RecordRequest) request), session);
    }

    private MrcpResponse stop(MrcpRequest request, MrcpSession session) {
        return _requestHandler.stop(((StopRequest) request), session);
    }

    private MrcpResponse startInputTimers(MrcpRequest request, MrcpSession session) {
        return _requestHandler.startInputTimers(((StartInputTimersRequest) request), session);
    }

}
