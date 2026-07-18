package com.bytedesk.call.mrcp4j.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bytedesk.call.mrcp4j.common.MrcpEventName;
import com.bytedesk.call.mrcp4j.common.MrcpRequestState;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.junit.jupiter.api.Test;

class MrcpMessageEncoderTest {

    @Test
    void encodesUnicodeContentAsUtf8AndUsesByteContentLength() throws Exception {
        String nlsml = "<?xml version=\"1.0\"?><result><interpretation><input mode=\"speech\">你好</input></interpretation></result>";
        MrcpEvent event = new MrcpEvent();
        event.setVersion(MrcpMessage.MRCP_VERSION_2_0);
        event.setEventName(MrcpEventName.RECOGNITION_COMPLETE);
        event.setRequestID(1);
        event.setRequestState(MrcpRequestState.COMPLETE);
        event.setContent("application/nlsml+xml", null, nlsml);

        CapturingEncoderOutput output = new CapturingEncoderOutput();
        new MrcpMessageEncoder().encode(null, event, output);

        IoBuffer buffer = (IoBuffer) output.messages().get(0);
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String encoded = new String(bytes, StandardCharsets.UTF_8);

        assertTrue(encoded.contains("你好"));
        assertTrue(encoded.contains("Content-Type:application/nlsml+xml"));
        assertTrue(encoded.contains("Content-Length:" + event.getContent().getBytes(StandardCharsets.UTF_8).length));
        assertEquals(bytes.length, event.getMessageLength());
    }

    private static final class CapturingEncoderOutput implements ProtocolEncoderOutput {

        private final List<Object> messages = new ArrayList<>();

        @Override
        public void write(Object encodedMessage) {
            messages.add(encodedMessage);
        }

        private List<Object> messages() {
            return messages;
        }
    }
}