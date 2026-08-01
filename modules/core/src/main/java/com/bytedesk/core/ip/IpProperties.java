package com.bytedesk.core.ip;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.ip")
public class IpProperties {

    private String provider = "ip2region";

    private boolean fallbackEnabled = true;

    private Ip2Region ip2region = new Ip2Region();

    private MaxMind maxmind = new MaxMind();

    @Data
    public static class Ip2Region {

        private String dbFile = "ip2region.xdb";
    }

    @Data
    public static class MaxMind {

        private String cityDbFile = "GeoLite2-City.mmdb";

        private List<String> locales = new ArrayList<>(List.of("zh-CN", "en"));
    }
}