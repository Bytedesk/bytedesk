package com.bytedesk.call.xmlcurl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DialplanOptions {
    // When set, will bridge to this endpoint instead of default demo actions
    private String bridgeEndpoint; // e.g., sofia/gateway/mygw/${destination_number}

    // Play a file (absolute path or built-in prompt) before bridging/hanging up
    private String playbackFile; // e.g., /usr/local/freeswitch/sounds/en/us/callie/ivr/8000/ivr-welcome.wav

    // Use TTS before action
    private String ttsEngine; // e.g., cepstral or mod_tts_commandline
    private String ttsText;

    // Sleep milliseconds between actions
    private Integer sleepMs;

    // If true, do not auto answer
    private Boolean noAnswer;

    // Start an IVR menu
    private String ivrMenu; // e.g., main_menu

    // Route to callcenter queue
    private String queueName; // e.g., support_queue

    // Record the session to a file before other actions
    private String recordFile; // e.g., /var/recordings/${uuid}.wav
}
