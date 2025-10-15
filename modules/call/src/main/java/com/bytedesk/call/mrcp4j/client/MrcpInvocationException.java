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

import com.bytedesk.call.mrcp4j.common.MrcpException;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;

/**
 * Thrown when an exception is encountered while invoking an MRCPv2 request. For example when
 * an MRCP response returned by an MRCP resource contains an error code.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@SuppressWarnings("serial")
public class MrcpInvocationException extends MrcpException {

    private final MrcpResponse _response;

    /**
     * @param response the response that triggered the exception to be thrown
     */
    public MrcpInvocationException(MrcpResponse response) {
        //TODO: set response message text as the exception message
        super("MRCPv2 Status Code:" + response.getStatusCode() + "[" + response.getStatusDesc() + "]");
        _response = response;
    }

    /**
     * Retrieves the response that triggered the exception to be thrown.
     * @return the response that triggered the exception to be thrown.
     */
    public MrcpResponse getResponse() {
        return _response;
    }

}
