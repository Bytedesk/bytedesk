package com.bytedesk.call.xmlcurl;

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

    @Test
    void buildDirectoryUser_with_options() {
        DirectoryOptions opt = DirectoryOptions.builder()
                .password("pwd")
                .callerIdName("Alice")
                .callerIdNumber("2000")
                .userContext("internal")
                .build();
        String xml = service.buildDirectoryUser("corp", "2000", opt);
        assertTrue(xml.contains("corp"));
        assertTrue(xml.contains("Alice"));
        assertTrue(xml.contains("user_context\" value=\"internal"));
    }

    @Test
    void buildDialplan_with_options() {
        DialplanOptions opt = DialplanOptions.builder()
                .noAnswer(true)
                .sleepMs(100)
                .playbackFile("ivr/ivr-welcome.wav")
                .bridgeEndpoint("sofia/gateway/gw/${destination_number}")
                .build();
        String xml = service.buildDialplan("default", "1000", opt);
        assertTrue(xml.contains("playback"));
        assertTrue(xml.contains("bridge"));
    }

    @Test
    void buildPhrases_and_configuration() {
        String p = service.buildPhrases("en");
        assertTrue(p.contains("phrases"));
        String c = service.buildConfiguration("sofia");
        assertTrue(c.contains("configuration"));
    }
}
