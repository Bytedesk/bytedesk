package com.bytedesk.call.httapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HttapiControllerTest {

    @Test
    void requiresShoutPlaybackForMp3Only() {
        assertTrue(HttapiController.requiresShoutPlayback("https://example.com/audio.mp3"));
        assertFalse(HttapiController.requiresShoutPlayback("https://example.com/audio.wav"));
        assertFalse(HttapiController.requiresShoutPlayback("https://example.com/audio.WAV"));
    }
}