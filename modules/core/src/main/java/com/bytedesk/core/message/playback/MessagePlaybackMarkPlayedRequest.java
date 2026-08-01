package com.bytedesk.core.message.playback;

import lombok.Data;

@Data
public class MessagePlaybackMarkPlayedRequest {

    private String messageUid;

    private Boolean played;
}
