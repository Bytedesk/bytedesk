package com.bytedesk.ai.keyword;

public enum KeywordMatchEnum {
    EXACT, // 精确匹配
    FUZZY, // 模糊匹配
    REGULAR, // 正则匹配
    VECTOR, // 向量匹配
    ELASTIC; // ElasticSearch搜索引擎匹配
}
