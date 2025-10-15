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
package com.bytedesk.call.mrcp4j.common;

/**
 * Defines the request states that are valid for MRCPv2.
 * 
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 * @see com.bytedesk.call.mrcp4j.server.MrcpSession#createResponse(short, com.bytedesk.call.mrcp4j.common.MrcpRequestState)
 * @see com.bytedesk.call.mrcp4j.server.MrcpSession#createEvent(com.bytedesk.call.mrcp4j.MrcpEventName, com.bytedesk.call.mrcp4j.common.MrcpRequestState)
 */
public enum MrcpRequestState {

    /**
     * The request has been placed on a queue and will be processed in first-in-first-out order.
     */
    PENDING     ("PENDING"),

    /**
     * The request is being processed and is not yet complete.
     */
    IN_PROGRESS ("IN-PROGRESS"),

    /**
     * The request has been processed to completion and there will be no more events or other
     * messages from the resource to the client with this request-id.
     */
    COMPLETE    ("COMPLETE");

    private String _name;

    MrcpRequestState(String name) {
        _name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _name;
    }

    /**
     * Converts an MRCP request state in string format to the appropriate MRCP4J enum value.
     * @param str MRCP request state
     * @return the request state enum instance corresponding to the string value specified
     * @throws IllegalArgumentException if the string value specified does not correspond to
     * an existing MRCP request state
     */
    public static MrcpRequestState fromString(String str) throws IllegalArgumentException {
        for (MrcpRequestState value : MrcpRequestState.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid MRCP request-state: " + str);
    }

}