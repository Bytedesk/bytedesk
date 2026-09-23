/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms of the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.robot.settings;

/**
 * AI 机器人权益门控判定结果
 *
 * <p>
 * 口径与管理后台菜单可见性保持一致：
 * 菜单 enabled != false 且 组织 vipLevel >= 菜单 vipLevel 才允许进入大模型机器人对话。
 * 不校验 vipExpireDate（与前端 fetchMenuVisibility / AuthoritySpecification 同口径）。
 *
 * @see RobotEntitlementService
 */
public record RobotEntitlementResult(
        boolean allowed,
        int orgVipLevel,
        int menuVipLevel,
        boolean menuEnabled,
        boolean menuFound,
        boolean usedOrgMenu,
        Reason reason) {

    /** 拒绝/放行原因枚举，供结构化日志与排查使用 */
    public enum Reason {
        /** 权益校验通过 */
        OK,
        /** /ai 菜单不存在（种子未初始化等异常场景），fail-open 放行 */
        MENU_NOT_FOUND,
        /** /ai 菜单被禁用 */
        MENU_DISABLED,
        /** 组织 vipLevel 低于 /ai 菜单要求的最低等级 */
        VIP_LEVEL_INSUFFICIENT
    }

    static RobotEntitlementResult menuNotFound() {
        return new RobotEntitlementResult(true, 0, 0, true, false, false, Reason.MENU_NOT_FOUND);
    }

    static RobotEntitlementResult menuDisabled(int orgVipLevel, int menuVipLevel, boolean usedOrgMenu) {
        return new RobotEntitlementResult(false, orgVipLevel, menuVipLevel, false, true, usedOrgMenu,
                Reason.MENU_DISABLED);
    }

    static RobotEntitlementResult vipInsufficient(int orgVipLevel, int menuVipLevel, boolean usedOrgMenu) {
        return new RobotEntitlementResult(false, orgVipLevel, menuVipLevel, true, true, usedOrgMenu,
                Reason.VIP_LEVEL_INSUFFICIENT);
    }

    static RobotEntitlementResult ok(int orgVipLevel, int menuVipLevel, boolean usedOrgMenu) {
        return new RobotEntitlementResult(true, orgVipLevel, menuVipLevel, true, true, usedOrgMenu, Reason.OK);
    }
}
