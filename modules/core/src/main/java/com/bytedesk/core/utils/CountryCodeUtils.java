package com.bytedesk.core.utils;

import org.springframework.util.StringUtils;

public final class CountryCodeUtils {

    public static final String DEFAULT_COUNTRY = "86";

    private CountryCodeUtils() {
    }

    public static String normalize(String country) {
        return StringUtils.hasText(country) ? country.trim() : DEFAULT_COUNTRY;
    }

    public static String buildMobileUsername(String country, String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return mobile;
        }
        return normalize(country) + mobile.trim();
    }
}