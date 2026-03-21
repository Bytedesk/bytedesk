package com.bytedesk.call.esl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.call.config.CallEventListener;
import com.bytedesk.call.config.CallFreeswitchProperties;
import com.bytedesk.call.esl.client.inbound.Client;
import com.bytedesk.call.esl.client.transport.message.EslHeaders;
import com.bytedesk.call.esl.client.transport.message.EslMessage;

@ExtendWith(MockitoExtension.class)
class EslServiceTest {

    @Mock
    private Client eslClient;

    @Mock
    private CallFreeswitchProperties callFreeswitchProperties;

    @Mock
    private CallEventListener callEventListener;

    @Test
    void xmlFlushCacheUsesNativeApiCommand() {
        when(eslClient.canSend()).thenReturn(true);
        when(eslClient.sendApiCommand("xml_flush_cache", "")).thenReturn(apiResponse("+OK cache flushed"));

        EslService eslService = new EslService(eslClient, callFreeswitchProperties, callEventListener);

        Map<String, Object> result = eslService.xmlFlushCache();

        verify(eslClient).sendApiCommand("xml_flush_cache", "");
        assertEquals(Boolean.TRUE, result.get("ok"));
    }

    @Test
    void apiResponseTreatsLaterOkBodyLineAsSuccess() {
        when(eslClient.canSend()).thenReturn(true);
        when(eslClient.sendApiCommand("sofia", "profile external rescan")).thenReturn(apiResponse(
                "Reload XML [Success]",
                "+OK scan complete"));

        EslService eslService = new EslService(eslClient, callFreeswitchProperties, callEventListener);

        Map<String, Object> result = eslService.sofiaProfileAction("external", "rescan");

        assertTrue(Boolean.TRUE.equals(result.get("ok")));
    }

    private EslMessage apiResponse(String... bodyLines) {
        EslMessage message = new EslMessage();
        message.getHeaders().put(EslHeaders.Name.CONTENT_TYPE, EslHeaders.Value.API_RESPONSE);
        for (String bodyLine : bodyLines) {
            message.getBodyLines().add(bodyLine);
        }
        return message;
    }
}