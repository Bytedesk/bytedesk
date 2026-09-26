/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-04 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-04 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.system_config;

/**
 * 系统全局配置常量
 */
public class SystemConfigConsts {

    /**
     * 平台全局配置使用的 orgUid（复用默认组织常量，避免发明新哨兵值）
     */
    public static final String PLATFORM_CONFIG_ORG_UID = com.bytedesk.core.constant.BytedeskConsts.DEFAULT_ORGANIZATION_UID;

    /**
     * Redis 缓存名（TTL 在 RedisCacheConfig 中注册）
     */
    public static final String CACHE_NAME_SYSTEM_CONFIG = "system_config";

    // ============ 受控 key（与 SystemConfigKeyEnum 保持一致） ============
    public static final String KEY_CUSTOM_ENABLED = "custom.enabled";
    public static final String KEY_CUSTOM_NAME = "custom.name";
    public static final String KEY_CUSTOM_LOGO = "custom.logo";
    public static final String KEY_CUSTOM_FAVICON = "custom.favicon";
    public static final String KEY_CUSTOM_DESCRIPTION = "custom.description";
    public static final String KEY_CUSTOM_PRIVACY_POLICY_URL = "custom.privacyPolicyUrl";
    public static final String KEY_CUSTOM_TERMS_OF_SERVICE_URL = "custom.termsOfServiceUrl";

    // ============ 大模型问答（AI） ============
    public static final String KEY_AI_DISABLE_QA = "ai.disableQa";
    public static final String KEY_AI_DISABLE_QA_REPLY = "ai.disableQaReply";

    // ============ 平台客服（PLATFORM_SERVICE） ============
    public static final String KEY_PLATFORM_SERVICE_ENABLED = "platform_service.enabled";
    public static final String KEY_PLATFORM_SERVICE_ORG_UID = "platform_service.orgUid";
    public static final String KEY_PLATFORM_SERVICE_WORKGROUP_UID = "platform_service.workgroupUid";

    // ============ 推送（PUSH，移动端 EMAS 应用凭据） ============
    public static final String KEY_PUSH_ALIYUN_ENABLED = "push.aliyun.enabled";
    public static final String KEY_PUSH_ALIYUN_ANDROID_APP_KEY = "push.aliyun.android.appKey";
    public static final String KEY_PUSH_ALIYUN_ANDROID_APP_SECRET = "push.aliyun.android.appSecret";
    public static final String KEY_PUSH_ALIYUN_IOS_APP_KEY = "push.aliyun.ios.appKey";
    public static final String KEY_PUSH_ALIYUN_IOS_APP_SECRET = "push.aliyun.ios.appSecret";

    // ============ 微信公众号模板消息（WECHAT_MP，WeChatMpTemplateService 常量可配置化） ============
    public static final String KEY_WECHAT_MP_APP_ID = "wechat_mp.appId";
    public static final String KEY_WECHAT_MP_LOGIN_NOTICE_TEMPLATE_ID = "wechat_mp.loginNoticeTemplateId";
    public static final String KEY_WECHAT_MP_MINI_PROGRAM_APP_ID = "wechat_mp.miniProgramAppId";
    public static final String KEY_WECHAT_MP_MINI_PROGRAM_PAGE_PATH = "wechat_mp.miniProgramPagePath";
    public static final String KEY_WECHAT_MP_BIND_TEMPLATE_ID = "wechat_mp.bindTemplateId";
    public static final String KEY_WECHAT_MP_UNBIND_TEMPLATE_ID = "wechat_mp.unbindTemplateId";
    public static final String KEY_WECHAT_MP_RECHARGE_TEMPLATE_ID = "wechat_mp.rechargeTemplateId";
    public static final String KEY_WECHAT_MP_RECHARGE_REMIND_TEMPLATE_ID = "wechat_mp.rechargeRemindTemplateId";
    public static final String KEY_WECHAT_MP_PAYMENT_TEMPLATE_ID = "wechat_mp.paymentTemplateId";
    public static final String KEY_WECHAT_MP_STATISTIC_TEMPLATE_ID = "wechat_mp.statisticTemplateId";
    public static final String KEY_WECHAT_MP_RATE_TEMPLATE_ID = "wechat_mp.rateTemplateId";
    public static final String KEY_WECHAT_MP_LEAVE_MSG_TEMPLATE_ID = "wechat_mp.leaveMsgTemplateId";
    public static final String KEY_WECHAT_MP_TICKET_TEMPLATE_ID = "wechat_mp.ticketTemplateId";
    public static final String KEY_WECHAT_MP_FEEDBACK_TEMPLATE_ID = "wechat_mp.feedbackTemplateId";
    public static final String KEY_WECHAT_MP_LOGIN_TEMPLATE_ID = "wechat_mp.loginTemplateId";
    public static final String KEY_WECHAT_MP_NEW_VISITOR_TEMPLATE_ID = "wechat_mp.newVisitorTemplateId";

    private SystemConfigConsts() {
    }
}
