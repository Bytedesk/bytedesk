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
 * Defines the event names that are valid values for MRCPv2 event messages.
 * 
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 * @see com.bytedesk.call.mrcp4j.server.MrcpSession#createEvent(com.bytedesk.call.mrcp4j.common.MrcpEventName, com.bytedesk.call.mrcp4j.MrcpRequestState)
 */
public enum MrcpEventName {

/*
   synthesizer-event    =  "SPEECH-MARKER"     ; H
                        /  "SPEAK-COMPLETE"    ; I
*/
    /**
     * This event from the synthesizer resource to the client is generated when the synthesizer
     * encounters a marker tag in the speech markup it is currently processing.
     */
    SPEECH_MARKER                   ("SPEECH-MARKER"),

    /**
     * This event from the synthesizer resource to the client indicates that the
     * corresponding "SPEAK" request was completed.
     */
    SPEAK_COMPLETE                  ("SPEAK-COMPLETE"),

/*
   recognizer-event     =  "START-OF-INPUT"          ; L
                        /  "RECOGNITION-COMPLETE"    ; M
                        /  "INTERPRETATION-COMPLETE" ; N
*/
    /**
     * This event from the recognition resource, the recorder resource or the verification
     * resource to the client indicates speech or DTMF has been detected by the resource.
     */
    START_OF_INPUT                  ("START-OF-INPUT"),

    /**
     * This event from the recognizer resource to the client indicates that recognition has completed.
     */
    RECOGNITION_COMPLETE            ("RECOGNITION-COMPLETE"),

    /**
     * This event from the recognition resource to the client indicates that the INTERPRET operation is complete.
     */
    INTERPRETATION_COMPLETE         ("INTERPRETATION-COMPLETE"),

/*
   recorder-event       =  "START-OF-INPUT"      ; D
                        /  "RECORD-COMPLETE"     ; E
*/
    //START_OF_INPUT                ("START-OF-INPUT"),  <- already defined under recognizer-event
    /**
     * This event from the recorder resource to the client indicates that recording has completed due either to
     * no-input, silence after speech or max-time exceeded.
     */
    RECORD_COMPLETE                 ("RECORD-COMPLETE"),

/*
   verification-event       =  "VERIFICATION-COMPLETE"  ; L
                            /  "START-OF-INPUT"         ; M
*/
    /**
     * This event from the verification resource to the client follows a call to VERIFY or VERIFY-FROM-BUFFER
     * and is used to communicate the verification results to the client.
     */
    VERIFICATION_COMPLETE           ("VERIFICATION-COMPLETE");
    //START_OF_INPUT                ("START-OF-INPUT"),  <- already defined under recognizer-event


    private String _name;

    private MrcpEventName(String name) {
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
     * Converts an MRCP event name in string format to the appropriate MRCP4J enum value.
     * @param str MRCP event string
     * @return the event name enum instance corresponding to the string name specified
     * @throws IllegalArgumentException if the string value specified does not correspond to
     * a valid MRCP event name
     */
    public static MrcpEventName fromString(String str) throws IllegalArgumentException {
        for (MrcpEventName value : MrcpEventName.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid MRCP event-name: " + str);
    }

}