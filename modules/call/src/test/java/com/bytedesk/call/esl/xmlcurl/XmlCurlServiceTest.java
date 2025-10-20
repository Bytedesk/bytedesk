package com.bytedesk.call.esl.xmlcurl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XmlCurlServiceTest {

    private final XmlCurlService service = new XmlCurlService();

    @Test
    void buildDirectoryUser_basic() {
        String xml = service.buildDirectoryUser("default", "1000");
        assertTrue(xml.contains("<section name=\"directory\">"));
        assertTrue(xml.contains("<user id=\"1000\">"));
        assertTrue(xml.contains("<param name=\"password\""));
    }

    @Test
    void buildDialplan_basic() {
        String xml = service.buildDialplan("default", "1000");
        assertTrue(xml.contains("<section name=\"dialplan\""));
        assertTrue(xml.contains("destination_number\" expression=\"^1000$"));
    }

    @Test
    void buildError_and_notFound() {
        String err = service.buildError("unauthorized", "invalid token");
        assertTrue(err.contains("result"));
        String nf = service.buildNotFound();
        assertTrue(nf.contains("not_found"));
    }
}
