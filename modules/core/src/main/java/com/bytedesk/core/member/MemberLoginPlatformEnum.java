package com.bytedesk.core.member;

import java.util.LinkedHashSet;
import java.util.Set;

import com.bytedesk.core.constant.BytedeskConsts;

public enum MemberLoginPlatformEnum {

    ADMIN("admin"),
    DESKTOP("desktop"),
    NOTEBASE("notebase"),
    WORKFLOW("workflow"),
    CALL("call"),
    CALL_ADMIN("callAdmin");

    private final String code;

    MemberLoginPlatformEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static boolean isSupported(String code) {
        if (code == null) {
            return false;
        }
        for (MemberLoginPlatformEnum platform : values()) {
            if (platform.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> allCodes() {
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        for (MemberLoginPlatformEnum platform : values()) {
            codes.add(platform.code);
        }
        return codes;
    }

    public static Set<String> defaultForRoleUids(Set<String> roleUids) {
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        if (roleUids != null && (roleUids.contains(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                || roleUids.contains(BytedeskConsts.DEFAULT_ROLE_SUPER_UID))) {
            codes.addAll(allCodes());
            return codes;
        }
        codes.add(DESKTOP.code);
        return codes;
    }
}