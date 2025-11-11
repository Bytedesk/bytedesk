/*
 * @Description: Resolve presence TTL with defaults and safe bounds
 */
package com.bytedesk.core.socket.connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PresenceTtlResolver {

    @Value("${bytedesk.presence.ttl.default:90}")
    private int defaultTtl;

    @Value("${bytedesk.presence.ttl.min:60}")
    private int minTtl;

    @Value("${bytedesk.presence.ttl.max:180}")
    private int maxTtl;

    @Value("${bytedesk.presence.ttl.mqtt:90}")
    private int mqttTtl;

    /**
     * Resolve final TTL seconds based on protocol and optional requested value.
     * If requested is out of bounds, it will be rejected and base TTL will be used (with warning logged).
     */
    public int resolve(final String protocol, final Integer requested) {
        int base = switch (protocol == null ? "" : protocol.toUpperCase()) {
            case "MQTT" -> mqttTtl > 0 ? mqttTtl : defaultTtl;
            default -> defaultTtl;
        };
        // clamp base into range
        base = clamp(base);

        if (requested == null) {
            return base;
        }
        if (requested >= minTtl && requested <= maxTtl) {
            return requested;
        }
        // Out of range -> reject and audit via warning
        log.warn("presence ttl requested out of range: {} not in [{},{}], use base {} (protocol={})",
                requested, minTtl, maxTtl, base, protocol);
        return base;
    }

    private int clamp(int value) {
        if (value < minTtl) return minTtl;
        if (value > maxTtl) return maxTtl;
        return value;
    }
}
