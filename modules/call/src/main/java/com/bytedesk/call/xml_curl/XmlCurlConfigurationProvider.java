package com.bytedesk.call.xml_curl;

import java.util.Map;
import java.util.Optional;

/**
 * Pluggable provider used by xml_curl configuration section.
 */
public interface XmlCurlConfigurationProvider {

    /**
     * Build a full FreeSWITCH configuration XML document for a configuration lookup.
     * Return empty when the configuration name is unsupported.
     */
    Optional<String> provideConfigurationXml(String configurationName, Map<String, String> params);
}