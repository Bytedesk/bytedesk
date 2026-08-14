/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;

import lombok.extern.slf4j.Slf4j;

/**
 * 国际化文本解析器：将 {@code i18n.xxx} 形式的 key 解析为当前 Locale 的本地化文本。
 *
 * <p>适用场景：业务层（Service / SSE 异步线程等）发送消息前，需要把存储为 i18n key 的
 * 兜底文案（如机器人默认回复 {@link I18Consts#I18N_ROBOT_DEFAULT_REPLY}）解析成实际文本，
 * 避免 i18n key 直接泄漏给前端。</p>
 *
 * <p>解析规则与 {@code GlobalExceptionHandler.tryTranslateI18nKey} 保持一致：
 * <ul>
 *   <li>仅处理以 {@link I18Consts#I18N_PREFIX} 开头的字符串，其它原样返回；</li>
 *   <li>支持 {@link I18Consts#I18N_ARG_SEPARATOR} 携带占位参数，格式 {@code key|arg1|arg2}；</li>
 *   <li>解析失败（无对应 properties 条目 / 上下文未就绪）时回退为 key 本身，不抛异常。</li>
 * </ul>
 * </p>
 *
 * <p>Locale 解析顺序：显式传入 locale → {@link LocaleContextHolder}（HTTP 请求线程）→
 * 系统默认 Locale。SSE 异步线程中 {@link LocaleContextHolder} 通常为空，因此默认回退到
 * 简体中文（zh_CN），与 bytedesk 后端兜底语言一致。</p>
 */
@Slf4j
public final class I18nTextResolver {

    /** 后端兜底 Locale：异步线程无请求上下文时使用。 */
    public static final Locale FALLBACK_LOCALE = Locale.SIMPLIFIED_CHINESE;

    private I18nTextResolver() {
    }

    /**
     * 判断给定文本是否为 i18n key（以 {@code i18n.} 开头）。
     */
    public static boolean isI18nKey(String text) {
        return StringUtils.hasText(text) && text.startsWith(I18Consts.I18N_PREFIX);
    }

    /**
     * 若 text 为 i18n key 则解析为本地化文本，否则原样返回。
     *
     * <p>使用 {@link LocaleContextHolder} 解析当前 Locale；异步线程无上下文时回退到
     * {@link #FALLBACK_LOCALE}。</p>
     *
     * @param text 待解析文本，可为 {@code null}
     * @return 解析后的本地化文本；非 key 或解析失败时返回原文本
     */
    public static String resolveIfKey(String text) {
        return resolveIfKey(text, null);
    }

    /**
     * 若 text 为 i18n key 则解析为本地化文本，否则原样返回。
     *
     * @param text   待解析文本，可为 {@code null}
     * @param locale 显式 Locale，为 {@code null} 时按 {@link LocaleContextHolder} →
     *               {@link #FALLBACK_LOCALE} 顺序解析
     * @return 解析后的本地化文本；非 key 或解析失败时返回原文本
     */
    public static String resolveIfKey(String text, Locale locale) {
        if (!isI18nKey(text)) {
            return text;
        }
        if (!ApplicationContextHolder.isInitialized()) {
            // 上下文未就绪（如启动早期），避免抛异常
            return text;
        }
        try {
            String key = text;
            Object[] args = null;
            int separatorIndex = key.indexOf(I18Consts.I18N_ARG_SEPARATOR);
            if (separatorIndex >= 0) {
                key = text.substring(0, separatorIndex);
                String rawArgs = text.substring(separatorIndex + I18Consts.I18N_ARG_SEPARATOR.length());
                args = rawArgs.isEmpty() ? new Object[0] : rawArgs.split("\\|", -1);
            }
            MessageSource messageSource = resolveMessageSource();
            Locale resolveLocale = locale != null ? locale : resolveLocale();
            String resolved = messageSource.getMessage(key, args, text, resolveLocale);
            if (resolved == null || resolved.isBlank()) {
                return text;
            }
            return resolved;
        } catch (Exception ex) {
            log.debug("Failed to translate i18n key: {} ({})", text, ex.getMessage());
            return text;
        }
    }

    /**
     * 解析 {@link MessageSource}。
     *
     * <p>容器中通常存在两个实现 {@link MessageSource} 接口的 bean：
     * Spring Boot 自动配置的 {@code messageSource}（主 MessageSource），以及
     * {@link org.springframework.context.ApplicationContext ApplicationContext} 自身
     * （它继承了 {@link MessageSource} 接口，bean 名通常为 {@code applicationEventPublisher}）。
     * 直接 {@code getBean(MessageSource.class)} 会因找到 2 个匹配而抛
     * {@code NoUniqueBeanDefinitionException}，因此优先按名字取 {@code messageSource} bean，
     * 失败时回退到 ApplicationContext 自身（其 {@code getMessage} 会委托给容器内主 MessageSource）。</p>
     */
    private static MessageSource resolveMessageSource() {
        try {
            // 优先按 bean 名取 Spring Boot 自动配置的 messageSource，避免 getBean(Class) 因
            // ApplicationContext 自身也实现 MessageSource 而抛 NoUniqueBeanDefinitionException
            return ApplicationContextHolder.getApplicationContext()
                    .getBean("messageSource", MessageSource.class);
        } catch (Exception ex) {
            // 回退：ApplicationContext 自身实现 MessageSource，内部委托给容器内 messageSource
            return ApplicationContextHolder.getApplicationContext();
        }
    }

    /**
     * 解析当前应使用的 Locale。
     *
     * <p>优先取请求线程的 {@link LocaleContextHolder}；为空（异步线程/无请求上下文）时
     * 回退到 {@link #FALLBACK_LOCALE}。</p>
     */
    private static Locale resolveLocale() {
        try {
            Locale ctxLocale = LocaleContextHolder.getLocale();
            if (ctxLocale != null) {
                return ctxLocale;
            }
        } catch (Exception ignored) {
            // 测试环境或无请求上下文时可能抛错，忽略
        }
        return FALLBACK_LOCALE;
    }
}
