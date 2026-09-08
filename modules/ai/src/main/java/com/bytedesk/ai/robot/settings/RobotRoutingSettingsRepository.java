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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Robot routing settings repository
 */
public interface RobotRoutingSettingsRepository extends JpaRepository<RobotRoutingSettingsEntity, Long> {

    /**
     * 批量关闭所有机器人路由开关：defaultRobot/offlineRobot/nonWorktimeRobot 全部置为 false。
     * 用于 "/ai" 菜单隐藏后停用所有工作组/技能组的机器人接待策略（含 published 与 draft 行）。
     *
     * @return 实际更新的行数
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE RobotRoutingSettingsEntity r
               SET r.defaultRobot = false,
                   r.offlineRobot = false,
                   r.nonWorktimeRobot = false
             WHERE r.deleted = false
               AND (r.defaultRobot = true OR r.offlineRobot = true OR r.nonWorktimeRobot = true)
            """)
    int disableAllRobotRouting();
}
