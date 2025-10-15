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
 * Defines the resource types that are valid for MRCPv2.
 * 
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public enum MrcpResourceType {

    /**
     * A full speech recognition resource that is capable of receiving a media stream
     * containing audio and interpreting it to recognition results.
     */
    SPEECHRECOG  ("speechrecog"),

    /**
     * A recognition resource capable of extracting and interpreting DTMF digits in
     * a media stream and matching them against a supplied digit grammar.
     */
    DTMFRECOG    ("dtmfrecog"),

    /**
     * A full capability speech synthesis resource capable of rendering speech from text.
     */
    SPEECHSYNTH  ("speechsynth"),

    /**
     * A speech synthesizer resource with very limited capabilities that can generate its
     * media stream exclusively from concatenated audio clips.
     */
    BASICSYNTH   ("basicsynth"),

    /**
     * A resource capable of verifying the authenticity of a claimed identity by matching a
     * media stream containing a voice to a pre-existing voice-print.
     */
    SPEAKVERIFY  ("speakverify"),

    /**
     * A resource capable of recording audio and saving it to a URI.
     */
    RECORDER     ("recorder");


    private String _name;

    MrcpResourceType(String name) {
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
     * Converts an MRCP resource type in string format to the appropriate MRCP4J enum value.
     * @param str MRCP resource type.
     * @return the resource type enum instance corresponding to the string value specified.
     * @throws IllegalArgumentException if the string value specified does not correspond to
     * an existing MRCP resource type.
     */
    public static MrcpResourceType fromString(String str) throws IllegalArgumentException {
        for (MrcpResourceType value : MrcpResourceType.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid MRCP resource type: " + str);
    }

    /**
     * Parses an MRCP channel ID to extract the resource type of the channel as the appropriate MRCP4J enum value.
     * @param channelID ID of the channel for which the resource type needs to be determined.
     * @return the resource type enum instance corresponding to the string value contained in the specified channel ID.
     * @throws IllegalArgumentException if the channel ID is not correctly formatted.
     */
    public static MrcpResourceType fromChannelID(String channelID) {
        String[] tokens = channelID.split("@");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Illegal Channel-Identifier value: " + channelID);
        }
        return fromString(tokens[1]);
    }

}