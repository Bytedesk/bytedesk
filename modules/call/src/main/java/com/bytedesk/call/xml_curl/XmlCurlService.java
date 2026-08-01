package com.bytedesk.call.xml_curl;

import java.util.Map;

public interface XmlCurlService {

    byte[] handleDialplan(Map<String, String> params);

    byte[] handleDirectory(Map<String, String> params);

    byte[] handleConfiguration(Map<String, String> params);

    byte[] handlePhrases(Map<String, String> params);

    byte[] resultNotFound();
}
