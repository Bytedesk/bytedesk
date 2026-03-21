package com.bytedesk.call.esl_event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.bytedesk.call.esl.client.transport.event.EslEvent;
import com.bytedesk.core.uid.UidUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class EslEventIngestServiceTest {

    @Mock
    private EslEventRepository eslEventRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private EslEvent eslEvent;

    @Test
    void retriesWithDatabaseSafePayloadLengthWhenCurrentSchemaIsTooSmall() {
        EslEventIngestProperties properties = new EslEventIngestProperties();
        properties.setEnabled(true);
        properties.setSampleRate(1.0d);
        properties.setMaxPayloadLength(20000);
        when(uidUtils.getUid()).thenReturn("uid-1");
        when(eslEvent.getEventName()).thenReturn("API");
        when(eslEvent.getEventSubclass()).thenReturn(null);
        when(eslEvent.getEventHeaders()).thenReturn(Map.of(
                "Event-Name", "API",
                "API-Command", "sofia",
                "API-Command-Argument", "profile external rescan",
                "Event-Calling-Function", "switch_api_execute",
                "payload", "x".repeat(600)));
        when(eslEvent.getEventBodyLines()).thenReturn(List.of("line-1", "line-2", "x".repeat(600)));
        when(eslEventRepository.save(any(EslEventEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Data truncation: Data too long for column 'headers_json' at row 1"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EslEventIngestService service = new EslEventIngestService(eslEventRepository, uidUtils, new ObjectMapper(), properties);

        service.ingest(eslEvent);

        ArgumentCaptor<EslEventEntity> captor = ArgumentCaptor.forClass(EslEventEntity.class);
        verify(eslEventRepository, times(2)).save(captor.capture());
        EslEventEntity retriedEntity = captor.getAllValues().get(1);
        assertTrue(retriedEntity.getHeadersJson().length() <= 255);
        assertTrue(retriedEntity.getBodyJson().length() <= 255);
        assertEquals("sofia", retriedEntity.getApiCommand());
    }
}