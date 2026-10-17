/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service, is a breach of the terms and automatically terminates your rights under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket_settings_visibility;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工单可见性设置统一解析入口。
 *
 * <p>解析顺序（与详情口径一致）：
 * <ol>
 * <li>工单自身设置引用（ticketSettingsUid）的已发布 visibilitySettings，不按 deleted/enabled
 * 额外过滤（软删除/解绑的历史引用仍按其内容判定，见 TicketSettingsRepository.findByUidIn 语义）；</li>
 * <li>引用为空/记录不存在/可见性子实体为空 → 回退本组织同类型默认设置；</li>
 * <li>仍无可用数据 → 默认 ORG_WIDE。</li>
 * </ol>
 * 只读已发布内容，不读草稿；返回规范化深拷贝，不改动 JPA 管理实体。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketVisibilityService {

    private final TicketSettingsRestService ticketSettingsRestService;

    /**
     * 按组织 + 工单类型 + 设置引用解析已发布可见性数据（查询条件入口，无需单个工单实体）。
     */
    public TicketVisibilitySettingsData resolve(String orgUid, String type, String ticketSettingsUid) {
        TicketVisibilitySettingsEntity visibilitySettings = null;
        if (StringUtils.hasText(ticketSettingsUid)) {
            visibilitySettings = ticketSettingsRestService.findByUid(ticketSettingsUid)
                    .map(settings -> settings.getVisibilitySettings())
                    .orElse(null);
        }
        if (visibilitySettings == null) {
            String normalizedType = TicketTypeEnum.fromValue(type).name();
            visibilitySettings = ticketSettingsRestService
                    .findDefaultByOrgUidAndType(orgUid, normalizedType)
                    .map(settings -> settings.getVisibilitySettings())
                    .orElse(null);
        }
        // 子实体存在但 content 为空时按默认 ORG_WIDE 处理（converter 不会返回 null content，
        // 此处防御旧数据/手工修改）
        TicketVisibilitySettingsData data = visibilitySettings == null
                ? TicketVisibilitySettingsData.builder().build()
                : (visibilitySettings.getContent() == null
                        ? TicketVisibilitySettingsData.builder().build()
                        : visibilitySettings.getContent());
        return data.normalizedCopy();
    }

    /**
     * 按工单实体解析已发布可见性数据（通知/详情入口）。
     */
    public TicketVisibilitySettingsData resolve(TicketEntity ticket) {
        if (ticket == null) {
            return TicketVisibilitySettingsData.builder().build().normalizedCopy();
        }
        return resolve(ticket.getOrgUid(), ticket.getType(), ticket.getTicketSettingsUid());
    }

    /**
     * 判断解析结果是否为未配置的默认值（无任何实际设置命中）。
     * 供调用方区分「正常无配置」与「查询异常」：本方法抛出异常时调用方不得按 ORG_WIDE 伪装成功。
     */
    public boolean isDefaultFallback(TicketVisibilitySettingsData data) {
        return data != null
                && TicketVisibilityModeEnum.ORG_WIDE.name().equals(data.getMode())
                && (data.getDepartmentUids() == null || data.getDepartmentUids().isEmpty())
                && (data.getCategoryRules() == null || data.getCategoryRules().isEmpty());
    }
}
