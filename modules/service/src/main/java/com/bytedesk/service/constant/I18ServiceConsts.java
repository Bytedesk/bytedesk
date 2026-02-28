/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 10:50:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-15 13:41:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.constant;

// import com.bytedesk.core.constant.I18Consts;

public class I18ServiceConsts {
    private I18ServiceConsts() {}

    // 客服相关
    public static final String I18N_AGENT_EXISTS = "客服已存在"; // 客服已存在
    
    // 转接相关
    public static final String I18N_TRANSFER_NOTICE_TITLE = "转接通知标题"; // 转接通知标题
    public static final String I18N_TRANSFER_NOTICE_CONTENT = "转接通知内容"; // 转接通知内容
    public static final String I18N_TRANSFER_ACCEPT_NOTICE_TITLE = "接受转接通知标题"; // 接受转接通知标题
    public static final String I18N_TRANSFER_ACCEPT_NOTICE_CONTENT = "接受转接通知内容"; // 接受转接通知内容
    public static final String I18N_TRANSFER_REJECT_NOTICE_TITLE = "拒绝转接通知标题"; // 拒绝转接通知标题
    public static final String I18N_TRANSFER_REJECT_NOTICE_CONTENT = "拒绝转接通知内容"; // 拒绝转接通知内容
    public static final String I18N_TRANSFER_TIMEOUT_NOTICE_TITLE = "转接超时通知标题"; // 转接超时通知标题
    public static final String I18N_TRANSFER_TIMEOUT_NOTICE_CONTENT = "转接超时通知内容"; // 转接超时通知内容
    public static final String I18N_TRANSFER_CANCEL_NOTICE_TITLE = "取消转接通知标题"; // 取消转接通知标题
    public static final String I18N_TRANSFER_CANCEL_NOTICE_CONTENT = "取消转接通知内容"; // 取消转接通知内容
    
    // 转接状态异常
    public static final String I18N_ALREADY_IN_TRANSFER_PENDING_STATE = "已处于转接待处理状态"; // 已处于转接待处理状态
    public static final String I18N_ALREADY_IN_TRANSFER_ACCEPTED_STATE = "已处于转接已接受状态"; // 已处于转接已接受状态
    public static final String I18N_ALREADY_IN_TRANSFER_REJECTED_STATE = "已处于转接已拒绝状态"; // 已处于转接已拒绝状态
    public static final String I18N_ALREADY_IN_TRANSFER_TIMEOUT_STATE = "已处于转接已超时状态"; // 已处于转接已超时状态
    public static final String I18N_ALREADY_IN_TRANSFER_CANCELED_STATE = "已处于转接已取消状态"; // 已处于转接已取消状态
    
    // 邀请相关
    public static final String I18N_INVITE_NOTICE_TITLE = "邀请通知标题"; // 邀请通知标题
    public static final String I18N_INVITE_NOTICE_CONTENT = "邀请通知内容"; // 邀请通知内容
    public static final String I18N_INVITE_ACCEPT_NOTICE_TITLE = "接受邀请通知标题"; // 接受邀请通知标题
    public static final String I18N_INVITE_ACCEPT_NOTICE_CONTENT = "接受邀请通知内容"; // 接受邀请通知内容
    public static final String I18N_INVITE_REJECT_NOTICE_TITLE = "拒绝邀请通知标题"; // 拒绝邀请通知标题
    public static final String I18N_INVITE_REJECT_NOTICE_CONTENT = "拒绝邀请通知内容"; // 拒绝邀请通知内容
    public static final String I18N_INVITE_TIMEOUT_NOTICE_TITLE = "邀请超时通知标题"; // 邀请超时通知标题
    public static final String I18N_INVITE_TIMEOUT_NOTICE_CONTENT = "邀请超时通知内容"; // 邀请超时通知内容
    public static final String I18N_INVITE_CANCEL_NOTICE_TITLE = "取消邀请通知标题"; // 取消邀请通知标题
    public static final String I18N_INVITE_CANCEL_NOTICE_CONTENT = "取消邀请通知内容"; // 取消邀请通知内容
    public static final String I18N_INVITE_EXIT_NOTICE_TITLE = "退出邀请通知标题"; // 退出邀请通知标题
    public static final String I18N_INVITE_EXIT_NOTICE_CONTENT = "退出邀请通知内容"; // 退出邀请通知内容
    public static final String I18N_INVITE_REMOVE_NOTICE_TITLE = "移除邀请通知标题"; // 移除邀请通知标题
    public static final String I18N_INVITE_REMOVE_NOTICE_CONTENT = "移除邀请通知内容"; // 移除邀请通知内容
    
    // 邀请状态异常
    public static final String I18N_ALREADY_IN_INVITE_PENDING_STATE = "已处于邀请待处理状态"; // 已处于邀请待处理状态
    public static final String I18N_ALREADY_IN_INVITE_ACCEPTED_STATE = "已处于邀请已接受状态"; // 已处于邀请已接受状态
    public static final String I18N_ALREADY_IN_INVITE_REJECTED_STATE = "已处于邀请已拒绝状态"; // 已处于邀请已拒绝状态
    public static final String I18N_ALREADY_IN_INVITE_TIMEOUT_STATE = "已处于邀请已超时状态"; // 已处于邀请已超时状态
    public static final String I18N_ALREADY_IN_INVITE_CANCELED_STATE = "已处于邀请已取消状态"; // 已处于邀请已取消状态

    // 机器人转人工默认值
    public static final String I18N_DEFAULT_MANUAL_TRANSFER_LABEL = "转人工客服";
    public static final int I18N_DEFAULT_MIN_CONFIDENCE = 65;
    public static final int I18N_DEFAULT_MAX_ROBOT_REPLIES = 3;
    public static final int I18N_DEFAULT_AUTO_TRANSFER_DELAY_SECONDS = 0;
    public static final int I18N_DEFAULT_COOLDOWN_SECONDS = 90;

}
