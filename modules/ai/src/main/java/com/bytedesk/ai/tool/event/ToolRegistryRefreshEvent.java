/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-12 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-12 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool.event;

import org.springframework.context.ApplicationEvent;

/**
 * 发布于 ToolEntity 注册表发生变更（创建 / 更新 / 软删除 / 平台同步失效）时，
 * 供 Caffeine 本地缓存（{@code ToolRegistryCacheService}）等热路径消费者失效缓存。
 *
 * <p>与 {@link ToolCreateEvent} / {@link ToolUpdateEvent} 的区别：
 * <ul>
 *   <li>前者面向 JPA 实体监听器，携带 {@code ToolEntity} 快照；</li>
 *   <li>本事件面向缓存层，仅在"注册表整体需要刷新"时触发，不携带具体实体，
 *       避免缓存层与实体生命周期耦合。</li>
 * </ul>
 */
public class ToolRegistryRefreshEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * 触发刷新的来源标记，仅用于日志排障。
     */
    private final String source_;

    public ToolRegistryRefreshEvent(Object source) {
        super(source);
        this.source_ = source == null ? "unknown" : source.getClass().getSimpleName();
    }

    public String getSourceType() {
        return source_;
    }
}
