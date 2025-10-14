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
import java.io.IOException;
import java.text.ParseException;

import com.bytedesk.call.mrcp4j.message.MrcpMessage;
import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeader;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;

import lombok.extern.slf4j.Slf4j;

/**
 * Decodes messages received in MRCPv2 format into {@link com.bytedesk.call.mrcp4j.message.MrcpMessage} instances.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class MrcpMessageDecoder {

    private MrcpResponseDecoder _responseDecoder = new MrcpResponseDecoder();
    private MrcpEventDecoder _eventDecoder = new MrcpEventDecoder();


    private static final int RESPONSE_LINE_REQUEST_ID_PART  = 2;
    private static final int START_LINE_PART_COUNT          = 5;

    // TODO: change ParseException to MrcpProtocolException
    public MrcpMessage decode(BufferedReader in) throws IOException, ParseException {

        // read until the first non-empty line to get the start-line
        String line = null;
        while ((line = in.readLine()) == null || (line = line.trim()).equals("")) {
            if (log.isTraceEnabled())
                log.trace((line == null) ? "MrcpMessageDecoder: null line" : "MrcpMessageDecoder: empty line");
            try {
                Thread.sleep(100); // TODO: make sleep time configurable
            } catch (InterruptedException e) {
                log.debug("InterruptedException", e);
            }
        }

        // verify the start-line contains the correct number of parts
        String[] startLineParts = line.split(" ");
        if (startLineParts.length != START_LINE_PART_COUNT) {
            throw new ParseException("Incorrect start-line format!", -1);
        }

        // determine if the message is a response or an event message
        boolean isResponse = false;
        try {
            Long.parseLong(startLineParts[RESPONSE_LINE_REQUEST_ID_PART]);
            isResponse = true;
        } catch (NumberFormatException e){
            // ignore, message should be event
        }

        // create the message from the start-line
        MrcpMessage message = null;
        if (isResponse) {
            message = _responseDecoder.createResponse(line);
        } else {
            message = _eventDecoder.createEvent(line);
        }

        // populate message headers
        while ((line = in.readLine()) != null && !(line = line.trim()).equals("")) {
            // TODO: handle multi-line headers
            int index = line.indexOf(':');
            if (index < 1) {
                throw new ParseException("Incorrect message-header format!", -1);
            }
            String name = line.substring(0, index);
            String value = line.substring(index + 1).trim();
            MrcpHeader header = MrcpHeaderName.createHeader(name, value);
            message.addHeader(header);
        }

        // read message content if present
        MrcpHeader contentLengthHeader = message.getHeader(MrcpHeaderName.CONTENT_LENGTH);
        int contentLength = 0;
        try {
            contentLength = (contentLengthHeader == null) ? 0 : ((Integer) contentLengthHeader.getValueObject()).intValue();
        } catch (IllegalValueException e) {
            throw new ParseException(e.getMessage(), -1);
        }
        if (contentLength > 0) {
            char[] content = new char[contentLength];
            int length = in.read(content, 0, contentLength);
            if (length != contentLength) {
                throw new ParseException("Content length mismatch, expected " +
                        contentLength + ", got " + length, -1);
            }
            message.setContent(new String(content));
        }

        return message;
    }


}