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

import java.util.EnumSet;

import com.bytedesk.call.mrcp4j.common.MrcpResourceType;

/**
 * Defines all valid MRCPv2 header names and provides factory methods for creating header value objects from header value strings.
 *
 * @author Niels Godfredsen {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">ngodfredsen@users.sourceforge.net</a>{@literal >}
 */
public enum MrcpHeaderName {

///////////////////////////////////////
// generic-headers
///////////////////////////////////////

    /**
     * com.bytedesk.call.mrcp4j.message.header.{@link com.bytedesk.call.mrcp4j.message.header.ChannelIdentifier}
     */
    CHANNEL_IDENTIFIER                  ("Channel-Identifier", new ChannelIdentifier.Factory()),

    /**
     * com.bytedesk.call.mrcp4j.message.header.{@link com.bytedesk.call.mrcp4j.message.header.RequestIdList}
     */
    ACTIVE_REQUEST_ID_LIST              ("Active-Request-Id-List", new RequestIdList.Factory()),

    PROXY_SYNC_ID                       ("Proxy-Sync-Id"),  // TODO: Fill in missing header value classes/factories for all header names...
    ACCEPT_CHARSET                      ("Accept-Charset"),
    CONTENT_ID                          ("Content-Id"),
    CONTENT_TYPE                        ("Content-Type"),

    /**
     * java.lang.{@link java.lang.Integer}
     */
    CONTENT_LENGTH                      ("Content-Length", new NonNegativeIntegerFactory()),

    CONTENT_BASE                        ("Content-Base" /*, AbsoluteURI.class*/),
    CONTENT_LOCATION                    ("Content-Location" /*, URIFactory.class -> AbsoluteURI/RelativeURI instance*/),
    CONTENT_ENCODING                    ("Content-Encoding" /*, TokenList.class*/),
    CACHE_CONTROL                       ("Cache-Control" /*, MapFactory.class or CacheDirectiveList */),
    LOGGING_TAG                         ("Logging-Tag"),
    SET_COOKIE                          ("Set-Cookie" /*, CookieList.class*/),
    SET_COOKIE2                         ("Set-Cookie2" /*, Cookie2List.class*/),
    VENDOR_SPECIFIC                     ("Vendor-Specific" /*, StringMapFactory.class*/),


///////////////////////////////////////
// synthesizer-headers
///////////////////////////////////////

    JUMP_SIZE                           ("Jump-Size"),

    /**
     * java.lang.{@link java.lang.Boolean}
     */
    KILL_ON_BARGE_IN                    ("Kill-On-Barge-In", Boolean.class),

    SPEAKER_PROFILE                     ("Speaker-Profile"),

    /**
     * com.bytedesk.call.mrcp4j.message.header.{@link com.bytedesk.call.mrcp4j.message.header.CompletionCause}
     */
    COMPLETION_CAUSE                    ("Completion-Cause", new CompletionCause.Factory()),

    COMPLETION_REASON                   ("Completion-Reason"),
    VOICE_PARAMETER                     ("Voice-Parameter"),
    PROSODY_PARAMETER                   ("Prosody-Parameter"),
    SPEECH_MARKER                       ("Speech-Marker"),
    SPEECH_LANGUAGE                     ("Speech-Language"),
    FETCH_HINT                          ("Fetch-Hint"),
    AUDIO_FETCH_HINT                    ("Audio-Fetch-Hint"),
    FETCH_TIMEOUT                       ("Fetch-Timeout"),
    FAILED_URI                          ("Failed-URI"),
    FAILED_URI_CAUSE                    ("Failed-URI-Cause"),
    SPEAK_RESTART                       ("Speak-Restart"),
    SPEAK_LENGTH                        ("Speak-Length"),
    LOAD_LEXICON                        ("Load-Lexicon"),
    LEXICON_SEARCH_ORDER                ("Lexicon-Search-Order"),


///////////////////////////////////////
// recog-only-headers
///////////////////////////////////////

    /**
     * java.lang.{@link java.lang.Float}
     */
    CONFIDENCE_THRESHOLD                ("Confidence-Threshold", new ZeroOneFloatFactory()),

    /**
     * java.lang.{@link java.lang.Float}
     */
    SENSITIVITY_LEVEL                   ("Sensitivity-Level", new ZeroOneFloatFactory()),

    /**
     * java.lang.{@link java.lang.Float}
     */
    SPEED_VS_ACCURACY                   ("Speed-Vs-Accuracy", new ZeroOneFloatFactory()),

    /**
     * java.lang.{@link java.lang.Integer}
     */
    N_BEST_LIST_LENGTH                  ("N-Best-List-Length", new PositiveIntegerFactory()),

    /**
     * java.lang.{@link java.lang.Long}
     */
    NO_INPUT_TIMEOUT                    ("No-Input-Timeout", new TimeoutFactory()),

    INPUT_TYPE                          ("Input-Type"),
    RECOGNITION_TIMEOUT                 ("Recognition-Timeout"),
    WAVEFORM_URI                        ("Waveform-URI"),
    INPUT_WAVEFORM_URI                  ("Input-waveform-URI"),
    //COMPLETION_CAUSE                    ("Completion-Cause"),
    //COMPLETION_REASON                   ("Completion-Reason"),
    RECOGNIZER_CONTEXT_BLOCK            ("Recognizer-Context-Block"),

    /**
     * java.lang.{@link java.lang.Boolean}
     */
    START_INPUT_TIMERS                  ("Start-Input-Timers", Boolean.class),



    /**
     * java.lang.{@link java.lang.Long}
     */
    SPEECH_COMPLETE_TIMEOUT             ("Speech-Complete-Timeout", Long.class),

    /**
     * java.lang.{@link java.lang.Long}
     */
    SPEECH_INCOMPLETE_TIMEOUT           ("Speech-Incomplete-Timeout", Long.class),

    DTMF_INTERDIGIT_TIMEOUT             ("DTMF-Interdigit-Timeout"),
    DTMF_TERM_TIMEOUT                   ("DTMF-Term-Timeout"),
    DTMF_TERM_CHAR                      ("DTMF-Term-Char"),
    //FETCH_TIMEOUT                       ("Fetch-Timeout"),
    //FAILED_URI                          ("Failed-URI"),
    //FAILED_URI_CAUSE                    ("Failed-URI-Cause"),

    /**
     * java.lang.{@link java.lang.Boolean}
     */
    SAVE_WAVEFORM                       ("Save-Waveform", Boolean.class),

    MEDIA_TYPE                          ("Media-Type"),
    NEW_AUDIO_CHANNEL                   ("New-Audio-Channel"),
    //SPEECH_LANGUAGE                     ("Speech-Language"),
    VER_BUFFER_UTTERANCE                ("Ver-Buffer-Utterance"),
    RECOGNITION_MODE                    ("Recognition-Mode"),
    CANCEL_IF_QUEUE                     ("Cancel-If-Queue", Boolean.class),
    HOTWORD_MAX_DURATION                ("Hotword-Max-Duration"),
    HOTWORD_MIN_DURATION                ("Hotword-Min-Duration"),
    INTERPRET_TEXT                      ("Interpret-Text"),
    ONE_OF_RULE_ID_URI                  ("One-Of-Rule-Id-URI"),


///////////////////////////////////////
// enrollment-headers
///////////////////////////////////////

    NUM_MIN_CONSISTENT_PRONUNCIATIONS   ("Num-Min-Vonsistent-Pronunciations"),

    /**
     * java.lang.{@link java.lang.Float}
     */
    CONSISTENCY_THRESHOLD               ("Consistency-Threshold", new ZeroOneFloatFactory()),

    /**
     * java.lang.{@link java.lang.Float}
     */
    CLASH_THRESHOLD                     ("Clash-Threshold", new ZeroOneFloatFactory()),

    PERSONAL_GRAMMAR_URI                ("Personal-Grammar-URI"),
    ENROLL_UTTERANCE                    ("Enroll-Utterance"),
    PHRASE_ID                           ("Phrase-Id"),
    PHRASE_NL                           ("Phrase-NL"),
    WEIGHT                              ("Weight"),
    SAVE_BEST_WAVEFORM                  ("Save-Best-Waveform"),
    NEW_PHRASE_ID                       ("New-Phrase-Id"),
    CONFUSABLE_PHRASES_URI              ("Confusable-Phrases-URI"),
    ABORT_PHRASE_ENROLLMENT             ("Abort-Phrase-Enrollment"),


///////////////////////////////////////
// recorder-headers
///////////////////////////////////////

    //SENSITIVITY_LEVEL                   ("Sensitivity-Level"),
    //NO_INPUT_TIMEOUT                    ("No-Input-Timeout"),
    //COMPLETION_CAUSE                    ("Completion-Cause"),
    //COMPLETION_REASON                   ("Completion-Reason"),
    //FAILED_URI                          ("Failed-URI"),
    //FAILED_URI_CAUSE                    ("Failed-URI-Cause"),
    RECORD_URI                          ("Record-URI"),
    //MEDIA_TYPE                          ("Media-Type"),
    MAX_TIME                            ("Max-Time"),
    TRIM_LENGTH                         ("Trim-Length"),
    FINAL_SILENCE                       ("Final-Silence"),
    CAPTURE_ON_SPEECH                   ("Capture-On-Speech"),
    //VER_BUFFER_UTTERANCE                ("Ver-Buffer-Utterance"),
    //START_INPUT_TIMERS                  ("Start-Input-Timers"),
    //NEW_AUDIO_CHANNEL                   ("New-Audio-Channel"),


///////////////////////////////////////
// verification-headers
///////////////////////////////////////

    REPOSITORY_URI                      ("Repository-URI"),
    VOICEPRINT_IDENTIFIER               ("Voiceprint-Identifier"),
    VERIFICATION_MODE                   ("Verification-Mode"),
    ADAPT_MODEL                         ("Adapt-Model"),
    ABORT_MODEL                         ("Abort-Model"),

    /**
     * java.lang.{@link java.lang.Float}
     */
    MIN_VERIFICATION_SCORE              ("min-verification-score", new MinusOnePlusOneFloatFactory()),

    NUM_MIN_VERIFICATION_PHRASES        ("Num-Min-Verification-Phrases"),
    NUM_MAX_VERIFICATION_PHRASES        ("Num-Max-Verification-Phrases"),
    //NO_INPUT_TIMEOUT                    ("No-Input-Timeout"),
    //SAVE_WAVEFORM                       ("Save-Waveform"),
    //MEDIA_TYPE                          ("Media-Type"),
    //WAVEFORM_URI                        ("Waveform-URI"),
    VOICEPRINT_EXISTS                   ("Voiceprint-Exists"),
    //VER_BUFFER_UTTERANCE                ("Ver-Buffer-Utterance"),
    //INPUT_WAVEFORM_URI                  ("Input-Waveform-URI"),
    //COMPLETION_CAUSE                    ("Completion-Cause"),
    //COMPLETION_REASON                   ("Completion-Reason"),
    //SPEECH_COMPLETE_TIMEOUT             ("Speech-Complete-Timeout"),
    //NEW_AUDIO_CHANNEL                   ("New-Audio-Channel"),
    ABORT_VERIFICATION                  ("Abort-Verification");
    //START_INPUT_TIMERS                  ("Start-Input-Timers");    

    public static EnumSet<MrcpHeaderName> GENERIC_HEADER_NAMES = EnumSet.range(CHANNEL_IDENTIFIER, VENDOR_SPECIFIC);

    public static EnumSet<MrcpHeaderName> SYNTHESIZER_HEADER_NAMES = EnumSet.range(JUMP_SIZE, LEXICON_SEARCH_ORDER);

    public static EnumSet<MrcpHeaderName> RECOG_ONLY_HEADER_NAMES = EnumSet.of(
            CONFIDENCE_THRESHOLD, SENSITIVITY_LEVEL, SPEED_VS_ACCURACY, N_BEST_LIST_LENGTH, NO_INPUT_TIMEOUT,
            INPUT_TYPE, RECOGNITION_TIMEOUT, WAVEFORM_URI, INPUT_WAVEFORM_URI, COMPLETION_CAUSE, COMPLETION_REASON,
            RECOGNIZER_CONTEXT_BLOCK, START_INPUT_TIMERS, SPEECH_COMPLETE_TIMEOUT, SPEECH_INCOMPLETE_TIMEOUT,
            DTMF_INTERDIGIT_TIMEOUT, DTMF_TERM_TIMEOUT, DTMF_TERM_CHAR, FETCH_TIMEOUT, FAILED_URI, FAILED_URI_CAUSE,
            SAVE_WAVEFORM, MEDIA_TYPE, NEW_AUDIO_CHANNEL, SPEECH_LANGUAGE, VER_BUFFER_UTTERANCE, RECOGNITION_MODE,
            CANCEL_IF_QUEUE, HOTWORD_MAX_DURATION, HOTWORD_MIN_DURATION, INTERPRET_TEXT, ONE_OF_RULE_ID_URI
            
    );

    public static EnumSet<MrcpHeaderName> ENROLLMENT_HEADER_NAMES = EnumSet.range(NUM_MIN_CONSISTENT_PRONUNCIATIONS, ABORT_PHRASE_ENROLLMENT);

    public static EnumSet<MrcpHeaderName> RECORDER_HEADER_NAMES = EnumSet.of(
            SENSITIVITY_LEVEL, NO_INPUT_TIMEOUT, COMPLETION_CAUSE, COMPLETION_REASON, FAILED_URI, FAILED_URI_CAUSE,
            RECORD_URI, MEDIA_TYPE, MAX_TIME, TRIM_LENGTH, FINAL_SILENCE, CAPTURE_ON_SPEECH, VER_BUFFER_UTTERANCE,
            START_INPUT_TIMERS, NEW_AUDIO_CHANNEL
    );

    public static EnumSet<MrcpHeaderName> VERIFICATION_HEADER_NAMES = EnumSet.of(
            REPOSITORY_URI, VOICEPRINT_IDENTIFIER, VERIFICATION_MODE, ADAPT_MODEL, ABORT_MODEL, CONFIDENCE_THRESHOLD,
            NUM_MIN_VERIFICATION_PHRASES, NUM_MAX_VERIFICATION_PHRASES, NO_INPUT_TIMEOUT, SAVE_WAVEFORM, MEDIA_TYPE,
            WAVEFORM_URI, VOICEPRINT_EXISTS, VER_BUFFER_UTTERANCE, INPUT_WAVEFORM_URI, COMPLETION_CAUSE, COMPLETION_REASON,
            SPEECH_COMPLETE_TIMEOUT, NEW_AUDIO_CHANNEL, ABORT_VERIFICATION, START_INPUT_TIMERS
    );

    private String _name;
    private ValueFactory _valueFactory;

    private MrcpHeaderName(String name) {
        // TODO: change to specific StringValueFactory?
        this(name, new GenericValueFactory(String.class));
    }

    private MrcpHeaderName(String name, Class<?> valueClass) {
        this(name, new GenericValueFactory(valueClass));
    }

    private MrcpHeaderName(String name, ValueFactory valueFactory) {
        _name = name;
        _valueFactory = valueFactory;
    }
    
    /**
     * Tests whether an MRCP header field-name in string format matches this enum instance.
     * @param name MRCP header field-name.
     * @return true if the specified field-name matches this enum instance.
     */
    public boolean isMatch(String name) {
        return _name.equalsIgnoreCase(name);
    }

    /**
     * Tests whether this instance represents a header name corresponding to a generic-header.
     * @return true if this header name corresponds to a generic-header.
     */
    public boolean isGeneric() {
        return GENERIC_HEADER_NAMES.contains(this);
    }

    /**
     * Tests whether this header name is applicable to a specified MRCP resource type.  Note that some resources may not support TODOC...
     * @param type the MRCP resource type to be tested against.
     * @return true if this header name corresponds to the specified resource type.
     */
    public boolean isApplicableTo(MrcpResourceType type) {
        if (isGeneric()) {
            return true;
        }

        switch (type) {
        case DTMFRECOG:
            return (RECOG_ONLY_HEADER_NAMES.contains(this));

        case SPEECHRECOG:
            return (RECOG_ONLY_HEADER_NAMES.contains(this) || ENROLLMENT_HEADER_NAMES.contains(this));

        case SPEECHSYNTH:
        case BASICSYNTH:
            return SYNTHESIZER_HEADER_NAMES.contains(this);

        case RECORDER:
            return RECORDER_HEADER_NAMES.contains(this);

        case SPEAKVERIFY:
            return VERIFICATION_HEADER_NAMES.contains(this);

        default:
            throw new AssertionError(type);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _name;
    }

    /**
     * Converts an MRCP header field-name in string format to the appropriate MRCP4J enum value.
     * @param str MRCP header field-name
     * @return the header name enum instance corresponding to the string value specified
     * @throws IllegalArgumentException if the string value specified does not correspond to
     * an existing MRCP header field-name
     */
    public static MrcpHeaderName fromString(String str) throws IllegalArgumentException {
        for (MrcpHeaderName value : MrcpHeaderName.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid MRCP header field-name: " + str);
    }
    
    public MrcpHeader constructHeader(Object valueObject) throws ClassCastException, IllegalArgumentException {
        String valueString = _valueFactory.toValueString(valueObject);
        return new MrcpHeader(this, valueString, valueObject);
    }

    /**
     * Creates the value object applicable to this {@code MrcpHeaderName} from a value string. 
     * @param valueString the string value for the value object instance. 
     * @return value object for the header using the {@code MrcpHeaderName} and supplied value string.
     * @throws IllegalValueException if the value string passed is not valid for the header type.
     */
    public Object createHeaderValue(String valueString) throws IllegalValueException {
        valueString = (valueString == null) ? "" : valueString.trim();
        return _valueFactory.fromValueString(valueString);
    }

    /**
     * Creates a new {@code MrcpHeader} instance based on this {@code MrcpHeaderName}. 
     * @param valueString the string value for the new {@code MrcpHeader} instance. 
     * @return a new instance using the {@code MrcpHeaderName} and supplied value.
     */
    public MrcpHeader createHeader(String valueString) {
        try {
            return new MrcpHeader(this, valueString, createHeaderValue(valueString));
        } catch (IllegalValueException e) {
            return new MrcpHeader(this, valueString, e);
        }
    }

    /**
     * Factory method to create new {@code MrcpHeader} instances.
     * @param  name field-name of the header.
     * @param  valueString field-value of the header.
     * @return a new instance of {@code MrcpHeaderName} using the supplied field-name and field-value.
     */
    public static MrcpHeader createHeader(String name, String valueString) {
        MrcpHeaderName headerName = null;
        try {
            headerName = fromString(name);
        } catch (IllegalArgumentException e) {
            return new VendorSpecificHeader(name, (valueString == null) ? "" : valueString.trim());
        }
        return headerName.createHeader(valueString);
    }

    /**
     * TODOC
     */
    private static class NonNegativeIntegerFactory extends GenericValueFactory {

        private static final Integer VALUE_MIN = Integer.valueOf(0);

        protected NonNegativeIntegerFactory() {
            super(Integer.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.GenericValueFactory#validateObject(java.lang.Object)
         */
        @Override
        protected void validateObject(Object valueObject) throws IllegalValueException {
            Integer value = (Integer) valueObject;
            if (value.compareTo(VALUE_MIN) < 0) {
                throw new IllegalValueException("Illegal non-negative-integer value: " + valueObject);
            }
        }

    }

    /**
     * TODOC
     */
    private static class PositiveIntegerFactory extends GenericValueFactory {

        private static final Integer VALUE_MIN = Integer.valueOf(1);

        protected PositiveIntegerFactory() {
            super(Integer.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.GenericValueFactory#validateObject(java.lang.Object)
         */
        @Override
        protected void validateObject(Object valueObject) throws IllegalValueException {
            Integer value = (Integer) valueObject;
            if (value.compareTo(VALUE_MIN) < 0) {
                throw new IllegalValueException("Illegal positive-integer value: " + valueObject);
            }
        }

    }

    /**
     * TODOC
     */
    private static class ZeroOneFloatFactory extends GenericValueFactory {

        private static final Float VALUE_MIN = Float.valueOf(0.0f);
        private static final Float VALUE_MAX = Float.valueOf(1.0f);

        protected ZeroOneFloatFactory() {
            super(Float.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.GenericValueFactory#validateObject(java.lang.Object)
         */
        @Override
        protected void validateObject(Object valueObject) throws IllegalValueException {
            Float value = (Float) valueObject;
            if (value.compareTo(VALUE_MIN) < 0 || value.compareTo(VALUE_MAX) > 0) {
                throw new IllegalValueException("Illegal zero-one-float value: " + valueObject);
            }
        }

    }

    /**
     * TODOC
     */
    private static class MinusOnePlusOneFloatFactory extends GenericValueFactory {

        private static final Float VALUE_MIN = Float.valueOf(-1.0f);
        private static final Float VALUE_MAX = Float.valueOf(1.0f);

        protected MinusOnePlusOneFloatFactory() {
            super(Float.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.GenericValueFactory#validateObject(java.lang.Object)
         */
        @Override
        protected void validateObject(Object valueObject) throws IllegalValueException {
            Float value = (Float) valueObject;
            if (value.compareTo(VALUE_MIN) < 0 || value.compareTo(VALUE_MAX) > 0) {
                throw new IllegalValueException("Illegal minus_one-plus_one-float value: " + valueObject);
            }
        }

    }

    /**
     * TODOC
     */
    private static class TimeoutFactory extends GenericValueFactory {

        private static final Long VALUE_MIN = Long.valueOf(0L);
        private static final Long VALUE_MAX = Long.valueOf(10L * 60L * 1000L);  // arbitrary maximum but ten minutes should be sufficient!

        protected TimeoutFactory() {
            super(Long.class);
        }

        /* (non-Javadoc)
         * @see com.bytedesk.call.mrcp4j.message.header.GenericValueFactory#validateObject(java.lang.Object)
         */
        @Override
        protected void validateObject(Object valueObject) throws IllegalValueException {
            Long value = (Long) valueObject;
            if (value.compareTo(VALUE_MIN) < 0 || value.compareTo(VALUE_MAX) > 0) {
                throw new IllegalValueException("Illegal value for timeout: " + valueObject);
            }
        }

    }

}
