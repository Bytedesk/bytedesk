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

import java.text.ParseException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeader;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequestFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Decodes request messages received in MRCPv2 format into {@link com.bytedesk.call.mrcp4j.message.request.MrcpRequest} instances.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@Slf4j
public class MrcpRequestDecoder implements ProtocolDecoder {

    private StringBuilder decodeBuf = new StringBuilder();

    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
      throws Exception {
        try {

            // create request from request-line
            MrcpRequest request = createRequest(readLine(in));
            
            // read message-header
            String line = null;
            while ( (line = readLine(in)) != null && !(line = line.trim()).equals("") ) {
                // TODO: handle multi-line headers
                int index = line.indexOf(':');
                if (index < 1) {
                    throw new ParseException("Incorrect message-header format!", -1);
                }
                String name = line.substring(0, index);
                String value = line.substring(index + 1).trim();
                MrcpHeader header = MrcpHeaderName.createHeader(name, value);

                request.addHeader(header);
            }

            // read request message body if present
            MrcpHeader contentLengthHeader = request.getHeader(MrcpHeaderName.CONTENT_LENGTH);
            int contentLength = 0;
            try {
                contentLength = (contentLengthHeader == null) ? 0 : ((Integer) contentLengthHeader.getValueObject()).intValue();
            } catch (IllegalValueException e) {
                throw new ProtocolDecoderException(e.getMessage(), e);
            }
            if (contentLength > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < contentLength; i++) {
                    byte b = in.get();
                    sb.append((char) b);
                } // TODO: handle exceptions
                request.setContent(sb.toString());
            }

            // write request object to out
            out.write(request);

        } catch (ParseException e) {
            //TODO: return 408 response to client?
            log.debug("ParseException", e);
            throw new ProtocolDecoderException(e.getMessage(), e);
        } catch (RuntimeException e) {
            log.debug("RuntimeException", e);
            throw e;
        }
    }

    private String readLine(IoBuffer in) {
        if (!in.hasRemaining()) {
            return null;
        }

        decodeBuf.delete(0, decodeBuf.length());
        boolean done = false;
        do {
            byte b = in.get();
            switch(b) {
            case '\r':
                break;
            case '\n':
                done = true;
                break;
            default:
                decodeBuf.append((char) b);
            }
        } while (!done && in.hasRemaining());

        return decodeBuf.toString();
    }

    private static final int REQUEST_LINE_MRCP_VERSION_PART   = 0;
    private static final int REQUEST_LINE_MESSAGE_LENGTH_PART = 1;
    private static final int REQUEST_LINE_METHOD_NAME_PART    = 2;
    private static final int REQUEST_LINE_REQUEST_ID_PART     = 3;
    private static final int REQUEST_LINE_PART_COUNT          = 4;

    public static MrcpRequest createRequest(String requestLine) throws ParseException {

        if (requestLine == null || (requestLine = requestLine.trim()).length() < 1) {
            throw new ParseException("No request-line provided!", -1);
        }

        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length != REQUEST_LINE_PART_COUNT) {
            throw new ParseException("Incorrect request-line format!", -1);
        }

        MrcpRequest request = null;

        // construct request from method-name
        try {
            request = MrcpRequestFactory.createRequest(requestLineParts[REQUEST_LINE_METHOD_NAME_PART]);
        } catch (IllegalArgumentException e){
            String message = "Incorrect method-name format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        // mrcp-version
        request.setVersion(requestLineParts[REQUEST_LINE_MRCP_VERSION_PART]);  //TODO: need to check here if version is supported, or maybe at higher level...

        // message-length
        try {
            request.setMessageLength(
                Integer.parseInt(requestLineParts[REQUEST_LINE_MESSAGE_LENGTH_PART])
            );
        } catch (NumberFormatException e){
            String message = "Incorrect message-length format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        // request-id
        try {
            request.setRequestID(
                Long.parseLong(requestLineParts[REQUEST_LINE_REQUEST_ID_PART])
            );
        } catch (NumberFormatException e){
            String message = "Incorrect request-id format!";
            throw (ParseException) new ParseException(message, -1).initCause(e);
        }

        return request;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        // No buffering in this codec
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // No resources to clean up
    }
}
