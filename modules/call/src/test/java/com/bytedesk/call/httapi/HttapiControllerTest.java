package com.bytedesk.call.httapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.LinkedMultiValueMap;

@ExtendWith(MockitoExtension.class)
class HttapiControllerTest {

    @Mock
    private LlmClient llmClient;

    @Mock
    private VoiceAgentHttpClient voiceAgentHttpClient;

    @Test
    void requiresShoutPlaybackForMp3Only() {
        assertTrue(HttapiController.requiresShoutPlayback("https://example.com/audio.mp3"));
        assertFalse(HttapiController.requiresShoutPlayback("https://example.com/audio.wav"));
        assertFalse(HttapiController.requiresShoutPlayback("https://example.com/audio.WAV"));
    }

    @Test
    void firstTurnShouldUseInjectedMrcpProfileResolver() {
        HttapiController controller = new HttapiController(
                llmClient,
                voiceAgentHttpClient,
                vars -> "java-mrcp");

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("turn", "1");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/ai-bot");

        String xml = new String(controller.aiBot(form, request), StandardCharsets.UTF_8);

        assertTrue(xml.contains("application=\"set\" data=\"tts_profile=java-mrcp\""));
        assertTrue(xml.contains("application=\"set\" data=\"unimrcp:profile=java-mrcp\""));
    }

    @Test
    void secondTurnShouldUseInjectedMrcpProfileResolverForRetryPrompt() {
        HttapiController controller = new HttapiController(
                llmClient,
                voiceAgentHttpClient,
                vars -> "java-mrcp");

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("turn", "2");
        form.add("mode", "single");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/ai-bot");

        String xml = new String(controller.aiBot(form, request), StandardCharsets.UTF_8);

        assertTrue(xml.contains("application=\"set\" data=\"unimrcp:profile=java-mrcp\""));
        assertTrue(xml.contains("若未识别任何内容，请靠近话筒再试"));
    }

    @Test
    void defaultResolverShouldPreferExplicitMrcpProfile() {
        HttapiMrcpProfileResolver resolver = new HttapiDefaultConfig().httapiMrcpProfileResolver();

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("mrcp_profile", "java-mrcp");

        assertTrue("java-mrcp".equals(resolver.resolveProfile(form.toSingleValueMap())));
    }

        @Test
        void secondTurnShouldExportAcdRouteVariablesFor9205RealtimeLoop() {
        HttapiController controller = new HttapiController(
            llmClient,
            voiceAgentHttpClient,
            vars -> "java-mrcp");

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("turn", "2");
        form.add("mode", "unlimited");
        form.add("voice_agent", "true");
        form.add("org_uid", "org-1");
        form.add("bot_did", "9205");
        form.add("voice_agent_provider", "qwen-audio-realtime");
        form.add("conversation_id", "uuid-1");
        form.add("file_url", "https://cdn.example.com/call.wav");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/ai-bot");
        request.setScheme("http");
        request.setServerName("api.weiyuai.cn");
        request.setServerPort(9003);

        when(voiceAgentHttpClient.chat(
            "http://api.weiyuai.cn:9003",
            "https://cdn.example.com/call.wav",
            "uuid-1",
            null,
            "org-1",
            "9205",
            "qwen-audio-realtime",
            null,
            null,
            null,
            null,
            null))
                .thenReturn(new VoiceAgentHttpClient.VoiceAgentChatResult(
                    "uuid-1",
                    "我要转人工",
                    "正在为您转接人工坐席",
                    "https://cdn.example.com/reply.wav",
                    "ACD_ENQUEUE",
                    "support",
                    "queue-1",
                    null,
                    "正在为您转接人工坐席",
                    null,
                    20));

        String xml = new String(controller.aiBot(form, request), StandardCharsets.UTF_8);

        assertTrue(xml.contains("application=\"export\" data=\"bot_user_text=我要转人工\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_reply_text=正在为您转接人工坐席\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_route=ACD_ENQUEUE\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_queue_name=support\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_queue_uid=queue-1\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_ring_timeout_seconds=20\""));
        assertTrue(xml.contains("application=\"playback\" data=\"https://cdn.example.com/reply.wav\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_continue=1\""));
        }

        @Test
        void secondTurnShouldExportLeaveMessageVariablesFor9205RealtimeLoop() {
        HttapiController controller = new HttapiController(
            llmClient,
            voiceAgentHttpClient,
            vars -> "java-mrcp");

        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("turn", "2");
        form.add("mode", "unlimited");
        form.add("voice_agent", "true");
        form.add("org_uid", "org-1");
        form.add("bot_did", "9205");
        form.add("voice_agent_provider", "qwen-audio-realtime");
        form.add("conversation_id", "uuid-2");
        form.add("file_url", "https://cdn.example.com/call-2.wav");

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/ai-bot");
        request.setScheme("http");
        request.setServerName("api.weiyuai.cn");
        request.setServerPort(9003);

        when(voiceAgentHttpClient.chat(
            "http://api.weiyuai.cn:9003",
            "https://cdn.example.com/call-2.wav",
            "uuid-2",
            null,
            "org-1",
            "9205",
            "qwen-audio-realtime",
            null,
            null,
            null,
            null,
            null))
                .thenReturn(new VoiceAgentHttpClient.VoiceAgentChatResult(
                    "uuid-2",
                    "现在转人工",
                    "当前非服务时间，请在提示音后留言。",
                    "https://cdn.example.com/voicemail.wav",
                    "LEAVE_MESSAGE",
                    "default",
                    "queue-2",
                    "OFF_HOURS",
                    "当前非服务时间，请在提示音后留言。",
                    90,
                    null));

        String xml = new String(controller.aiBot(form, request), StandardCharsets.UTF_8);

        assertTrue(xml.contains("application=\"export\" data=\"bot_route=LEAVE_MESSAGE\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_queue_name=default\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_queue_uid=queue-2\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_leave_reason=OFF_HOURS\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_leave_max_record_seconds=90\""));
        assertTrue(xml.contains("application=\"playback\" data=\"https://cdn.example.com/voicemail.wav\""));
        assertTrue(xml.contains("application=\"export\" data=\"bot_continue=1\""));
        }
}