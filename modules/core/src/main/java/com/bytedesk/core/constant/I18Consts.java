/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 22:25:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 10:06:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

// 国际化常量
public class I18Consts {

    private I18Consts() {
    }

    public static final String I18N_PREFIX = "i18n.";
    // 
    public static final String I18N_USERNAME_OR_PASSWORD_INCORRECT = I18N_PREFIX + "username.or.password.incorrect";
    public static final String I18N_MOBILE_ALREADY_EXISTS = I18N_PREFIX + "mobile.already.exists";
    public static final String I18N_EMAIL_ALREADY_EXISTS = I18N_PREFIX + "email.already.exists";
    public static final String I18N_MOBILE_NOT_EXISTS = I18N_PREFIX + "mobile.not.exists";
    public static final String I18N_EMAIL_NOT_EXISTS = I18N_PREFIX + "email.not.exists";
    public static final String I18N_MOBILE_FORMAT_ERROR = I18N_PREFIX + "mobile.format.error";
    public static final String I18N_EMAIL_FORMAT_ERROR = I18N_PREFIX + "email.format.error";
    // captcha
    public static final String I18N_AUTH_CAPTCHA_SEND_SUCCESS = I18N_PREFIX + "auth.captcha.send.success";
    public static final String I18N_AUTH_CAPTCHA_ERROR = I18N_PREFIX + "auth.captcha.error";
    public static final String I18N_AUTH_CAPTCHA_EXPIRED = I18N_PREFIX + "auth.captcha.expired";
    public static final String I18N_AUTH_CAPTCHA_ALREADY_SEND = I18N_PREFIX + "auth.captcha.already.send";
    public static final String I18N_AUTH_CAPTCHA_VALIDATE_FAILED = I18N_PREFIX + "auth.captcha.validate.failed";
    // "文件助手"
    public static final String I18N_FILE_ASSISTANT_NAME = I18N_PREFIX + "file.assistant";
    public static final String I18N_CLIPBOARD_ASSISTANT_NAME = I18N_PREFIX + "clipboard.assistant";
    // "意图改写"
    public static final String I18N_INTENT_REWRITE_ASSISTANT_NAME = I18N_PREFIX + "intent.rewrite.assistant";
    // "意图识别"
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_NAME = I18N_PREFIX + "intent.classification.assistant";
    // "情绪分析"
    public static final String I18N_EMOTION_ASSISTANT_NAME = I18N_PREFIX + "emotion.assistant";
    // "手机、电脑文件互传"
    public static final String I18N_FILE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "file.assistant.description";
    public static final String I18N_CLIPBOARD_ASSISTANT_DESCRIPTION = I18N_PREFIX + "clipboard.assistant.description";
    // "意图改写"
    public static final String I18N_INTENT_REWRITE_ASSISTANT_DESCRIPTION = I18N_PREFIX + "intent.rewrite.assistant.description";
    // "意图识别"
    public static final String I18N_INTENT_CLASSIFICATION_ASSISTANT_DESCRIPTION = I18N_PREFIX + "intent.classification.assistant.description";
    // "情绪分析"
    public static final String I18N_EMOTION_ASSISTANT_DESCRIPTION = I18N_PREFIX + "emotion.assistant.description";
    // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_NAME = I18N_PREFIX + "system.notification";
    public static final String I18N_SYSTEM_NOTIFICATION_DESCRIPTION = I18N_PREFIX + "system.notification.description";
    // user
    public static final String I18N_USER_OLD_PASSWORD_WRONG = I18N_PREFIX + "old.password.wrong";
    //
    public static final String I18N_THREAD_CONTENT_IMAGE = I18N_PREFIX + "thread.content.image";
    public static final String I18N_THREAD_CONTENT_FILE = I18N_PREFIX + "thread.content.file";
    public static final String I18N_THREAD_CONTENT_AUDIO = I18N_PREFIX + "thread.content.audio";
    public static final String I18N_THREAD_CONTENT_VIDEO = I18N_PREFIX + "thread.content.video";
    //
    public static final String I18N_WELCOME_TIP = I18N_PREFIX + "welcome.tip";
    public static final String I18N_TOP_TIP = I18N_PREFIX + "top.tip";
    public static final String I18N_LEAVEMSG_TIP = I18N_PREFIX + "leavemsg.tip";
    public static final String I18N_REENTER_TIP = I18N_PREFIX + "reenter.tip";
    public static final String I18N_QUEUE_TIP = I18N_PREFIX + "queue.tip";
    public static final String I18N_QUEUE_MESSAGE_TEMPLATE = I18N_PREFIX + "queue.message.template"; // 新增的国际化常量
    public static final String I18N_AUTO_CLOSE_TIP = I18N_PREFIX + "auto.close.tip"; // 自动关闭提示
    public static final String I18N_AGENT_CLOSE_TIP = I18N_PREFIX + "agent.close.tip"; // 客服关闭提示
    public static final String I18N_AGENT_TRANSFER_TIP = I18N_PREFIX + "agent.transfer.tip"; // 客服转接提示
    // invite rate tip
    public static final String I18N_INVITE_RATE_TIP = I18N_PREFIX + "invite.rate.tip";
    // unified
    public static final String I18N_UNIFIED_NICKNAME = I18N_PREFIX + "unified.nickname";
    public static final String I18N_UNIFIED_DESCRIPTION = I18N_PREFIX + "unified.description";
    //
    public static final String I18N_WORKGROUP_NICKNAME = I18N_PREFIX + "workgroup.nickname";
    public static final String I18N_WORKGROUP_BEFORE_NICKNAME = I18N_PREFIX + "workgroup.before.nickname";
    public static final String I18N_WORKGROUP_AFTER_NICKNAME = I18N_PREFIX + "workgroup.after.nickname";
    public static final String I18N_WORKGROUP_DESCRIPTION = I18N_PREFIX + "workgroup.description";
    public static final String I18N_WORKGROUP_BEFORE_DESCRIPTION = I18N_PREFIX + "workgroup.before.description";
    public static final String I18N_WORKGROUP_AFTER_DESCRIPTION = I18N_PREFIX + "workgroup.after.description";
    //
    public static final String I18N_AGENT_NICKNAME = I18N_PREFIX + "agent.nickname";
    public static final String I18N_AGENT_DESCRIPTION = I18N_PREFIX + "agent.description";
    public static final String I18N_AGENT_OFFLINE = I18N_PREFIX + "agent.offline";
    public static final String I18N_AGENT_UNAVAILABLE = I18N_PREFIX + "agent.unavailable";
    public static final String I18N_AGENT_AVAILABLE = I18N_PREFIX + "agent.available";
    //
    public static final String I18N_USER_NICKNAME = I18N_PREFIX + "user.nickname";
    public static final String I18N_USER_DESCRIPTION = I18N_PREFIX + "user.description";
    public static final String I18N_USER_NOT_FOUND = I18N_PREFIX + "user.not.found";
    public static final String I18N_CREATE_FAILED = I18N_PREFIX + "create.failed";
    //
    public static final String I18N_DESCRIPTION = I18N_PREFIX + "description";
    public static final String I18N_TODO = I18N_PREFIX + "todo";
    //
    public static final String I18N_ROBOT_NICKNAME = I18N_PREFIX + "robot.nickname";
    public static final String I18N_ROBOT_NAME = I18N_PREFIX + "robot.name";
    public static final String I18N_ROBOT_DESCRIPTION = I18N_PREFIX + "robot.description";
    public static final String I18N_ROBOT_AGENT_ASSISTANT_NICKNAME = I18N_PREFIX + "robot.agent.assistant.nickname";
    // "llm.prompt";
    public static final String I18N_ROBOT_REPLY = I18N_PREFIX + "robot.reply";
    public static final String I18N_ROBOT_NO_REPLY = I18N_PREFIX + "robot.noreply";
    //
    public static final String I18N_ADMIN = I18N_PREFIX + "admin";
    public static final String I18N_ADMIN_DESCRIPTION = I18N_PREFIX + "admin.description";
    public static final String I18N_MEMBER = I18N_PREFIX + "member";
    public static final String I18N_MEMBER_DESCRIPTION = I18N_PREFIX + "member.description";
    // quick reply category
    public static final String I18N_QUICK_REPLY_CATEGORY_CONTACT = I18N_PREFIX + "contact"; // 询问联系方式
    public static final String I18N_QUICK_REPLY_CATEGORY_THANKS = I18N_PREFIX + "thanks"; // 感谢
    public static final String I18N_QUICK_REPLY_CATEGORY_WELCOME = I18N_PREFIX + "welcome"; // 问候
    public static final String I18N_QUICK_REPLY_CATEGORY_BYE = I18N_PREFIX + "bye"; // 告别
    //
    public static final String I18N_QUICK_REPLY_CONTACT_TITLE = I18N_PREFIX + "contact.title";//
    public static final String I18N_QUICK_REPLY_CONTACT_CONTENT = I18N_PREFIX + "contact.content";//
    //
    public static final String I18N_QUICK_REPLY_THANKS_TITLE = I18N_PREFIX + "thanks.title";//
    public static final String I18N_QUICK_REPLY_THANKS_CONTENT = I18N_PREFIX + "thanks.content";//
    //
    public static final String I18N_QUICK_REPLY_WELCOME_TITLE = I18N_PREFIX + "welcome.title";//
    public static final String I18N_QUICK_REPLY_WELCOME_CONTENT = I18N_PREFIX + "welcome.content";//
    //
    public static final String I18N_QUICK_REPLY_BYE_TITLE = I18N_PREFIX + "bye.title";//
    public static final String I18N_QUICK_REPLY_BYE_CONTENT = I18N_PREFIX + "bye.content";//
    //
    public static final String I18N_VIP_REST_API = I18N_PREFIX + "vip.api";
    // used for init organization
    public static final String I18N_FAQ_CATEGORY_DEMO_1 = I18N_PREFIX + "faq.category.demo.1";
    public static final String I18N_FAQ_CATEGORY_DEMO_2 = I18N_PREFIX + "faq.category.demo.2";
    //
    public static final String I18N_FAQ_DEMO_QUESTION_1 = I18N_PREFIX + "faq.demo.title.1";
    public static final String I18N_FAQ_DEMO_ANSWER_1 = I18N_PREFIX + "faq.demo.content.1";
    public static final String I18N_FAQ_DEMO_QUESTION_2 = I18N_PREFIX + "faq.demo.title.2";
    public static final String I18N_FAQ_DEMO_ANSWER_2 = I18N_PREFIX + "faq.demo.content.2";
    //
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_1 = I18N_PREFIX + "quick.button.demo.title.1";
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_1 = I18N_PREFIX + "quick.button.demo.content.1";
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_2 = I18N_PREFIX + "quick.button.demo.title.2";
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_2 = I18N_PREFIX + "quick.button.demo.content.2";
    //
    public static final String I18N_GROUP_NAME = I18N_PREFIX + "group.name";
    public static final String I18N_GROUP_DESCRIPTION = I18N_PREFIX + "group.description";
    //
    public static final String I18N_NOTICE_TITLE = I18N_PREFIX + "notice.title";
    public static final String I18N_NOTICE_TYPE = I18N_PREFIX + "notice.type";
    public static final String I18N_NOTICE_CONTENT = I18N_PREFIX + "notice.content";
    public static final String I18N_NOTICE_IP = I18N_PREFIX + "notice.ip";
    public static final String I18N_NOTICE_IP_LOCATION = I18N_PREFIX + "notice.ipLocation";
    public static final String I18N_NOTICE_URL = I18N_PREFIX + "notice.url";
    public static final String I18N_NOTICE_EXTRA = I18N_PREFIX + "notice.extra";
    // 
    public static final String I18N_NOTICE_PARSE_FILE_SUCCESS = I18N_PREFIX + "notice.parse.file.success";
    public static final String I18N_NOTICE_PARSE_FILE_ERROR = I18N_PREFIX + "notice.parse.file.error";
    // 
    public static final String I18N_AUTO_CLOSED = I18N_PREFIX + "auto.closed";
    public static final String I18N_AGENT_CLOSED = I18N_PREFIX + "agent.closed";
    public static final String I18N_AGENT_TRANSFER = I18N_PREFIX + "agent.transfer";
    // 
    public static final String I18N_BLACK_USER_ALREADY_EXISTS = I18N_PREFIX + "black.user.already.exists";
    // typing
    public static final String I18N_TYPING = I18N_PREFIX + "typing";
    
    

}
