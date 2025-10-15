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
 * Base class for all MRCP4J exception types.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
@SuppressWarnings("serial")
public class MrcpException extends Exception {

    /**
     * 
     */
    public MrcpException() {
        super();
    }

    /**
     * @param message the error message
     */
    public MrcpException(String message) {
        super(message);
    }

    /**
     * @param message the error message
     * @param cause the root cause for this exception
     */
    public MrcpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause the root cause for this exception
     */
    public MrcpException(Throwable cause) {
        super(cause);
    }

}
