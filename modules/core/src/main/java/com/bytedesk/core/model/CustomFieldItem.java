package com.bytedesk.core.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户自定义字段：字段昵称、字段 key、字段值
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段昵称（展示名）
     */
    private String nickname;

    /**
     * 字段 key（唯一标识，用于规则匹配/存取）
     */
    private String key;

    /**
     * 字段值
     */
    private String value;
}
