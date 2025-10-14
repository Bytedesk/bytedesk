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
package com.bytedesk.call.mrcp4j.message.request;

import com.bytedesk.call.mrcp4j.MrcpMethodName;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpRequestFactory {

    private MrcpRequestFactory() {
        // restrict instance initialization to private access
    }

    public static MrcpRequest createRequest(String methodName) {
        return createRequest(MrcpMethodName.fromString(methodName));
    }

    public static MrcpRequest createRequest(MrcpMethodName methodName) {
        MrcpRequest request = null;

        switch (methodName) {
        case RECORD:
            request = new RecordRequest();
            break;

        case STOP:
            request = new StopRequest();
            break;

        case START_INPUT_TIMERS:
            request = new StartInputTimersRequest();
            break;

        default:
            request = new UnimplementedRequest(methodName); // TODO: should throw Exception instead when all possible methods have been specified
        }

        return request;
    }
    
    public static MrcpRequest createVendorSpecificRequest(String methodName) {
    	MrcpRequest request = null;
    	request = new UnimplementedRequest(methodName);
    	return request;
    }

    // temporary class for unimplemented request types
    public static class UnimplementedRequest extends MrcpRequest {
        UnimplementedRequest(MrcpMethodName methodName) {
            super(methodName);
        }
        UnimplementedRequest(String methodName) {
            super(methodName);
        }
    }

}