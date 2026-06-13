package com.bytedesk.call.xml_curl.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import com.bytedesk.call.xml_curl.XmlCurlService;

@Service
@ConditionalOnMissingBean(XmlCurlService.class)
public class DefaultXmlCurlService implements XmlCurlService {

    @Override
    public byte[] handleDialplan(Map<String, String> params) {
        return resultNotFound();
    }

    @Override
    public byte[] handleDirectory(Map<String, String> params) {
        return resultNotFound();
    }

    @Override
    public byte[] handleConfiguration(Map<String, String> params) {
        return resultNotFound();
    }

    @Override
    public byte[] handlePhrases(Map<String, String> params) {
        return resultNotFound();
    }

    @Override
    public byte[] resultNotFound() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<document type=\"freeswitch/xml\">\n"
                + "  <section name=\"result\">\n"
                + "    <result status=\"not found\"/>\n"
                + "  </section>\n"
                + "</document>\n";
        return xml.getBytes(StandardCharsets.UTF_8);
    }
}