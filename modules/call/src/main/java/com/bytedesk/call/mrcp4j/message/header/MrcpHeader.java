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

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class MrcpHeader {

    private MrcpHeaderName _name;
    private String _valueString;
    private Object _valueObject;
    
    /*public MrcpHeader(MrcpHeaderName name, Object valueObject) {
        this(name, valueObject.toString(), valueObject);
    }*/

    MrcpHeader(MrcpHeaderName name, String valueString, Object valueObject) {
        _name = name;
        _valueString = valueString;
        _valueObject = valueObject;
    }

    public MrcpHeaderName getHeaderName() {
        return _name;
    }

    public String getNameString() {
        return _name.toString();
    }
    
    public boolean isValidValue() {
        return !(_valueObject == null || _valueObject instanceof Throwable);
    }

    public Object getValueObject() throws IllegalValueException {
        if (_valueObject == null) {
            throw new IllegalValueException("Value object could not be constructed for value string: " + _valueString);
        }

        if (_valueObject instanceof Throwable) {
            if (_valueObject instanceof IllegalValueException) {
                throw (IllegalValueException) _valueObject;
            }
            throw new IllegalValueException("Value object could not be constructed for value string: " +
                    _valueString, (Throwable) _valueObject);
        }

        return _valueObject;
    }

    public String getValueString() {
        return _valueString;
    }

    public StringBuilder appendTo(StringBuilder sb) {
        sb.append(_name);
        sb.append(":");
        sb.append(_valueString);
        return sb;
    }

    @Override
    public String toString() {
        return appendTo(new StringBuilder()).toString();
    }

}