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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public class RequestIdList {
    
    private List<Long> _idList = new ArrayList<Long>();
    
    public void addRequestId(Long requestId) {
        _idList.add(requestId);
    }

    /**
     * @return Returns the list of request IDs.
     */
    public List<Long> getIdList() {
        return new ArrayList<Long>(_idList);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Long id : _idList) {
            sb.append(id).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    static class Factory extends BaseValueFactory {

        Factory() {
            super(RequestIdList.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.ValueFactory#fromValueString(java.lang.String)
         */
        public Object fromValueString(String valueString) throws IllegalValueException {
            RequestIdList instance = new RequestIdList();
            String[] tokens = valueString.split(",");
            for (String token : tokens) {
                try {
                    Long requestId = Long.valueOf(token.trim());
                    instance.addRequestId(requestId);
                } catch (NumberFormatException  e) {
                    throw new IllegalValueException("Illegal request-id-list value: " + valueString, e);
                }
            }
            return instance;
        }

    }

}
