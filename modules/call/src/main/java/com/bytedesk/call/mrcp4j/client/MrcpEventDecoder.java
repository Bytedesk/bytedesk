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

import com.bytedesk.call.mrcp4j.common.MrcpEventName;
import com.bytedesk.call.mrcp4j.common.MrcpRequestState;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;

import java.io.IOException;
import java.text.ParseException;

/**
 * Decodes event messages received in MRCPv2 format into {@link com.bytedesk.call.mrcp4j.message.MrcpEvent} instances.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpEventDecoder {

    private static final int EVENT_LINE_MRCP_VERSION_PART   = 0;
    private static final int EVENT_LINE_MESSAGE_LENGTH_PART = 1;
    private static final int EVENT_LINE_EVENT_NAME_PART     = 2;
    private static final int EVENT_LINE_REQUEST_ID_PART     = 3;
    private static final int EVENT_LINE_REQUEST_STATE_PART  = 4;
    private static final int EVENT_LINE_PART_COUNT          = 5;

    public MrcpEvent createEvent(String eventLine) throws IOException, ParseException {

        if (eventLine == null || (eventLine = eventLine.trim()).length() < 1) {
            throw new ParseException("No event-line provided!", -1);
        }

        String[] eventLineParts = eventLine.split(" ");
        if (eventLineParts.length != EVENT_LINE_PART_COUNT) {
            throw new ParseException("Incorrect event-line format!", -1);
        }

        MrcpEvent event = new MrcpEvent();

        // mrcp-version
        event.setVersion(eventLineParts[EVENT_LINE_MRCP_VERSION_PART]); //TODO: check if this matches request version, maybe at a higher level...

        // message-length
        try {
            event.setMessageLength(
                Integer.parseInt(eventLineParts[EVENT_LINE_MESSAGE_LENGTH_PART])
            );
        } catch (NumberFormatException e){
            String message = "Incorrect message-length format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        // event-name
        try {
            event.setEventName(
                MrcpEventName.fromString(eventLineParts[EVENT_LINE_EVENT_NAME_PART])
            );
        } catch (IllegalArgumentException e){
            String message = "Incorrect event-name format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        // request-id
        try {
            event.setRequestID(
                Long.parseLong(eventLineParts[EVENT_LINE_REQUEST_ID_PART])
            );
        } catch (NumberFormatException e){
            String message = "Incorrect request-id format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        // request-state
        try {
            event.setRequestState(
                MrcpRequestState.fromString(eventLineParts[EVENT_LINE_REQUEST_STATE_PART])
            );
        } catch (IllegalArgumentException e){
            String message = "Incorrect request-state format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        return event;
    }

}