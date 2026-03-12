package com.bytedesk.call.xml_curl;

import java.util.Map;
import java.util.Optional;

/**
 * Pluggable provider used by xml_curl directory section.
 */
public interface XmlCurlDirectoryProvider {

    /**
     * Build a full FreeSWITCH directory XML document for a user lookup.
     * Return empty when user/domain cannot be resolved.
     */
    Optional<String> provideDirectoryXml(String user, String domain, Map<String, String> params);
}
