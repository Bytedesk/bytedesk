package com.bytedesk.core.member;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

class MemberLoginPlatformEnumTest {

    @Test
    void resolveImportPlatformsShouldDefaultToAllPlatformsWhenBlank() {
        assertEquals(MemberLoginPlatformEnum.allCodes(), MemberLoginPlatformEnum.resolveImportPlatforms(null));
        assertEquals(MemberLoginPlatformEnum.allCodes(), MemberLoginPlatformEnum.resolveImportPlatforms("   "));
    }

    @Test
    void resolveImportPlatformsShouldParseMixedCodesAndLabels() {
        LinkedHashSet<String> expected = new LinkedHashSet<>(List.of("admin", "desktop", "callAdmin"));

        assertEquals(
                expected,
                MemberLoginPlatformEnum.resolveImportPlatforms("管理后台, desktop, 呼叫中心后台 callAdmin"));
    }

    @Test
    void resolveImportPlatformsShouldSupportAllPlatformAliases() {
        assertEquals(MemberLoginPlatformEnum.allCodes(), MemberLoginPlatformEnum.resolveImportPlatforms("所有平台"));
        assertEquals(MemberLoginPlatformEnum.allCodes(), MemberLoginPlatformEnum.resolveImportPlatforms("all"));
    }
}