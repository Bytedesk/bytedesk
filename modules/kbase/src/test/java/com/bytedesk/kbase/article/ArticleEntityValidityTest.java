package com.bytedesk.kbase.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.utils.BdDateUtils;

/**
 * ArticleEntity 有效期判定助手单元测试：
 * Article 的 startDate/endDate 可空（无 @Builder.Default），null=无边界语义
 */
class ArticleEntityValidityTest {

    private ArticleEntity buildArticle(ZonedDateTime startDate, ZonedDateTime endDate) {
        return ArticleEntity.builder()
                .title("t")
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Test
    void validNow_whenBothNull_shouldBeValid() {
        // 未设置有效期的文章视为永久有效（修复问题 B 的语义基础）
        ArticleEntity article = buildArticle(null, null);
        assertThat(article.isValidNow()).isTrue();
        assertThat(article.isExpired()).isFalse();
        assertThat(article.isNotStarted()).isFalse();
    }

    @Test
    void validNow_whenWithinRange_shouldBeValid() {
        ZonedDateTime now = BdDateUtils.now();
        ArticleEntity article = buildArticle(now.minusHours(1), now.plusHours(1));
        assertThat(article.isValidNow()).isTrue();
    }

    @Test
    void validNow_whenExpired_shouldBeInvalidAndExpired() {
        ZonedDateTime now = BdDateUtils.now();
        ArticleEntity article = buildArticle(now.minusDays(2), now.minusDays(1));
        assertThat(article.isValidNow()).isFalse();
        assertThat(article.isExpired()).isTrue();
    }

    @Test
    void validNow_whenNotStarted_shouldBeInvalidAndNotStarted() {
        ZonedDateTime now = BdDateUtils.now();
        ArticleEntity article = buildArticle(now.plusDays(1), now.plusDays(2));
        assertThat(article.isValidNow()).isFalse();
        assertThat(article.isNotStarted()).isTrue();
    }
}
