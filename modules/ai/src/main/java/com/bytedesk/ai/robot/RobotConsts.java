/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-13 17:11:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 14:15:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.redis.RedisConsts;

public class RobotConsts {
    private RobotConsts() {}

    // 
    public static final String DEFAULT_ROBOT_DEMO_UID = "robot_demo";
    // airline key
    public static final String ROBOT_INIT_DEMO_AIRLINE_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:init:demo:airline";
    // bytedesk key
    public static final String ROBOT_INIT_DEMO_BYTEDESK_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:init:demo:bytedesk";
    // shopping key
    public static final String ROBOT_INIT_DEMO_SHOPPING_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:init:demo:shopping";
    // 
    public static final String CATEGORY_JOB = I18Consts.I18N_PREFIX + "JOB";
    public static final String CATEGORY_LANGUAGE = I18Consts.I18N_PREFIX + "LANGUAGE";
    public static final String CATEGORY_TOOL = I18Consts.I18N_PREFIX + "TOOL";
    public static final String CATEGORY_WRITING = I18Consts.I18N_PREFIX + "WRITING";
    
}
