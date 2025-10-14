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
public class CompletionCause {

    private short _causeCode = -1;
    private String _causeName = null;
    private String _valueString = null;

    /**
     * @param causeCode coded value (000, 001, etc.) signifying the cause for request completion.
     * @param causeName textual representation of the cause for request completion.
     * @throws IllegalArgumentException if the Cause-Code or Cause-Name values provided are not valid.
     */
    public CompletionCause(short causeCode, String causeName) throws IllegalArgumentException {
        this (causeCode, causeName, constructValueString(causeCode, causeName));
    }

    CompletionCause(short causeCode, String causeName, String valueString) throws IllegalArgumentException {
        if (causeName == null || valueString == null) {
            throw new NullPointerException();
        }
        _causeCode = causeCode;
        _causeName = causeName;
        _valueString = valueString;
    }

    /**
     * @return the Cause-Code.
     */
    public short getCauseCode() {
        return _causeCode;
    }

    /**
     * @return the Cause-Name.
     */
    public String getCauseName() {
        return _causeName;
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
        if (obj instanceof CompletionCause) {
            return _valueString.equals(((CompletionCause) obj)._valueString);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _causeCode;
    }

    private static String constructValueString(short causeCode, String causeName) throws IllegalArgumentException {
        if (causeCode < 0 || causeCode > 999) {
            throw new IllegalArgumentException("Illegal Cause-Code value: " + causeCode);
        }
        if (causeName == null || (causeName = causeName.trim()).length() < 1 || causeName.indexOf(' ') > -1) {
            throw new IllegalArgumentException("Illegal Cause-Name value: " + causeName);
        }

        StringBuilder sb = new StringBuilder();
        if (causeCode < 100) {
            sb.append('0');
            if (causeCode < 10) {
                sb.append('0');
            }
        }
        sb.append(causeCode);
        sb.append(' ');
        sb.append(causeName);

        return sb.toString();
    }

    static class Factory extends BaseValueFactory {

        Factory() {
            super(CompletionCause.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.ValueFactory#fromValueString(java.lang.String)
         */
        public Object fromValueString(String valueString) throws IllegalValueException {
            String[] tokens = valueString.split(" ");
            if (tokens.length != 2) {
                throw new IllegalValueException("Illegal Completion-Cause header value: " + valueString);
            }

            try {
                short causeCode = Short.parseShort(tokens[0]);
                if (causeCode < 0 || causeCode > 999) {
                    throw new IllegalValueException("Illegal Completion-Cause code: " + valueString);
                }
                return new CompletionCause(causeCode, tokens[1].trim(), valueString);
            } catch (NumberFormatException e) {
                throw new IllegalValueException("Illegal Completion-Cause code: " + valueString, e);
            }
        }

    }

}
