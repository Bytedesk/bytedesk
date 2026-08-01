package com.bytedesk.call.xml_curl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bytedesk.call.xml_curl.impl.DefaultXmlCurlService;

@SpringBootTest(classes = XmlCurlDefaultConfig.class)
class XmlCurlDefaultConfigTest {

    @Autowired
    private XmlCurlService xmlCurlService;

    @Test
    void registersDefaultXmlCurlServiceBean() {
        assertThat(xmlCurlService).isInstanceOf(DefaultXmlCurlService.class);
    }
}