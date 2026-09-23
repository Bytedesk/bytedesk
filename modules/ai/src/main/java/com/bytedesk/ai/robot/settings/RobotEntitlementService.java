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

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.menu.MenuEntity;
import com.bytedesk.core.menu.MenuRepository;
import com.bytedesk.core.menu.MenuTypeEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 机器人权益门控服务
 *
 * <p>
 * 在路由决策时点实时判定某组织当前是否被允许进入大模型机器人对话，
 * 判定口径与管理后台菜单可见性完全一致：
 * <ul>
 * <li>有效 /ai 菜单（组织行优先，回退平台行）enabled != false；</li>
 * <li>组织 vipLevel >= /ai 菜单 vipLevel。</li>
 * </ul>
 *
 * <p>
 * 设计要点：
 * <ul>
 * <li>非破坏性：不篡改 workgroupSettings.robotSettings，菜单恢复/组织升级后即时恢复机器人接待；</li>
 * <li>fail-open：/ai 菜单不存在（种子未初始化等异常场景）时放行并输出 WARN，避免误伤正常组织；</li>
 * <li>不校验 vipExpireDate：与前端 fetchMenuVisibility、后端 AuthoritySpecification 同口径。</li>
 * </ul>
 *
 * @see RobotEntitlementResult
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RobotEntitlementService {

    /** AI 模块在管理后台的根菜单链接（与 RobotRoutingSettingsEventListener 保持一致） */
    private static final String AI_MENU_LINK = "/ai";

    private final MenuRepository menuRepository;
    private final OrganizationRestService organizationRestService;

    /**
     * 判定组织当前是否被允许进入大模型机器人对话
     *
     * @param orgUid 组织 uid（工作组所属组织）
     * @return 判定结果（含拒绝原因与决策快照，供日志/排查复用）
     */
    public RobotEntitlementResult evaluate(String orgUid) {
        // 组织 vipLevel：组织不存在或未设置时按 0 处理（与实体 normalize 逻辑一致）
        int orgVipLevel = 0;
        if (StringUtils.hasText(orgUid)) {
            Optional<OrganizationEntity> organization = organizationRestService.findFreshByUid(orgUid);
            orgVipLevel = organization
                    .map(organizationEntity -> organizationEntity.getVipLevel())
                    .filter(vipLevel -> vipLevel != null)
                    .orElse(0);
        }

        // 解析有效 /ai 菜单：组织自定义行优先，回退平台默认行（对齐前端 queryByOrg 的覆盖语义）
        Optional<MenuEntity> orgMenu = StringUtils.hasText(orgUid)
                ? menuRepository.findByLinkAndOrgUidAndTypeAndDeletedFalse(
                        AI_MENU_LINK, orgUid, MenuTypeEnum.ADMIN.name())
                : Optional.empty();
        boolean usedOrgMenu = orgMenu.isPresent();
        MenuEntity menu = orgMenu.orElse(menuRepository
                .findByLinkAndOrgUidIsNullAndTypeAndDeletedFalse(AI_MENU_LINK, MenuTypeEnum.ADMIN.name())
                .orElse(null));

        // 菜单缺失：fail-open 放行（异常场景，输出 WARN 提示种子数据问题）
        if (menu == null) {
            log.warn("AI 权益门控: 未找到 /ai 菜单(种子数据未初始化?)，按 fail-open 放行 - orgUid: {}", orgUid);
            return RobotEntitlementResult.menuNotFound();
        }

        int menuVipLevel = menu.getVipLevel() != null ? menu.getVipLevel() : 0;
        boolean menuEnabled = !Boolean.FALSE.equals(menu.getEnabled());

        // 菜单被禁用：拒绝
        if (!menuEnabled) {
            log.info("AI 权益门控拒绝: /ai 菜单已禁用 - orgUid: {}, orgVipLevel: {}, menuVipLevel: {}, usedOrgMenu: {}",
                    orgUid, orgVipLevel, menuVipLevel, usedOrgMenu);
            return RobotEntitlementResult.menuDisabled(orgVipLevel, menuVipLevel, usedOrgMenu);
        }

        // 组织等级不足：拒绝
        if (orgVipLevel < menuVipLevel) {
            log.info("AI 权益门控拒绝: 组织 vipLevel 低于 /ai 菜单要求 - orgUid: {}, orgVipLevel: {}, menuVipLevel: {}, usedOrgMenu: {}",
                    orgUid, orgVipLevel, menuVipLevel, usedOrgMenu);
            return RobotEntitlementResult.vipInsufficient(orgVipLevel, menuVipLevel, usedOrgMenu);
        }

        log.debug("AI 权益门控放行 - orgUid: {}, orgVipLevel: {}, menuVipLevel: {}, usedOrgMenu: {}",
                orgUid, orgVipLevel, menuVipLevel, usedOrgMenu);
        return RobotEntitlementResult.ok(orgVipLevel, menuVipLevel, usedOrgMenu);
    }

    /**
     * 便捷方法：组织当前是否被允许进入大模型机器人对话
     */
    public boolean isAllowed(String orgUid) {
        return evaluate(orgUid).allowed();
    }
}
