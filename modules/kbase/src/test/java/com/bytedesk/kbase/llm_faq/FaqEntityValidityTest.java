package com.bytedesk.kbase.llm_faq;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.utils.BdDateUtils;

/**
 * FaqEntity 有效期判定助手单元测试：
 * 统一 null=无边界语义（startDate==null 早已生效，endDate==null 永不过期）
 */
class FaqEntityValidityTest {

    private FaqEntity buildFaq(ZonedDateTime startDate, ZonedDateTime endDate) {
        return FaqEntity.builder()
                .question("q")
                .answer("a")
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Test
    void validNow_whenBothNull_shouldBeValid() {
        FaqEntity faq = buildFaq(null, null);
        assertThat(faq.isValidNow()).isTrue();
        assertThat(faq.isExpired()).isFalse();
        assertThat(faq.isNotStarted()).isFalse();
    }

    @Test
    void validNow_whenWithinRange_shouldBeValid() {
        ZonedDateTime now = BdDateUtils.now();
        FaqEntity faq = buildFaq(now.minusHours(1), now.plusHours(1));
        assertThat(faq.isValidNow()).isTrue();
        assertThat(faq.isExpired()).isFalse();
        assertThat(faq.isNotStarted()).isFalse();
    }

    @Test
    void validNow_whenExpired_shouldBeInvalidAndExpired() {
        ZonedDateTime now = BdDateUtils.now();
        FaqEntity faq = buildFaq(now.minusDays(2), now.minusDays(1));
        assertThat(faq.isValidNow()).isFalse();
        assertThat(faq.isExpired()).isTrue();
        assertThat(faq.isNotStarted()).isFalse();
    }

    @Test
    void validNow_whenNotStarted_shouldBeInvalidAndNotStarted() {
        ZonedDateTime now = BdDateUtils.now();
        FaqEntity faq = buildFaq(now.plusDays(1), now.plusDays(2));
        assertThat(faq.isValidNow()).isFalse();
        assertThat(faq.isExpired()).isFalse();
        assertThat(faq.isNotStarted()).isTrue();
    }

    @Test
    void validNow_whenStartDateNull_shouldIgnoreStartBoundary() {
        ZonedDateTime now = BdDateUtils.now();
        FaqEntity faq = buildFaq(null, now.plusHours(1));
        assertThat(faq.isValidNow()).isTrue();
    }

    @Test
    void validNow_whenEndDateNull_shouldNeverExpire() {
        ZonedDateTime now = BdDateUtils.now();
        FaqEntity faq = buildFaq(now.minusDays(365), null);
        assertThat(faq.isValidNow()).isTrue();
        assertThat(faq.isExpired()).isFalse();
    }

    @Test
    void validAt_whenBoundaryEqual_shouldBeValid() {
        // 临界相等：startDate == at == endDate 应视为有效（闭区间）
        ZonedDateTime at = BdDateUtils.now();
        FaqEntity faq = buildFaq(at, at);
        assertThat(faq.isValidAt(at)).isTrue();
    }
}
