package com.bytedesk.call.httapi;

import java.util.Map;

/**
 * Resolves the MRCP profile used by HTTAPI voice flows.
 */
public interface HttapiMrcpProfileResolver {

    String resolveProfile(Map<String, String> vars);
}