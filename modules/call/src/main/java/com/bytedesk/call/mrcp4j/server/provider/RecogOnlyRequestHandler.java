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
package com.bytedesk.call.mrcp4j.server.provider;

import com.bytedesk.call.mrcp4j.common.MrcpResourceType;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequestFactory.UnimplementedRequest;
import com.bytedesk.call.mrcp4j.message.request.StartInputTimersRequest;
import com.bytedesk.call.mrcp4j.message.request.StopRequest;
import com.bytedesk.call.mrcp4j.server.MrcpSession;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public interface RecogOnlyRequestHandler extends GenericRequestHandler {

    public static final MrcpResourceType[] RESOURCE_TYPES = {
        MrcpResourceType.DTMFRECOG,
        MrcpResourceType.SPEECHRECOG
    };

/*
   recog-only-method    =  "DEFINE-GRAMMAR"          ; A
                        /  "RECOGNIZE"               ; B
                        /  "INTERPRET"               ; C
                        /  "GET-RESULT"              ; D
                        /  "START-INPUT-TIMERS"      ; E
                        /  "STOP"                    ; F
*/

    public MrcpResponse defineGrammar(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse recognize(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse interpret(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse getResult(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse startInputTimers(StartInputTimersRequest request, MrcpSession session);

    public MrcpResponse stop(StopRequest request, MrcpSession session);

}
