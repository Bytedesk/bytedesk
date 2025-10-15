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
 * Defines the method names that are valid values for MRCPv2 request messages.
 * 
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 * @see com.bytedesk.call.mrcp4j.message.request.MrcpRequestFactory#createRequest(com.bytedesk.call.mrcp4j.common.MrcpMethodName)
 */
public enum MrcpMethodName {

/*
   generic-method      =    "SET-PARAMS"
                       /    "GET-PARAMS"
*/
    /**
     * Tells the MRCP resource to define parameters for the session.
     */
    SET_PARAMS              ("SET-PARAMS"),

    /**
     * Asks the MRCP resource for its current session parameters.
     */
    GET_PARAMS              ("GET-PARAMS"),

/*
   synthesizer-method   =  "SPEAK"             ; A
                        /  "STOP"              ; B
                        /  "PAUSE"             ; C
                        /  "RESUME"            ; D
                        /  "BARGE-IN-OCCURRED" ; E
                        /  "CONTROL"           ; F
                        /  "DEFINE-LEXICON"    ; G
*/
    /**
     * Provides the synthesizer resource with the speech text and initiates
     * speech synthesis and streaming.
     */
    SPEAK                   ("SPEAK"),

    /**
     * Tells the MRCP resource to stop an operation in progress, for example tells
     * the synthesizer resource to stop speaking if it is speaking something.
     */
    STOP                    ("STOP"),

    /**
     * Tells the synthesizer resource to pause speech output if it is speaking something.
     */
    PAUSE                   ("PAUSE"),

    /**
     * Tells a paused synthesizer resource to resume speaking.
     */
    RESUME                  ("RESUME"),

    /**
     * Provides a client which has received a barge-in-able event a means to communicate
     * the occurrence of the event to the synthesizer resource.
     */
    BARGE_IN_OCCURRED       ("BARGE-IN-OCCURRED"),

    /**
     * Directs a synthesizer resource that is speaking to modify what it is speaking on the fly.
     */
    CONTROL                 ("CONTROL"),

    /**
     * Provides a lexicon and tells the server to load, unload, activate or deactivate the lexicon.
     */
    DEFINE_LEXICON          ("DEFINE-LEXICON"),

/*
   recog-only-method    =  "DEFINE-GRAMMAR"          ; A
                        /  "RECOGNIZE"               ; B
                        /  "INTERPRET"               ; C
                        /  "GET-RESULT"              ; D
                        /  "START-INPUT-TIMERS"      ; E
                        /  "STOP"                    ; F
*/
    /**
     * Specifies one or more grammars to the recognition resource and requests the resource to access, fetch, and
     * compile the grammars as needed.
     */
    DEFINE_GRAMMAR          ("DEFINE-GRAMMAR"),

    /**
     * Requests the recognizer to start recognition and provides it with one or more
     * grammar references for grammars to match against the input media.
     */
    RECOGNIZE               ("RECOGNIZE"),

    /**
     * Takes as input an interpret-text header, containing the text for which the semantic 
     * interpretation is desired, and returns, via the INTERPRETATION-
     * COMPLETE event, an interpretation result which is very similar to 
     * the one returned from a RECOGNIZE method invocation.
     */
    INTERPRET               ("INTERPRET"),

    /**
     * May be issued when the recognizer resource is in the recognized state, allows the client to
     * retrieve results for a completed recognition.
     */
    GET_RESULT              ("GET-RESULT"),

    /**
     * Should be sent from the client to the recognition resource, the recorder resource and/or the
     * verification resource when it knows that a kill-on-barge-in prompt has finished playing.
     */
    START_INPUT_TIMERS      ("START-INPUT-TIMERS"),

    //STOP                    ("STOP"),

/*
   enrollment-method    =  "START-PHRASE-ENROLLMENT" ; G
                        /  "ENROLLMENT-ROLLBACK"     ; H
                        /  "END-PHRASE-ENROLLMENT"   ; I
                        /  "MODIFY-PHRASE"           ; J
                        /  "DELETE-PHRASE"           ; K
*/

    /**
     * Starts a new phrase enrollment session during which the client may call
     * RECOGNIZE multiple times to enroll a new utterance in a grammar.
     */
    START_PHRASE_ENROLLMENT ("START-PHRASE-ENROLLMENT"),

    /**
     * Discards the last live utterance from the RECOGNIZE operation.
     */
    ENROLLMENT_ROLLBACK     ("ENROLLMENT-ROLLBACK"),

    /**
     * Directs the recognition resource to end an active phrase enrollment session.
     */
    END_PHRASE_ENROLLMENT   ("END-PHRASE-ENROLLMENT"),

    /**
     * Used to change the phrase ID, NL phrase and/or weight for a given phrase in a personal grammar.
     */
    MODIFY_PHRASE           ("MODIFY-PHRASE"),

    /**
     * Used to delete a phrase in a personal grammar added through voice enrollment or text enrollment.
     */
    DELETE_PHRASE           ("DELETE-PHRASE"),

/*
   recorder-Method      =  "RECORD"              ; A
                        /  "STOP"                ; B
                        /  "START-INPUT-TIMERS"  ; C
*/

    /**
     * Directs the recorder resource to start recording audio immediately or wait for the end pointing
     * functionality to detect speech in the audio, depending on the headers specified in the request.
     */
    RECORD                  ("RECORD"),

    //STOP                    ("STOP"),
    //START_INPUT_TIMERS      ("START-INPUT-TIMERS"),

/*
   verification-method      =  "START-SESSION"          ; A
                            / "END-SESSION"             ; B
                            / "QUERY-VOICEPRINT"        ; C
                            / "DELETE-VOICEPRINT"       ; D
                            / "VERIFY"                  ; E
                            / "VERIFY-FROM-BUFFER"      ; F
                            / "VERIFY-ROLLBACK"         ; G
                            / "STOP"                    ; H
                            / "CLEAR-BUFFER"            ; I
                            / "START-INPUT-TIMERS"      ; J
                            / "GET-INTERMEDIATE-RESULT" ; K
*/
    /**
     * Requests the verification resource to start a speaker verification or identification session.
     */
    START_SESSION           ("START-SESSION"),

    /**
     * Requests the verification resource to terminate an ongoing verification session and release the verification voiceprint resources.
     */
    END_SESSION             ("END-SESSION"),

    /**
     * Queries the verification resource for status information on a particular voice-print or voice-print repository.
     */
    QUERY_VOICEPRINT        ("QUERY-VOICEPRINT"),

    /**
     * Removes a voice-print from a voice-print repository.
     */
    DELETE_VOICEPRINT       ("DELETE-VOICEPRINT"),

    /**
     * Requests the verification resource to either train/adapt the voiceprint
     * or to verify/identify a claimed identity.
     */
    VERIFY                  ("VERIFY"),

    /**
     * Directs the verification resource to verify buffered audio against a voiceprint.
     */
    VERIFY_FROM_BUFFER      ("VERIFY-FROM-BUFFER"),

    /**
     * Directs the verification resource to discard the last buffered utterance or discard the last live utterances (when the mode is "train" or "verify").
     */
    VERIFY_ROLLBACK         ("VERIFY-ROLLBACK"),

    //STOP                    ("STOP"),

    /**
     * Directs the verification resource to clear the verification buffer.
     */
    CLEAR_BUFFER            ("CLEAR-BUFFER"),

    //START_INPUT_TIMERS      ("START-INPUT-TIMERS"),

    /**
     * Polls the verification resource for intermediate results of a verification request that is in progress.
     */
    GET_INTERMEDIATE_RESULT ("GET-INTERMEDIATE-RESULT");
    
    private String _name;

    private MrcpMethodName(String name) {
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
     * Converts an MRCP method in string format to the appropriate MRCP4J enum value.
     * @param str MRCP method string
     * @return the method name enum instance corresponding to the string value specified
     * @throws IllegalArgumentException if the string value specified does not correspond to
     * an existing MRCP method name
     */
    public static MrcpMethodName fromString(String str) throws IllegalArgumentException {
        for (MrcpMethodName value : MrcpMethodName.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid MRCP method-name: " + str);
    }

}