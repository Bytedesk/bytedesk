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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class GenericValueFactory extends BaseValueFactory {
    
    private Constructor<?> _constructor;

    protected GenericValueFactory(Class<?> valueClass) {
        super(valueClass);
        try {
            _constructor = valueClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }

    /* (non-Javadoc)
     * @see com.bytedesk.call.mrcp4j.message.header.ValueFactory#fromValueString(java.lang.String)
     */
    public Object fromValueString(String valueString) throws IllegalValueException {
        try {
            Object valueObject = _constructor.newInstance(valueString);
            validateObject(valueObject);
            return valueObject;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            if (cause instanceof IllegalValueException) {
                throw (IllegalValueException) cause;
            }
            throw new IllegalValueException("Illegal " + getValueClass().getName() +
                    " value: " + valueString, (cause == null) ? e : cause);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}