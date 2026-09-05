/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-01 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-01 11:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;

/**
 * Jackson 3 (tools.jackson) 日期时间配置
 *
 * Spring Boot 4 起，HTTP 消息转换默认使用 Jackson 3 自动配置的 JsonMapper，
 * JacksonConfig 中定义的 Jackson 2 ObjectMapper Bean 不再作用于 REST 接口的 JSON 解析。
 * 因此这里通过 JsonMapperBuilderCustomizer 注册宽松的 ZonedDateTime 反序列化器，
 * 兼容 "yyyy-MM-dd HH:mm:ss" 等不带时区的格式，与后端返回的日期字符串格式保持一致，
 * 否则前端回传 "2026-10-01 10:54:50" 这类字符串时会报错：
 * Cannot deserialize value of type `java.time.ZonedDateTime` from String ...
 */
@Configuration
@Description("Jackson 3 DateTime Configuration - HTTP JSON 使用的 Jackson 3 宽松日期时间反序列化配置")
public class Jackson3DateTimeConfig {

    /**
     * 自定义 ZonedDateTime 反序列化器（Jackson 3 版本）
     * 支持多种日期时间格式，包括没有时区信息的格式，
     * 与 JacksonConfig.ZonedDateTimeDeserializer（Jackson 2）保持一致的格式列表
     */
    public static class ZonedDateTimeDeserializer extends ValueDeserializer<ZonedDateTime> {

        private static final DateTimeFormatter[] FORMATTERS = {
            // 带时区的格式
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"),
            // 不带时区的格式，使用系统默认时区
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        };

        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext context) {
            String value = p.getString();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            // 尝试多种格式解析
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    if (formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
                        // 对于 ISO 格式，直接解析
                        return ZonedDateTime.parse(value, formatter);
                    }
                    // 对于其他格式，先解析为 LocalDateTime，然后转换为 ZonedDateTime
                    LocalDateTime localDateTime = LocalDateTime.parse(value, formatter);
                    return localDateTime.atZone(ZoneId.systemDefault());
                } catch (DateTimeParseException e) {
                    // 继续尝试下一个格式
                    continue;
                }
            }

            // 如果所有格式都失败，抛出异常
            return context.reportInputMismatch(ZonedDateTime.class,
                    "Unable to parse ZonedDateTime: " + value);
        }
    }

    /**
     * 注册到 Spring Boot 4 自动配置的 Jackson 3 JsonMapper，
     * 该 JsonMapper 用于 REST 接口的 HTTP JSON 消息转换
     */
    @Bean
    public JsonMapperBuilderCustomizer bytedeskZonedDateTimeMapperCustomizer() {
        return builder -> builder.addModule(bytedeskZonedDateTimeModule());
    }

    public static SimpleModule bytedeskZonedDateTimeModule() {
        SimpleModule module = new SimpleModule("BytedeskZonedDateTimeModule");
        module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        return module;
    }
}
