/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-27 10:55:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-07 14:47:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

public class RedisConsts {

    private RedisConsts() {
    }

    public static final String ACCESS_TOKEN_PREFIX = "bytedeskim:access_token:";
    //
    public static final String VALIDATE_CODE = "bytedeskim:validate_code:";
    public static final String REGISTE_IP = "bytedeskim:registe_ip:";
    public static final String REGISTE_IP_VISITOR = "bytedeskim:registe_ip_visitor:";

    //
    // 长链接
    public static final String CONNECTED_PREFIX = "bytedeskim:connected:";

    // 分类
    public static final String CATEGORY_PREFIX = "bytedeskim:category:";

    // 常用语
    public static final String CUW_CATEGORY_PREFIX = "bytedeskim:cuw:category:";

    // 机器人
    public static final String ANSWER_WORKGROUP_PREFIX = "bytedeskim:answer:workgroup:";
    public static final String ANSWER_PREFIX = "bytedeskim:answer:";

    // 自动回复
    public static final String AUTO_REPLY_CONTENT_PREFIX = "bytedeskim:autoreply:content:";
    public static final String AUTO_REPLY_IMAGEURL_PREFIX = "bytedeskim:autoreply:imageurl:";
    public static final String AUTO_REPLY_USERNAME_PREFIX = "bytedeskim:autoreply:username:";
    public static final String AUTO_REPLY_NICKNAME_PREFIX = "bytedeskim:autoreply:nickname:";
    public static final String AUTO_REPLY_AVATAR_PREFIX = "bytedeskim:autoreply:avatar:";

    // 黑名单
    public static final String BLOCK = "bytedeskim:block";
    public static final String BLOCK_WORKGROUP_PREFIX = "bytedeskim:block:workgroup:"; // 技能组黑名单
    public static final String BLOCK_AGENT_PREFIX = "bytedeskim:block:agent:"; // 指定客服黑名单
    //

    // token
    public static final String DEVICE_TOKEN_LSB_SCHOOL_DEBUG_PREFIX = "bytedeskim:device_token_lsb_school:debug:";
    public static final String DEVICE_TOKEN_LSB_SCHOOL_RELEASE_PREFIX = "bytedeskim:device_token_lsb_school:release:";
    public static final String DEVICE_TOKEN_LSB_SCHOOL_RELEASE_WID_PREFIX = "bytedeskim:device_token_wid_lsb_school:release:";

    //
    public static final String DEVICE_TOKEN_LSB_DEBUG_PREFIX = "bytedeskim:device_token_lsb:debug:";
    public static final String DEVICE_TOKEN_LSB_RELEASE_PREFIX = "bytedeskim:device_token_lsb:release:";
    public static final String DEVICE_TOKEN_LSB_RELEASE_WID_PREFIX = "bytedeskim:device_token_wid_lsb:release:";

    // 推送
    public static final String WEB_ENDPOINT_PREFIX = "bytedeskim:webpush:endpoint:";
    public static final String WEB_KEY_PREFIX = "bytedeskim:webpush:key:";
    public static final String WEB_AUTH_PREFIX = "bytedeskim:webpush:auth:";
    public static final String DEVICE_TOKEN_DEBUG_PREFIX = "bytedeskim:device_token:debug:";
    public static final String DEVICE_TOKEN_RELEASE_PREFIX = "bytedeskim:device_token:release:";
    public static final String DEVICE_TOKEN_RELEASE_WID_PREFIX = "bytedeskim:device_token_wid:release:";
    // 取消推送
    public static final String CANCEL_WECHAT_MP_PUSH = "bytedeskim:cancel_wechat_mp_push"; // 取消推送微信公众号提示
    public static final String CANCEL_EMAIL_PUSH = "bytedeskim:cancel_emai_push"; // 取消推送邮箱消息

    //
    public static final String USER_HOST_PREFIX = "bytedeskim:user:host:";
    public static final String HOST_PREFIX = "bytedeskim:host";

    // 路由分配
    public static final String ROUND_ROBIN_PREFIX = "bytedeskim:round:robin:"; // 轮询分配
    public static final String ROUTING_AVERAGE_PREFIX = "bytedeskim:routing:average:"; // 当日接待数最少优先
    public static final String ROUTING_LESS_PREFIX = "bytedeskim:routing:less:"; // 当前进行中对话数量最少优先

    //
    public static final String DELAY_DISCONNECT_PREFIX = "bytedeskim:delay:disconnect:";

    // 技能组
    public static final String WORKGROUP_PREFIX = "bytedeskim:workgroup:"; // 技能组缓存
    public static final String WORKGROUP_WEBHOOK_PREFIX = "bytedeskim:workgroup:webhook:"; // webhook相关
    public static final String WORKGROUP_WEBHOOK_LEAVE_MESSAGE_PREFIX = "bytedeskim:workgroup:webhookleavemessage:";
    public static final String WORKGROUP_AGENT_PREFIX = "bytedeskim:workgroup:agent:"; // 技能组所含客服
    public static final String WORKGROUP_OUT_OF_DATE = "bytedeskim:workgroup:outofdate"; // 已过期技能组
    public static final String WORKGROUP_ADMIN_PREFIX = "bytedeskim:workgroup:admin:"; // 管理员所有技能组
    public static final String WORKGROUP_SUBDOMAIN_PREFIX = "bytedeskim:workgroup:subdomain:"; // 技能组对应subdomain

    // 微信客服
    public static final String BIND_WECHAT_KEFU_UID = "bytedeskim:bind:wechatkefuuid";
    public static final String WECHAT_KEFU_SUITETICKET = "bytedeskim:wechatkefu:suiteticket";
    public static final String WECHAT_KEFU_SUITE_ACCESS_TOKEN = "bytedeskim:wechatkefu:suiteaccesstoken";
    public static final String WECHAT_KEFU_PRE_AUTH_CODE = "bytedeskim:wechatkefu:preauthcode";
    public static final String WECHAT_KEFU_ACCESSTOKEN = "bytedeskim:wechatkefu:accesstoken:";
    public static final String WECHAT_KEFU_NEXTCURSOR = "bytedeskim:wechatkefu:nextcursor:";
    public static final String WECHAT_KEFU_OPENKFID = "bytedeskim:wechatkefu:openkfid:";
    public static final String WECHAT_KEFU_CORPID_DOMAIM = "bytedeskim:wechatkefu:corpidsubdomain:";
    public static final String WECHAT_KEFU_VISITOR_INFO_PREFIX = "bytedeskim:wechatkefu:visitor:";
    public static final String WECHAT_KEFU_AGENT_INFO_PREFIX = "bytedeskim:wechatkefu:agent:";

    // 微信小程序
    public static final String WECHAT_MINI_ACCESSTOKEN = "bytedeskim:wechatmini:accesstoken:";
    public static final String WECHAT_MINI_VISITOR_INFO_PREFIX = "bytedeskim:wechatmini:visitor:";
    public static final String WECHAT_MINI_SUB_DOMAIM = "bytedeskim:wechatmini:subdomain:";
    public static final String WECHAT_MINI_INFO_PREFIX = "bytedeskim:wechatmini:info:";

    // 微信公众号、微信开放平台
    public static final String WECHAT_MP_ACCESSTOKEN = "bytedeskim:wechat:accesstoken:";
    public static final String WECHAT_COMPONENT_VERIFY_TICKET = "bytedeskim:wechat:componentverifyticket:";
    public static final String WECHAT_COMPONENT_ACCESS_TOKEN = "bytedeskim:wechat:componentaccesstoken:";
    public static final String WECHAT_PRE_AUTH_CODE = "bytedeskim:wechat:preauthcode:";
    public static final String WECHAT_VISITOR_INFO_PREFIX = "bytedeskim:wechat:visitor:";
    public static final String WECHAT_SUB_DOMAIM = "bytedeskim:wechat:subdomain:";
    public static final String WECHAT_INFO_PREFIX = "bytedeskim:wechat:info:";

    // 企业微信
    public static final String BIND_WECHAT_WORK_UID = "bytedeskim:bind:wechatworkuid";
    public static final String WECHAT_WORK_SUITETICKET = "bytedeskim:wechatwork:suiteticket";
    public static final String WECHAT_WORK_MINI_SUITETICKET = "bytedeskim:wechatworkmini:suiteticket";
    public static final String WECHAT_WORK_SUITE_ACCESS_TOKEN = "bytedeskim:wechatwork:suiteaccesstoken";
    public static final String WECHAT_WORK_MINI_SUITE_MINI_ACCESS_TOKEN = "bytedeskim:wechatworkmini:suiteaccesstoken";
    public static final String WECHAT_WORK_PRE_AUTH_CODE = "bytedeskim:wechatwork:preauthcode";
    public static final String WECHAT_WORK_MINI_PRE_AUTH_CODE_MINI = "bytedeskim:wechatworkmini:preauthcode";
    public static final String WECHAT_WORK_ACCESSTOKEN = "bytedeskim:wechatwork:accesstoken:";
    public static final String WECHAT_WORK_ACCESSTOKEN_MINI = "bytedeskim:wechatworkmini:accesstoken:";
    public static final String WECHAT_WORK_NEXTCURSOR = "bytedeskim:wechatwork:nextcursor:";
    public static final String WECHAT_WORK_AGENTUID = "bytedeskim:wechatwork:agentuid:";
    public static final String WECHAT_WORK_OPENKFID = "bytedeskim:wechatwork:openkfid:";
    public static final String WECHAT_WORK_JSAPITICKET = "bytedeskim:wechatwork:jsapiticket:";
    public static final String WECHAT_WORK_CODE = "bytedeskim:wechatwork:code:";

    // 用户
    public static final String USER_DISABLED = "bytedeskim:user:disabled"; // "bytedeskim:user:enabled";
    public static final String USER_AGENT = "bytedeskim:user:agent";
    public static final String USER_VISITOR = "bytedeskim:user:visitor";
    public static final String USER_NO_ACCEPT_STATUS = "bytedeskim:user:no_accept_status";
    public static final String USER_NO_WEBRTC_STATUS = "bytedeskim:user:no_webrtc_status";
    public static final String USER_TOPIC_PREFIX = "bytedeskim:user:topic:";
    public static final String USER_WORKGROUP_PREFIX = "bytedeskim:user:workgroup:";
    public static final String USER_INFO_PREFIX = "bytedeskim:user:info:";
    public static final String USER_ADMIN_PREFIX = "bytedeskim:user:admin:"; // 管理员下属所有客服账号
    public static final String AGENT_ADMIN_PREFIX = "bytedeskim:agent:admin:"; // 客服对应的管理员信息

    public static final String VISITOR_INFO_PREFIX = "bytedeskim:visitor:info:";
    public static final String VISITOR_ID_PREFIX = "bytedeskim:visitor:id:";
    public static final String VISITOR_USERNAME_PREFIX = "bytedeskim:visitor:username:";
    public static final String USER_THREAD_AGENT_PREFIX = "bytedeskim:user:thread:agent:";
    public static final String USER_STOMP_USERNAME_UID_PREFIX = "bytedeskim:user:stomp:username:";
    public static final String USER_RECENT_PREFIX = "bytedeskim:user:recent:";
    public static final String API_PREFIX = "bytedeskim:api";
    public static final String SCAN_PREFIX = "bytedeskim:scan:";
    public static final String MINI_QRCODE_PREFIX = "bytedeskim:miniqrcode:";
    public static final String QUEUE_STATISTIC_PREFIX = "bytedeskim:queue:statistic:";
    public static final String THREAD_STATISTIC_PREFIX = "bytedeskim:thread:statistic:";
    public static final String USER_MAXTHREADCOUNT_PREFIX = "bytedeskim:user:maxthreadcount:";
    public static final String USER_WEBHOOK_PREFIX = "bytedeskim:user:webhook:";
    public static final String USER_WEBHOOK_LEAVE_MESSAGE_PREFIX = "bytedeskim:user:webhookleavemessage:";

    // 机器人缓存信息
    public static final String ROBOT_INFO_PREFIX = "bytedeskim:robot:info:";
    public static final String ROBOT_ADMIN_PREFIX = "bytedeskim:robot:admin:";

    // URL
    public static final String SHORT_URL = "bytedeskim:shorturl:";
    public static final String LONG_URL = "bytedeskim:longurl:";

    //
    // 工作组当前会话数
    public static final String WORKGROUP_THREAD_COUNT_PREFIX = "bytedeskim:workgroup:thread:count:";
    // 某个客服账号当前会话数
    public static final String AGENT_THREAD_COUNT = "bytedeskim:agent:thread:count";
    // 会话对应的客服
    public static final String THREAD_AGENT_MAP = "bytedeskim:thread:agent:map:";
    // 机器人自动获客用语
    public static final String THREAD_ROBOT_COLLECT_MOBILE = "bytedeskim:thread:robot_collect_mobile:";
    // 会话免打扰
    public static final String THREAD_NODISTURB = "bytedeskim:thread:nodisturb";
    public static final String USER_THREAD_WORKGROUP_PREFIX = "bytedeskim:user:thread:workgroup:";
    public static final String USER_THREAD_CHANNEL_PREFIX = "bytedeskim:user:thread:channel:";
    public static final String USER_THREAD_FILEHELPER_PREFIX = "bytedeskim:user:thread:filehelper:";
    public static final String USER_THREAD_ZHIPUAI_PREFIX = "bytedeskim:user:thread:zhipuai:";
    public static final String THREAD_PREFIX = "bytedeskim:user:thread:";
    // 会话对应的URL
    public static final String THREAD_URL_PREFIX = "bytedeskim:thread:url:";
    public static final String THREAD_PREURL_PREFIX = "bytedeskim:thread:preurl:";
    public static final String THREAD_URLTITLE_PREFIX = "bytedeskim:thread:urltitle:";
    //
    public static final String USER_THREAD_FOR_CLOSE = "bytedeskim:user:thread:forclose2";
    public static final String USER_THREAD_FOR_VISITOR_PREFIX = "bytedeskim:user:thread:forvisitor:";
    public static final String USER_THREAD_FOR_VISITOR_UID_PREFIX = "bytedeskim:user:thread:forvisitoruid:";
    // public static final String USER_STOMP_USERNAME_UID_PREFIX =
    // "bytedeskim:user:stomp:username:";
    // 缓存当前客服所有进行中会话
    public static final String AGENT_THREAD_PREFIX = "bytedeskim:agent:thread:";
    public static final String AGENT_THREAD_TOP_PREFIX = "bytedeskim:agent:thread:top:";
    public static final String AGENT_THREAD_STAR_PREFIX = "bytedeskim:agent:thread:star:";
    public static final String AGENT_THREAD_HISTORY_PREFIX = "bytedeskim:agent:thread:history:";

    // 敏感词
    public static final String TABOO = "bytedeskim:taboo";

    // 设置
    public static final String SETTINGS_PREVIEW = "bytedeskim:settings:preview";
    public static final String SETTINGS_CONNECTED = "bytedeskim:settings:connected";
    public static final String SETTINGS_DISCONNECTED = "bytedeskim:settings:disconnected";
    public static final String SETTINGS_HISTORY_MESSAGES = "bytedeskim:settings:historymessages";
    public static final String SETTINGS_AGENT_HISTORY_MESSAGES = "bytedeskim:settings:agenthistorymessages";
    public static final String SETTINGS_WECHAT_PAY = "bytedeskim:settings:wechatpay";
    public static final String SETTINGS_ALI_PAY = "bytedeskim:settings:alipay";
    public static final String SETTINGS_ZHAOBIAO = "bytedeskim:settings:zhaobiao";
    public static final String SETTINGS_MEIYU = "bytedeskim:settings:meiyu";

    // 排队
    public static final String QUEUE_PREFIX = "bytedeskim:queue:";
    // public static final String QUEUE_USER_WORKGROUP_PREFIX =
    // "bytedeskim:user:queue:workgroup:";
    public static final String QUEUE_WORKGROUP_PREFIX = "bytedeskim:queue:workgroup:";

    // 指纹
    public static final String FINGERPRINT2_PREFIX = "bytedeskim:fingerPrint2:";

    // 快捷按钮
    public static final String QUICKBUTTON_PREFIX = "bytedeskim:quickbutton:";

    // 转人工自定义词
    public static final String TRANSFERWORD_PREFIX = "bytedeskim:transferWord:";

    // 推送
    public static final String PUSH_TOKEN_LBS_XM_PREFIX = "bytedeskim:push_token:lbs:xm";
    public static final String PUSH_TOKEN_LBS_HW_PREFIX = "bytedeskim:push_token:lbs:hw";
    public static final String PUSH_TOKEN_LS_XM_PREFIX = "bytedeskim:push_token:ls:xm";
    public static final String PUSH_TOKEN_LS_HW_PREFIX = "bytedeskim:push_token:ls:hw";
    public static final String PUSH_TOKEN_LS_SCHOOL_XM_PREFIX = "bytedeskim:push_token:lsschool:xm";
    public static final String PUSH_TOKEN_LS_SCHOOL_HW_PREFIX = "bytedeskim:push_token:lsschool:hw";

    // openid
    public static final String USER_MP_OPENID_PREFIX = "bytedeskim:user:mpopenid:";
    public static final String USER_MP_OPENID_WORKGROUP_PREFIX = "bytedeskim:user:mpopenid:workgroup:";
    public static final String USER_MINI_OPENID_PREFIX = "bytedeskim:user:miniopenid:";
    public static final String USER_MINI_OPENID_WORKGROUP_PREFIX = "bytedeskim:user:miniopenid:workgroup:";

    // menu
    public static final String MENU_PREFIX = "bytedeskim:menu:";

    // login
    // 通过开放平台绑定公众号、小程序
    public static final String BIND_WECHAT_UID = "bytedeskim:bind:wechatuid";
    public static final String BIND_WECHAT_URL = "bytedeskim:bind:wechaturl";
    // 验证码
    public static final String CODE = "code:";
    public static final String CACHE_REGISTER_IP = "cacheRegisterIP:";
    public static final String CACHE_VISITOR_REGISTER_IP = "cacheVisitorRegisterIP:";

    // 绑定lazada店铺
    public static final String BIND_LAZADA_UID = "bytedeskim:bind:lazadauid";
    public static final String BIND_LAZADA_URL = "bytedeskim:bind:lazadaurl";

    //
    public static final String LEAVE_MESSAGE_SETTING_APPOINTED_EMAIL_PREFIX = "bytedeskim:leavemsg:appointed:email:";
    public static final String LEAVE_MESSAGE_SETTING_APPOINTED_MOBILE_PREFIX = "bytedeskim:leavemsg:appointed:mobile:";
    public static final String LEAVE_MESSAGE_SETTING_WORKGROUP_EMAIL_PREFIX = "bytedeskim:leavemsg:workgroup:email:";
    public static final String LEAVE_MESSAGE_SETTING_WORKGROUP_MOBILE_PREFIX = "bytedeskim:leavemsg:workgroup:mobile:";

    // 智谱AI token
    public static final String ROBOT_ZHIPU_TOKEN = "bytedeskim:robot:zhipu:token";
    public static final String ROBOT_ZHIPU_WHITELIST = "bytedeskim:robot:zhipu:whitelist"; // 白名单
    public static final String ROBOT_ZHIPU_MESSAGE_USER = "bytedeskim:robot:zhipu:messageuser"; // 缓存发送过问题的用户
    public static final String ROBOT_ZHIPU_MESSAGE_COUNT = "bytedeskim:robot:zhipu:messagecount"; // 缓存用户发送问题的次数

    // 招标
    public static final String ZHAOBIAO_SPIDER_URL = "zhaobiao:spiderurl";
    public static final String ZHAOBIAO_SPIDER_SUMMARY_URL = "zhaobiao:spidersummaryurl";
    // 51voa
    public static final String VOA_SPIDER_URL = "voa:spiderurl";
    public static final String VOA_SPIDER_DETAIL_URL = "voa:spiderdetailurl";

    // redis pub/sub
    public static final String REDIS_PUBSUB_CHANNEL_TOPIC = "redispubsubchannel:message";
    public static final String REDIS_PUBSUB_CHANNEL_TOPIC_OBJECT = "redispubsubchannel:messageobject";
    public static final String REDIS_PUBSUB_CHANNEL_TOPIC_RESPONSE = "redispubsubchannel:messageresponse";

}
