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

import static com.bytedesk.call.mrcp4j.message.MrcpMessage.CRLF;

import com.bytedesk.call.mrcp4j.message.header.MrcpHeader;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Encodes {@link com.bytedesk.call.mrcp4j.message.request.MrcpRequest} instances into MRCPv2 specification format.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpRequestEncoder {

    public void encode(MrcpRequest request, PrintWriter out) throws IOException {

        StringBuilder messageBuffer = new StringBuilder();

        // append request line
        int offset = appendResponseLine(messageBuffer, request);

        // append headers
        for (MrcpHeader header : request.getHeaders()) {
            messageBuffer.append(header.toString()).append(CRLF);
        }

        // append CRLF line
        messageBuffer.append(CRLF);

        // append message content if present
        if (request.hasContent()) {
            messageBuffer.append(request.getContent());
        }

        // determine and set message length
        int bufferLength = messageBuffer.length();
        int bufferLengthLength = Integer.toString(bufferLength).length();
        int messageLength = bufferLength + bufferLengthLength;
        String messageLengthString = Integer.toString(messageLength);
        if (messageLengthString.length() > bufferLengthLength) {
            messageLengthString = Integer.toString(++messageLength);
        }
        messageBuffer.insert(offset, messageLengthString);
        request.setMessageLength(messageLength);
        bufferLength = messageBuffer.length();

        // write message to out
        out.print(messageBuffer.toString());

    }

    private static int appendResponseLine(StringBuilder encodeBuf, MrcpRequest request) {
        String version = request.getVersion();
        encodeBuf.append(version).append(' ');
        // message length will be inserted at this position after headers and content are encoded
        encodeBuf.append(' ').append(request.getMethodNameAsString());
        encodeBuf.append(' ').append(request.getRequestID());
        encodeBuf.append(CRLF);
        return version.length() + 1;
    }

}
