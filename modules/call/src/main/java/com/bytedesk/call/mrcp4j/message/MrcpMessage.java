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
package com.bytedesk.call.mrcp4j.message;

import com.bytedesk.call.mrcp4j.message.header.ChannelIdentifier;
import com.bytedesk.call.mrcp4j.message.header.IllegalValueException;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeader;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public abstract class MrcpMessage {

    public static final String CRLF = "\r\n";
    public static final String MRCP_VERSION_2_0 = "MRCP/2.0";

    private String _version;
    private int _messageLength = -1;
    private long _requestID = -1;

    private Map<String, MrcpHeader> _headers = new LinkedHashMap<String, MrcpHeader>();

    private String _content;

    public void setVersion(String version) {
        _version = version;
    }

    public String getVersion() {
        return _version;
    }

    public void setMessageLength(int messageLength) {
        _messageLength = messageLength;
    }

    public int getMessageLength() {
        return _messageLength;
    }

    public void setRequestID(long requestID) {
        _requestID = requestID;
    }

    public long getRequestID() {
        return _requestID;
    }

    public void addHeader(MrcpHeader header) {
        if (header != null) {
            _headers.put(header.getNameString(), header);
        }
    }

    public MrcpHeader getHeader(MrcpHeaderName name) {
        return _headers.get(name.toString());
    }

    public MrcpHeader getHeader(String name) {
        return _headers.get(name);
    }

    public MrcpHeader removeHeader(MrcpHeaderName name) {
        return removeHeader(name.toString());
    }

    public MrcpHeader removeHeader(String name) {
        return _headers.remove(name);
    }

    public Collection<MrcpHeader> getHeaders() {
        return _headers.values();
    }

    /**
     * Sets the content for the body of the message as well as any applicable headers.
     * @param contentType the MIME type of the content. (required)
     * @param contentId the ID of the content. (optional)
     * @param content the body of the message. (required)
     * @throws IOException if the URL cannot be opened
     */
    public void setContent(String contentType, String contentId, URL content) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(content.openStream()));
        
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append(CRLF);
        }
        setContent(contentType, contentId, sb.substring(0, sb.length()-2));
    }

    /**
     * Sets the content for the body of the message as well as any applicable headers.
     * @param contentType the MIME type of the content. (required)
     * @param contentId the ID of the content. (optional)
     * @param content the body of the message. (required, use <code>removeContent()</code> method to remove previously set content.)
     */
    public void setContent(String contentType, String contentId, String content) {
        if (content == null || (content = content.trim()).length() < 1) {
            throw new IllegalArgumentException(
                "Cannot add zero length or null content, to remove content use removeContent() instead!");
        }

        if (contentType == null || (contentType = contentType.trim()).length() < 1) {
            throw new IllegalArgumentException(
                "contentType is a required parameter, must not be null or zero length");
        }

        if (contentId != null && (contentId = contentId.trim()).length() < 1) {
            contentId = null;
        }

        content = content.concat(CRLF);
        int contentLength = content.length();

        // construct applicable headers
        MrcpHeader contentTypeHeader = MrcpHeaderName.CONTENT_TYPE.constructHeader(contentType);
        MrcpHeader contentIdHeader = (contentId == null) ? null : MrcpHeaderName.CONTENT_ID.constructHeader(contentId);
        MrcpHeader contentLengthHeader = MrcpHeaderName.CONTENT_LENGTH.constructHeader(Integer.valueOf(contentLength));

        // clean-up any old headers
        removeHeader(MrcpHeaderName.CONTENT_TYPE);
        removeHeader(MrcpHeaderName.CONTENT_ID);
        removeHeader(MrcpHeaderName.CONTENT_LENGTH);

        // add new headers
        addHeader(contentTypeHeader);
        addHeader(contentIdHeader);
        addHeader(contentLengthHeader);

        _content = content;
    }

    /**
     * TODOC
     */
    public void removeContent() {

        // clean-up applicable headers
        removeHeader(MrcpHeaderName.CONTENT_TYPE);
        removeHeader(MrcpHeaderName.CONTENT_ID);
        removeHeader(MrcpHeaderName.CONTENT_LENGTH);

        _content = null;
    }

    /**
     * Method for setting the content of the message if the content type, id and length headers
     * have already been set.  Provided primarly for use while decoding messages objects from a
     * MRCP message stream.  MRCP4J clients generally should not use this method.
     * @param content the body of the message.
     */
    public void setContent(String content) {
        _content = content;
    }

    /**
     * @return true if this message has content in the body of the message.
     */
    public boolean hasContent() {
        return _content != null;
    }

    /**
     * TODOC
     * @return Returns the content.
     */
    public String getContent() {
        return _content;
    }

    /**
     * @return the type of the content of the message or null if not set.
     */
    public String getContentType() {
        MrcpHeader header = getHeader(MrcpHeaderName.CONTENT_TYPE);
        return (header == null) ? null : header.getValueString();
    }

    /**
     * @return the channel identifier associated with this message or null if it has not been set or was set with an invalid value.
     * @throws IllegalValueException if an illegal value has been specified for the channel-identifier header.
     */
    public ChannelIdentifier getChannelIdentifier() throws IllegalValueException {
        MrcpHeader header = getHeader(MrcpHeaderName.CHANNEL_IDENTIFIER);
        return (header == null) ? null : (ChannelIdentifier) header.getValueObject();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        // append start line
        StringBuilder sb = appendStartLine(new StringBuilder());

        // append headers
        for (MrcpHeader header : _headers.values()) {
            header.appendTo(sb);
            sb.append(CRLF);
        }

        // append CRLF line
        sb.append(CRLF);

        // append message body if present
        if (hasContent()) {
            sb.append(this.getContent());
        }

        return sb.toString();
    }

    protected abstract StringBuilder appendStartLine(StringBuilder sb);

}