package com.bytedesk.call.xml_curl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bytedesk.call.xml_curl.impl.DefaultXmlCurlService;

class XmlCurlServiceTest {

    private final XmlCurlService service = new DefaultXmlCurlService();

    @Test
    void defaultImplementationShouldAlwaysReturnNotFound() {
        byte[] dialplan = service.handleDialplan(Map.of());
        byte[] directory = service.handleDirectory(Map.of());
        byte[] configuration = service.handleConfiguration(Map.of());
        byte[] phrases = service.handlePhrases(Map.of());

        assertNotFound(dialplan);
        assertNotFound(directory);
        assertNotFound(configuration);
        assertNotFound(phrases);
    }

    private void assertNotFound(byte[] result) {
        String xml = new String(result, StandardCharsets.UTF_8);
        assertTrue(xml.contains("status=\"not found\""));
    }
}