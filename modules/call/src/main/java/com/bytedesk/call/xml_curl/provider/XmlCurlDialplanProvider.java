package com.bytedesk.call.xml_curl.provider;

import java.util.Map;
import java.util.Optional;

/**
 * Pluggable provider used by xml_curl dialplan section.
 */
public interface XmlCurlDialplanProvider {

    /**
     * Build a full FreeSWITCH dialplan XML document for a destination lookup.
     * Return empty when the request cannot be resolved by this provider.
     */
    Optional<String> provideDialplanXml(String context, String destinationNumber, Map<String, String> params);
}