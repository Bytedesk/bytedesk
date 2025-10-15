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
package com.bytedesk.call.mrcp4j.message.header;

import com.bytedesk.call.mrcp4j.common.MrcpResourceType;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class ChannelIdentifier {

    private String _channelID;
    private MrcpResourceType _resourceType;
    private String _valueString;

    public ChannelIdentifier(String channelID, MrcpResourceType type) {
        this(channelID, type, constructValueString(channelID, type));
    }

    ChannelIdentifier(String channelID, MrcpResourceType type, String valueString) {
        if (channelID == null || type == null || valueString == null) {
            throw new NullPointerException();
        }
        _channelID = channelID;
        _resourceType = type;
        _valueString = valueString;
    }

    public String getChannelID() {
        return _channelID;
    }

    public MrcpResourceType getResourceType() {
        return _resourceType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _valueString;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChannelIdentifier) {
            return _valueString.equals(((ChannelIdentifier) obj)._valueString);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _valueString.hashCode();
    }

    private static String constructValueString(String channelID, MrcpResourceType type) {
        if (channelID == null || (channelID = channelID.trim()).length() < 1) {
            throw new IllegalArgumentException("Empty/null channel-id value: " + channelID);
        }
        if (type == null) {
            throw new NullPointerException("Null resource-type for channel-identifier not allowed!");
        }

        StringBuilder sb = new StringBuilder(channelID);
        sb.append('@');
        sb.append(type.toString());
        return sb.toString();
    }

    static class Factory extends BaseValueFactory {

        Factory() {
            super(ChannelIdentifier.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.ValueFactory#fromValueString(java.lang.String)
         */
        public Object fromValueString(String valueString) throws IllegalValueException {
            String[] tokens = valueString.split("@");
            if (tokens.length != 2) {
                throw new IllegalValueException("Illegal Channel-Identifier value: " + valueString);
            }
            MrcpResourceType resourceType = MrcpResourceType.fromString(tokens[1].trim());
            return new ChannelIdentifier(tokens[0].trim(), resourceType, valueString);
        }

    }


}
