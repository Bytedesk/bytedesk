package com.bytedesk.kbase.trigger.config;

import java.util.Map;
import java.util.Optional;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.kbase.trigger.TriggerKeyConsts;

/**
 * Central registry for mapping triggerKey -> config class.
 *
 * Note: TriggerTypeEnum(THREAD/CUSTOMER/TICKET) is a coarse category; the config schema
 * is typically determined by triggerKey.
 */
public final class TriggerConfigRegistry {

    private TriggerConfigRegistry() {
    }

    private static final Map<String, Class<?>> CONFIG_CLASS_BY_TRIGGER_KEY = Map.of(
            TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE,
            VisitorNoResponseProactiveMessageConfig.class);

    public static Optional<Class<?>> findConfigClass(String triggerKey) {
        if (triggerKey == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CONFIG_CLASS_BY_TRIGGER_KEY.get(triggerKey));
    }

    public static <T> Optional<T> parse(String triggerKey, String configJson, Class<T> clazz) {
        if (clazz == null) {
            return Optional.empty();
        }
        if (configJson == null || configJson.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(JSON.parseObject(configJson, clazz));
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    public static Optional<Object> parse(String triggerKey, String configJson) {
        Optional<Class<?>> clazzOpt = findConfigClass(triggerKey);
        if (clazzOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(JSON.parseObject(configJson, clazzOpt.get()));
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }
}
