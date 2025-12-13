/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Attribute converter for quick button payload
 */
package com.bytedesk.kbase.quick_button;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class QuickButtonPayloadConverter implements AttributeConverter<QuickButtonPayload, String> {

    @Override
    public String convertToDatabaseColumn(QuickButtonPayload attribute) {
        if (attribute == null) {
            return null;
        }
        return JSON.toJSONString(attribute);
    }

    @Override
    public QuickButtonPayload convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData)) {
            return null;
        }
        return JSON.parseObject(dbData, QuickButtonPayload.class);
    }
}
