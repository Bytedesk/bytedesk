package com.bytedesk.core.message.playback;

import org.springframework.util.StringUtils;

import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.AudioContent;
import com.bytedesk.core.message.content.VoiceContent;

public class MessagePlaybackJsonHelper {

    private MessagePlaybackJsonHelper() {
    }

    public static String markPlayed(String content, String messageType, boolean played) {
        MessageTypeEnum type = MessageTypeEnum.fromValue(messageType);

        if (type == MessageTypeEnum.VOICE) {
            VoiceContent voiceContent = VoiceContent.fromJson(content);
            if (voiceContent == null) {
                voiceContent = VoiceContent.builder()
                        .url(StringUtils.hasText(content) ? content : "")
                        .build();
            }
            voiceContent.setPlayed(played);
            return voiceContent.toJson();
        }

        if (type == MessageTypeEnum.AUDIO) {
            AudioContent audioContent = AudioContent.fromJson(content);
            if (audioContent == null) {
                audioContent = AudioContent.builder()
                        .url(StringUtils.hasText(content) ? content : "")
                        .build();
            }
            audioContent.setPlayed(played);
            return audioContent.toJson();
        }

        return content;
    }
}
