package com.bytedesk.disk.archive;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DiskArchivePolicyTest {
    @Test
    void acceptsOnlyKnownModulesAndRequestIds() {
        assertThat(DiskArchivePolicy.requiredModule("CONTRACT")).isEqualTo("CONTRACT");
        assertThat(DiskArchivePolicy.requiredModule("INVOICE")).isEqualTo("INVOICE");
        assertThatThrownBy(() -> DiskArchivePolicy.requiredModule("HR")).isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> DiskArchivePolicy.requiredModule(null)).isInstanceOf(DiskArchiveException.class);
        assertThat(DiskArchivePolicy.requestId("abc-1.2")).isEqualTo("abc-1.2");
        assertThatThrownBy(() -> DiskArchivePolicy.requestId("带空格 id")).isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> DiskArchivePolicy.requestId(null)).isInstanceOf(DiskArchiveException.class);
    }

    @Test
    void optionalMetadataAllowsNullsAndBoundLength() {
        assertThat(DiskArchivePolicy.optional(null, 10)).isNull();
        assertThat(DiskArchivePolicy.optional("  ", 10)).isNull();
        assertThat(DiskArchivePolicy.optional(" 对方A ", 255)).isEqualTo("对方A");
        assertThatThrownBy(() -> DiskArchivePolicy.optional("a".repeat(11), 10))
                .isInstanceOf(DiskArchiveException.class);
    }

    @Test
    void amountMustBeNonNegativeWithAtMostTwoDecimalsAndValidCurrency() {
        DiskArchivePolicy.amount(null, null);
        DiskArchivePolicy.amount(java.math.BigDecimal.valueOf(12.30), "cny");
        assertThatThrownBy(() -> DiskArchivePolicy.amount(java.math.BigDecimal.valueOf(-1), "CNY"))
                .isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> DiskArchivePolicy.amount(new java.math.BigDecimal("1.234"), "CNY"))
                .isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> DiskArchivePolicy.amount(java.math.BigDecimal.ONE, "XX"))
                .isInstanceOf(DiskArchiveException.class);
    }

    @Test
    void digestIsStableAndHexSha256() {
        assertThat(DiskArchivePolicy.digest("a\0b")).hasSize(64).matches("[0-9a-f]+");
        assertThat(DiskArchivePolicy.digest("a\0b")).isEqualTo(DiskArchivePolicy.digest("a\0b"));
        assertThat(DiskArchivePolicy.digest("a\0b")).isNotEqualTo(DiskArchivePolicy.digest("a\0c"));
    }
}
