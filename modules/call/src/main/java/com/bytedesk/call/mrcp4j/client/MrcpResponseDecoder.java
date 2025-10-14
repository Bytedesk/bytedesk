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

import com.bytedesk.call.mrcp4j.MrcpRequestState;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;

import java.io.IOException;
import java.text.ParseException;

/**
 * Decodes response messages received in MRCPv2 format into {@link com.bytedesk.call.mrcp4j.message.MrcpResponse} instances.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpResponseDecoder {

    private static final int RESPONSE_LINE_MRCP_VERSION_PART   = 0;
    private static final int RESPONSE_LINE_MESSAGE_LENGTH_PART = 1;
    private static final int RESPONSE_LINE_REQUEST_ID_PART     = 2;
    private static final int RESPONSE_LINE_STATUS_CODE_PART    = 3;
    private static final int RESPONSE_LINE_REQUEST_STATE_PART  = 4;
    private static final int RESPONSE_LINE_PART_COUNT          = 5;

    public MrcpResponse createResponse(String responseLine) throws IOException, ParseException {

        if (responseLine == null || (responseLine = responseLine.trim()).length() < 1) {
            throw new ParseException("No response-line provided!", -1);
        }

        String[] responseLineParts = responseLine.split(" ");
        if (responseLineParts.length != RESPONSE_LINE_PART_COUNT) {
            throw new ParseException("Incorrect response-line format!", -1);
        }

        MrcpResponse response = new MrcpResponse();

        // mrcp-version
        response.setVersion(responseLineParts[RESPONSE_LINE_MRCP_VERSION_PART]); //TODO: check if this matches request version, maybe at a higher level...

        // message-length
        try {
            response.setMessageLength(
                Integer.parseInt(responseLineParts[RESPONSE_LINE_MESSAGE_LENGTH_PART])
            );
        } catch (NumberFormatException e){
            throw new ParseException("Incorrect message-length format!", -1);
        }

        // request-id
        try {
            response.setRequestID(
                Long.parseLong(responseLineParts[RESPONSE_LINE_REQUEST_ID_PART])
            );
        } catch (NumberFormatException e){
            throw new ParseException("Incorrect request-id format!", -1);
        }

        // status-code
        try {
            response.setStatusCode(
                Short.parseShort(responseLineParts[RESPONSE_LINE_STATUS_CODE_PART])
            );
        } catch (NumberFormatException e){
            throw new ParseException("Incorrect status-code format!", -1);
        }

        // request-state
        try {
            response.setRequestState(
                MrcpRequestState.fromString(responseLineParts[RESPONSE_LINE_REQUEST_STATE_PART])
            );
        } catch (IllegalArgumentException e){
            throw (ParseException) new ParseException("Incorrect request-state format!", -1).initCause(e);
        }

        return response;
    }

}