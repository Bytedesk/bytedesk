package com.bytedesk.kbase.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

/**
 * 有效期过滤构造工具单元测试：
 * 验证 null 容忍语义的查询结构可正确构建
 */
class KbaseValidityFilterUtilsTest {

    private static final String NOW = "2026-09-08 10:00:00";

    @Test
    void validityFilterQuery_shouldBuildWithoutError() {
        Query query = KbaseValidityFilterUtils.validityFilterQuery(NOW);
        assertThat(query).isNotNull();
        assertThat(query.isBool()).isTrue();
    }

    @Test
    void validityBool_shouldContainStartAndEndClauses() {
        BoolQuery boolQuery = KbaseValidityFilterUtils.validityBool(NOW);
        // 两个 must：startDate 子句 + endDate 子句
        assertThat(boolQuery.must()).hasSize(2);
        boolQuery.must().forEach(m -> {
            assertThat(m.isBool()).isTrue();
            // 每个子句：range OR NOT exists，minimumShouldMatch=1
            BoolQuery clause = m.bool();
            assertThat(clause.should()).hasSize(2);
            assertThat(clause.minimumShouldMatch()).isEqualTo("1");
        });
    }

    @Test
    void validityBool_startClause_shouldContainRangeAndMustNotExists() {
        BoolQuery boolQuery = KbaseValidityFilterUtils.validityBool(NOW);
        // 第一个 must 是 startDate 子句
        BoolQuery startClause = boolQuery.must().get(0).bool();
        boolean hasRange = startClause.should().stream().anyMatch(q -> q.isRange());
        boolean hasMustNotExists = startClause.should().stream()
                .anyMatch(q -> q.isBool() && q.bool().mustNot() != null
                        && q.bool().mustNot().stream().anyMatch(n -> n.isExists()));
        assertThat(hasRange).isTrue();
        assertThat(hasMustNotExists).isTrue();
    }

    @Test
    void validityBool_endClause_shouldContainRangeAndMustNotExists() {
        BoolQuery boolQuery = KbaseValidityFilterUtils.validityBool(NOW);
        // 第二个 must 是 endDate 子句
        BoolQuery endClause = boolQuery.must().get(1).bool();
        boolean hasRange = endClause.should().stream().anyMatch(q -> q.isRange());
        boolean hasMustNotExists = endClause.should().stream()
                .anyMatch(q -> q.isBool() && q.bool().mustNot() != null
                        && q.bool().mustNot().stream().anyMatch(n -> n.isExists()));
        assertThat(hasRange).isTrue();
        assertThat(hasMustNotExists).isTrue();
    }

    @Test
    void validityBool_rangeBoundaries_shouldUseGivenNow() {
        BoolQuery boolQuery = KbaseValidityFilterUtils.validityBool(NOW);
        // startDate 子句的 range 是 lte(now)，endDate 子句的 range 是 gte(now)
        BoolQuery startClause = boolQuery.must().get(0).bool();
        var startRange = startClause.should().stream()
                .filter(q -> q.isRange()).findFirst().orElseThrow().range();
        assertThat(startRange.isTerm()).isTrue();
        assertThat(startRange.term().lte()).isEqualTo(NOW);

        BoolQuery endClause = boolQuery.must().get(1).bool();
        var endRange = endClause.should().stream()
                .filter(q -> q.isRange()).findFirst().orElseThrow().range();
        assertThat(endRange.isTerm()).isTrue();
        assertThat(endRange.term().gte()).isEqualTo(NOW);
    }
}
