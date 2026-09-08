/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

/**
 * 知识库有效期过滤工具：
 * 构造 null 容忍的 startDate/endDate 范围过滤（对 keyword 定宽字符串做词法比较）。
 *
 * 语义：
 * - 有效 = (startDate 缺失 或 startDate <= now) AND (endDate 缺失 或 endDate >= now)
 * - 字段缺失（存量未重建文档、未设置有效期的内容）视为无边界（永久有效）
 *
 * 日期存储格式统一为定宽字符串 yyyy-MM-dd HH:mm:ss（BdDateUtils.formatDatetimeToString，
 * 固定 Asia/Shanghai、无毫秒），词法序 = 时间序，keyword range 比较可靠。
 */
public class KbaseValidityFilterUtils {

    private KbaseValidityFilterUtils() {
    }

    /**
     * 构造有效期过滤 query（作为 bool filter 使用）
     *
     * @param now 定宽格式的当前时间字符串（yyyy-MM-dd HH:mm:ss，须与索引写入格式一致）
     */
    public static Query validityFilterQuery(String now) {
        return validityBool(now)._toQuery();
    }

    /**
     * 构造有效期过滤 BoolQuery（已包含 minimumShouldMatch，可直接 _toQuery）
     */
    public static BoolQuery validityBool(String now) {
        // startDate 缺失 或 startDate <= now
        BoolQuery.Builder startShould = new BoolQuery.Builder();
        startShould.should(QueryBuilders.range()
                .term(t -> t.field("startDate").lte(now))
                .build()._toQuery());
        startShould.should(QueryBuilders.bool()
                .mustNot(QueryBuilders.exists().field("startDate").build()._toQuery())
                .build()._toQuery());
        startShould.minimumShouldMatch("1");

        // endDate 缺失 或 endDate >= now
        BoolQuery.Builder endShould = new BoolQuery.Builder();
        endShould.should(QueryBuilders.range()
                .term(t -> t.field("endDate").gte(now))
                .build()._toQuery());
        endShould.should(QueryBuilders.bool()
                .mustNot(QueryBuilders.exists().field("endDate").build()._toQuery())
                .build()._toQuery());
        endShould.minimumShouldMatch("1");

        BoolQuery.Builder filter = new BoolQuery.Builder();
        filter.must(startShould.build()._toQuery());
        filter.must(endShould.build()._toQuery());
        return filter.build();
    }
}
