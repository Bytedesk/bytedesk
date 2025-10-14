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
package com.bytedesk.call.mrcp4j.util;

/**
 * Utility class for wrapping object references.  The primary purpose of this class
 * is to allow for null values to be passed to methods that may not accept null values.
 * For example posting a null value to a <code>java.util.concurrent.BlockingQueue</code>.
 * 
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 *
 * @param <O> type of the object being wrapped
 */
public class ObjectWrapper<O> {

    private O _obj;

    /**
     * Constructs an instance that wraps the specified object.
     * @param obj object to be wrapped.  Null values allowed.
     */
    public ObjectWrapper(O obj) {
        _obj = obj;
    }
    
    /**
     * Gets the object wrapped by this instance.
     * @return the wrapped object or null if this instance wraps a null.
     */
    public O getObject() {
        return _obj;
    }

}
