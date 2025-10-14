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

import com.bytedesk.call.mrcp4j.MrcpResourceType;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.request.StopRequest;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequestFactory.UnimplementedRequest;
import com.bytedesk.call.mrcp4j.server.MrcpSession;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public interface SpeakVerifyRequestHandler extends GenericRequestHandler {

    public static final MrcpResourceType[] RESOURCE_TYPES = { MrcpResourceType.SPEAKVERIFY };

/*
   verifier-method          = "START-SESSION"           ; A
                            / "END-SESSION"             ; B
                            / "QUERY-VOICEPRINT"        ; C
                            / "DELETE-VOICEPRINT"       ; D
                            / "VERIFY"                  ; E
                            / "VERIFY-FROM-BUFFER"      ; F
                            / "VERIFY-ROLLBACK"         ; G
                            / "STOP"                    ; H
                            / "CLEAR-BUFFER"            ; I
                            / "START-INPUT-TIMERS"      ; J
                            / "GET-INTERMEDIATE-RESULT" ; K
*/

    public MrcpResponse startSession(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse endSession(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse queryVoiceprint(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse deleteVoiceprint(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse verify(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse verifyFromBuffer(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse verifyRollback(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse stop(StopRequest request, MrcpSession session);

    public MrcpResponse clearBuffer(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse startInputTimers(UnimplementedRequest request, MrcpSession session);

    public MrcpResponse getIntermediateResult(UnimplementedRequest request, MrcpSession session);

}
