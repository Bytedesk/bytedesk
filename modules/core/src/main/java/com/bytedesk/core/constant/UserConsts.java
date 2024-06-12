/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 12:21:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-05 12:57:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

/**
 *
 * @author xiaper.io
 */
public class UserConsts {

    // Prevents instantiation
    private UserConsts() {}

    /**
     * 通知用户
     *
     * 不能登录，仅能够作为通知用户占位符
     */
    public static String USERNAME_NOTIFICATION = "bytedesk_notification";

    /**
     * 个性签名
     */
    public static String USER_DESCRIPTION = "个性签名";

    /**
     * 客服发送欢迎语
     */
    public static final String DEFAULT_AGENT_WELCOME_TIP = "您好，有什么可以帮您的？";

    /**
     *  自动回复
     */
    public static final String AUTO_REPLY_NO = "无自动回复";
    public static final String AUTO_REPLY_EAT = "外出就餐，请稍后";
    public static final String AUTO_REPLY_LEAVE = "不在电脑旁，请稍后";
    public static final String AUTO_REPLY_BACK = "马上回来";
    public static final String AUTO_REPLY_PHONE = "接听电话中";


    // 默认组织uid
    public static final String DEFAULT_ORGANIZATION_UID = "default_organization_uid";
    public static final String DEFAULT_AGENT_UID = "default_agent_uid";
    public static final String DEFAULT_WORKGROUP_UID = "default_wg_uid";
    public static final String DEFAULT_ROBOT_UID = "default_robot_uid";
    public static final String DEFAULT_FILE_ASISTANT_UID = "default_file_asistant_uid";
    public static final String DEFAULT_SYSTEM_NOTIFICATION_UID = "default_system_notification_uid";


}


