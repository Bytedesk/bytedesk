package com.bytedesk.call.httapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}