package com.bytedesk.core.ip;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpLocationResult {

    public static final String LOCAL_LOCATION = "0|0|0|内网IP|内网IP";

    private String provider;

    private String ip;

    private String location;

    private String country;

    private String region;

    private String province;

    private String city;

    private String isp;

    private Double latitude;

    private Double longitude;

    public static IpLocationResult local(String provider, String ip) {
        return fromPipeLocation(provider, ip, LOCAL_LOCATION);
    }

    public static IpLocationResult fromPipeLocation(String provider, String ip, String location) {
        String normalizedLocation = StringUtils.hasText(location) ? location : LOCAL_LOCATION;
        String[] parts = normalizedLocation.split("\\|", -1);
        return new IpLocationResult(
                provider,
                ip,
                buildLocation(
                        part(parts, 0),
                        part(parts, 1),
                        part(parts, 2),
                        part(parts, 3),
                        part(parts, 4)),
                part(parts, 0),
                part(parts, 1),
                part(parts, 2),
                part(parts, 3),
                part(parts, 4),
                null,
                null);
    }

    public static IpLocationResult of(String provider, String ip, String country, String region, String province,
            String city, String isp, Double latitude, Double longitude) {
        return new IpLocationResult(
                provider,
                ip,
                buildLocation(country, region, province, city, isp),
                normalize(country),
                normalize(region),
                normalize(province),
                normalize(city),
                normalize(isp),
                latitude,
                longitude);
    }

    private static String buildLocation(String country, String region, String province, String city, String isp) {
        return String.join("|",
                normalize(country),
                normalize(region),
                normalize(province),
                normalize(city),
                normalize(isp));
    }

    private static String part(String[] parts, int index) {
        if (parts == null || parts.length <= index) {
            return "0";
        }
        return normalize(parts[index]);
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "0";
    }
}