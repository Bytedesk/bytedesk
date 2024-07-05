/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 22:25:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-01 15:56:20
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

// 国际化常量
public class I18Consts {

    private I18Consts() {
    }

    // public static final String EN = "en";
    // public static final String ZH = "zh";
    //
    // public static final String EN_US = "en-US";
    // public static final String ZH_CN = "zh-CN";
    // public static final String ZH_TW = "zh-TW";

    public static final String I18N_PREFIX = "i18n.";
    // "文件助手"
    public static final String I18N_FILE_ASISTANT_NAME = I18N_PREFIX + "file.asistant";
    // "手机、电脑文件互传"
    public static final String I18N_FILE_ASISTANT_DESCRIPTION = I18N_PREFIX + "file.asistant.description";
    // 系统通知
    public static final String I18N_SYSTEM_NOTIFICATION_NAME = I18N_PREFIX + "system.notification";
    public static final String I18N_SYSTEM_NOTIFICATION_DESCRIPTION = I18N_PREFIX + "system.notification.description";
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
    //
    public static final String I18N_WORKGROUP_NICKNAME = I18N_PREFIX + "workgroup.nickname";
    public static final String I18N_WORKGROUP_DESCRIPTION = I18N_PREFIX + "workgroup.description";
    //
    public static final String I18N_AGENT_NICKNAME = I18N_PREFIX + "agent.nickname";
    public static final String I18N_AGENT_DESCRIPTION = I18N_PREFIX + "agent.description";
    public static final String I18N_AGENT_OFFLINE = I18N_PREFIX + "agent.offline";
    public static final String I18N_AGENT_UNAVAILABLE = I18N_PREFIX + "agent.unavailable";
    public static final String I18N_AGENT_AVAILABLE = I18N_PREFIX + "agent.available";
    //
    public static final String I18N_USER_NICKNAME = I18N_PREFIX + "user.nickname";
    public static final String I18N_USER_DESCRIPTION = I18N_PREFIX + "user.description";
    //
    public static final String I18N_DESCRIPTION = I18N_PREFIX + "description";
    //
    public static final String I18N_ROBOT_NICKNAME = I18N_PREFIX + "robot.nickname";
    public static final String I18N_ROBOT_DESCRIPTION = I18N_PREFIX + "robot.description";
    public static final String I18N_ROBOT_LLM_PROMPT = I18N_PREFIX + "llm.prompt";
    //
    public static final String I18N_ADMIN = I18N_PREFIX + "admin";
    public static final String I18N_ADMIN_DESCRIPTION = I18N_PREFIX + "admin.description";
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
    // userd for init organization
    public static final String I18N_FAQ_CATEGORY_DEMO_1 = I18N_PREFIX + "faq.category.demo.1";
    public static final String I18N_FAQ_CATEGORY_DEMO_2 = I18N_PREFIX + "faq.category.demo.2";
    //
    public static final String I18N_FAQ_DEMO_TITLE_1 = I18N_PREFIX + "faq.demo.title.1";
    public static final String I18N_FAQ_DEMO_CONTENT_1 = I18N_PREFIX + "faq.demo.content.1";
    public static final String I18N_FAQ_DEMO_TITLE_2 = I18N_PREFIX + "faq.demo.title.2";
    public static final String I18N_FAQ_DEMO_CONTENT_2 = I18N_PREFIX + "faq.demo.content.2";
    //
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_1 = I18N_PREFIX + "quick.button.demo.title.1";
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_1 = I18N_PREFIX + "quick.button.demo.content.1";
    public static final String I18N_QUICK_BUTTON_DEMO_TITLE_2 = I18N_PREFIX + "quick.button.demo.title.2";
    public static final String I18N_QUICK_BUTTON_DEMO_CONTENT_2 = I18N_PREFIX + "quick.button.demo.content.2";
    // 
    public static final String I18N_GROUP_NAME = I18N_PREFIX + "group.name";
    public static final String I18N_GROUP_DESCRIPTION = I18N_PREFIX + "group.description";

}
