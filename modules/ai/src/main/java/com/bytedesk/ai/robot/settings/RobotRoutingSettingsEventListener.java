/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-07 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-07 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.robot.settings;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.menu.MenuEntity;
import com.bytedesk.core.menu.MenuTypeEnum;
import com.bytedesk.core.menu.event.MenuUpdateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 监听菜单更新事件：
 * 当超级管理后台将 "/ai" 菜单隐藏/禁用后，全量关闭 RobotRoutingSettings 的机器人接待开关
 * （defaultRobot/offlineRobot/nonWorktimeRobot 全部置为 false），
 * 使得工作组/技能组不再路由到机器人。
 */
@Slf4j
@Component
@AllArgsConstructor
public class RobotRoutingSettingsEventListener {

    /** AI 模块在管理后台的根菜单链接 */
    private static final String AI_MENU_LINK = "/ai";

    private final RobotRoutingSettingsService robotRoutingSettingsService;

    @EventListener
    public void onMenuUpdateEvent(MenuUpdateEvent event) {
        MenuEntity menu = event.getMenu();
        if (menu == null || !AI_MENU_LINK.equals(menu.getLink())) {
            return;
        }
        // 仅处理管理后台 ADMIN 类型菜单的隐藏动作
        if (!MenuTypeEnum.ADMIN.name().equals(menu.getType())) {
            return;
        }
        // 仅在菜单被隐藏/禁用时触发
        if (!Boolean.FALSE.equals(menu.getEnabled())) {
            return;
        }
        log.info("/ai menu disabled, disabling all robot routing settings");
        robotRoutingSettingsService.disableAllRobotRouting();
    }
}
