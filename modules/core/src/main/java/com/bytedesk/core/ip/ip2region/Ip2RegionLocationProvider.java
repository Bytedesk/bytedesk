package com.bytedesk.core.ip.ip2region;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.ip.IpLocationProvider;
import com.bytedesk.core.ip.IpLocationResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class Ip2RegionLocationProvider implements IpLocationProvider {

    public static final String PROVIDER_NAME = "ip2region";

    private final IP2RegionConfig ip2RegionConfig;

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        return ip2RegionConfig.isAvailable();
    }

    @Override
    public IpLocationResult locate(String ip) {
        Searcher searcher = ip2RegionConfig.getSearcher();
        if (searcher == null) {
            return null;
        }
        try {
            String location = searcher.search(ip);
            if (!StringUtils.hasText(location)) {
                return IpLocationResult.local(getName(), ip);
            }
            return IpLocationResult.fromPipeLocation(getName(), ip, location);
        } catch (Exception e) {
            log.error("failed to search ip2region for ip {}", ip, e);
            return null;
        }
    }
}