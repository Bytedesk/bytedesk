package com.bytedesk.core.ip.maxmind;

import java.net.InetAddress;

import org.springframework.stereotype.Component;

import com.bytedesk.core.ip.IpLocationProvider;
import com.bytedesk.core.ip.IpLocationResult;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MaxMindLocationProvider implements IpLocationProvider {

    public static final String PROVIDER_NAME = "maxmind";

    private final MaxMindConfig maxMindConfig;

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        return maxMindConfig.isAvailable();
    }

    @Override
    public IpLocationResult locate(String ip) {
        DatabaseReader databaseReader = maxMindConfig.getDatabaseReader();
        if (databaseReader == null) {
            return null;
        }
        try {
            InetAddress address = InetAddress.getByName(ip);
            CityResponse response = databaseReader.city(address);
            return IpLocationResult.of(
                    getName(),
                    ip,
                    response.country() != null ? response.country().name() : null,
                    response.continent() != null ? response.continent().name() : null,
                    response.mostSpecificSubdivision() != null ? response.mostSpecificSubdivision().name() : null,
                    response.city() != null ? response.city().name() : null,
                    null,
                    response.location() != null ? response.location().latitude() : null,
                    response.location() != null ? response.location().longitude() : null);
        } catch (AddressNotFoundException e) {
            log.debug("MaxMind address not found for ip {}", ip);
            return IpLocationResult.local(getName(), ip);
        } catch (Exception e) {
            log.error("Failed to lookup MaxMind location for ip {}", ip, e);
            return null;
        }
    }
}