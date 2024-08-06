/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 07:32:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-30 22:24:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.keyword;

public enum KeywordMatchEnum {
    EXACT, // 精确匹配
    FUZZY, // 模糊匹配
    REGULAR, // 正则匹配
    VECTOR, // 向量匹配
    ELASTIC; // ElasticSearch搜索引擎匹配

    // 根据字符串查找对应的枚举常量
    public static KeywordMatchEnum fromValue(String value) {
        for (KeywordMatchEnum type : KeywordMatchEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No KeywordMatchEnum constant with value: " + value);
    }
}
