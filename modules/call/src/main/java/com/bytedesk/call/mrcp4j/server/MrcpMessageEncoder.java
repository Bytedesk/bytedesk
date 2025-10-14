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

import static com.bytedesk.call.mrcp4j.message.MrcpMessage.CRLF;

import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.MrcpServerMessage;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeader;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderException;


/**
 * Encodes {@link com.bytedesk.call.mrcp4j.message.MrcpMessage} instances into MRCPv2 specification format.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpMessageEncoder implements ProtocolEncoder {

    private StringBuilder _encodeBuf = new StringBuilder();

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
      throws Exception {

        // clear encode buffer
        _encodeBuf.delete(0, _encodeBuf.length());

        // append start line
        int offset = -1;
        if (message instanceof MrcpResponse) {
            offset = appendResponseLine(_encodeBuf, ((MrcpResponse) message));
        } else if (message instanceof MrcpEvent) {
            offset = appendEventLine(_encodeBuf, ((MrcpEvent) message));
        } else {
            throw new ProtocolEncoderException("Unsupported message type: " + message.getClass().getName());
        }

        // append headers
        MrcpServerMessage serverMessage = (MrcpServerMessage) message;
        for (MrcpHeader header : serverMessage.getHeaders()) {
            _encodeBuf.append(header.toString()).append(CRLF);
        }

        // append CRLF line
        _encodeBuf.append(CRLF);

        // append message body if present
        if (serverMessage.hasContent()) {
            _encodeBuf.append(serverMessage.getContent());
        }

        // determine and set message length
        int bufferLength = _encodeBuf.length();
        int bufferLengthLength = Integer.toString(bufferLength).length();
        int messageLength = bufferLength + bufferLengthLength;
        String messageLengthString = Integer.toString(messageLength);
        if (messageLengthString.length() > bufferLengthLength) {
            messageLengthString = Integer.toString(++messageLength);
        }
        _encodeBuf.insert(offset, messageLengthString);
        serverMessage.setMessageLength(messageLength);
        bufferLength = _encodeBuf.length();

        // write _encodeBuf to out
        IoBuffer bytes = IoBuffer.allocate(bufferLength);
        for (int i = 0; i < bufferLength; i++) {
            bytes.put((byte) _encodeBuf.charAt(i));
        }
        bytes.flip();
        out.write(bytes);
    }

    private static int appendEventLine(StringBuilder encodeBuf, MrcpEvent event) {
        String version = event.getVersion();
        encodeBuf.append(version).append(' ');
        // message length will be inserted at this position after headers and content are encoded
        encodeBuf.append(' ').append(event.getEventName());
        encodeBuf.append(' ').append(event.getRequestID());
        encodeBuf.append(' ').append(event.getRequestState());
        encodeBuf.append(CRLF);
        return version.length() + 1;
    }

    private static int appendResponseLine(StringBuilder encodeBuf, MrcpResponse response) {
        String version = response.getVersion();
        encodeBuf.append(version).append(' ');
        // message length will be inserted at this position after headers and content are encoded
        encodeBuf.append(' ').append(response.getRequestID());
        encodeBuf.append(' ').append(response.getStatusCode());
        encodeBuf.append(' ').append(response.getRequestState());
        encodeBuf.append(CRLF);
        return version.length() + 1;
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // No resources to clean up
    }
}