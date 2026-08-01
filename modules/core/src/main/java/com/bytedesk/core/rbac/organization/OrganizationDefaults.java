package com.bytedesk.core.rbac.organization;

import java.time.ZonedDateTime;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.BdDateUtils;

public final class OrganizationDefaults {

    private static final int FALLBACK_DEFAULT_VIP_LEVEL = 0;
    private static final int FALLBACK_DEFAULT_VIP_DAYS = 365;

    private OrganizationDefaults() {
    }

    public static int resolveDefaultVipLevel(BytedeskProperties bytedeskProperties) {
        if (bytedeskProperties == null || bytedeskProperties.getOrganization() == null) {
            return FALLBACK_DEFAULT_VIP_LEVEL;
        }
        Integer configured = bytedeskProperties.getOrganization().getDefaultVipLevel();
        if (configured == null || configured < 0) {
            return FALLBACK_DEFAULT_VIP_LEVEL;
        }
        return configured;
    }

    public static ZonedDateTime resolveDefaultVipExpireDate(BytedeskProperties bytedeskProperties, Integer vipLevel) {
        int normalizedVipLevel = normalizeVipLevel(vipLevel);
        if (normalizedVipLevel <= 0) {
            return null;
        }
        return BdDateUtils.now().plusDays(resolveDefaultVipDays(bytedeskProperties));
    }

    public static int resolveDefaultVipDays(BytedeskProperties bytedeskProperties) {
        if (bytedeskProperties == null || bytedeskProperties.getOrganization() == null) {
            return FALLBACK_DEFAULT_VIP_DAYS;
        }
        Integer configured = bytedeskProperties.getOrganization().getDefaultVipDays();
        if (configured == null || configured <= 0) {
            return FALLBACK_DEFAULT_VIP_DAYS;
        }
        return configured;
    }

    public static int normalizeVipLevel(Integer vipLevel) {
        if (vipLevel == null || vipLevel < 0) {
            return 0;
        }
        return vipLevel;
    }
}