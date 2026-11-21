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
package com.bytedesk.core.system_config.enums;

import com.bytedesk.core.system_config.SystemConfigConsts;

/**
 * 系统全局配置受控 key 清单
 * 
 * 扩展方式：新增全局配置项 = 在此枚举加一条（含 key/类型/分组/展示名/说明/排序），
 * 后端 /query 自动合成完整清单，前端表单按元数据自动渲染，无需改表、无需新接口。
 * 
 * 默认回退来源为 bytedesk.custom.* 静态配置（properties / docker compose 环境变量 BYTEDESK_CUSTOM_*）。
 */
public enum SystemConfigKeyEnum {

    // ============ 品牌外观（BRAND） ============
    CUSTOM_ENABLED(
            SystemConfigConsts.KEY_CUSTOM_ENABLED,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.BRAND,
            "启用自定义品牌",
            "关闭后，名称/logo/favicon/描述等品牌设置将不生效，回退系统默认",
            1),
    CUSTOM_NAME(
            SystemConfigConsts.KEY_CUSTOM_NAME,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.BRAND,
            "系统名称",
            "管理后台左上角标题与浏览器标签页标题",
            2),
    CUSTOM_LOGO(
            SystemConfigConsts.KEY_CUSTOM_LOGO,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.BRAND,
            "系统 Logo",
            "管理后台左上角 Logo 图片，支持填写 URL 或直接上传（需为完整 http(s) 地址）",
            3),
    CUSTOM_FAVICON(
            SystemConfigConsts.KEY_CUSTOM_FAVICON,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.BRAND,
            "网站图标 Favicon",
            "浏览器标签页图标，为空时默认使用 Logo，支持填写 URL 或直接上传",
            4),
    CUSTOM_DESCRIPTION(
            SystemConfigConsts.KEY_CUSTOM_DESCRIPTION,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.BRAND,
            "系统描述",
            "系统副标题/描述信息",
            5),

    // ============ 协议与合规（AGREEMENT） ============
    CUSTOM_PRIVACY_POLICY_URL(
            SystemConfigConsts.KEY_CUSTOM_PRIVACY_POLICY_URL,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.AGREEMENT,
            "隐私协议链接",
            "登录页面《用户隐私协议》跳转地址（完整 URL）",
            1),
    CUSTOM_TERMS_OF_SERVICE_URL(
            SystemConfigConsts.KEY_CUSTOM_TERMS_OF_SERVICE_URL,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.AGREEMENT,
            "用户协议链接",
            "登录页面《用户服务协议》跳转地址（完整 URL）",
            2),

    // ============ 平台客服（PLATFORM_SERVICE） ============
    PLATFORM_SERVICE_ENABLED(
            SystemConfigConsts.KEY_PLATFORM_SERVICE_ENABLED,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.PLATFORM_SERVICE,
            "启用平台客服入口",
            "开启后访客聊天页工具栏显示「平台客服」按钮，用于用户-商户纠纷时联系平台介入",
            1),
    PLATFORM_SERVICE_ORG_UID(
            SystemConfigConsts.KEY_PLATFORM_SERVICE_ORG_UID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.PLATFORM_SERVICE,
            "平台客服组织 UID",
            "平台客服所属组织，默认 df_org_uid",
            2),
    PLATFORM_SERVICE_WORKGROUP_UID(
            SystemConfigConsts.KEY_PLATFORM_SERVICE_WORKGROUP_UID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.PLATFORM_SERVICE,
            "平台客服工作组 UID",
            "访客联系的工作组，须属于上述组织，默认 df_wg_uid",
            3),

    // ============ 大模型问答（AI） ============
    AI_DISABLE_QA(
            SystemConfigConsts.KEY_AI_DISABLE_QA,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.AI,
            "禁用大模型问答",
            "开启后全平台访客咨询直接返回固定回复，不再进入大模型问答（用于平台审核等场景）",
            1),
    AI_DISABLE_QA_REPLY(
            SystemConfigConsts.KEY_AI_DISABLE_QA_REPLY,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.AI,
            "固定回复内容",
            "禁用大模型问答时，回复给访客的固定文案；留空则使用系统内置默认文案",
            2),

    // ============ 推送（PUSH） ============
    PUSH_ALIYUN_ENABLED(
            SystemConfigConsts.KEY_PUSH_ALIYUN_ENABLED,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.PUSH,
            "启用阿里云推送",
            "移动端登录后通过鉴权接口拉取推送凭据的总开关；关闭后移动端跳过阿里云推送初始化",
            1),
    PUSH_ALIYUN_ANDROID_APP_KEY(
            SystemConfigConsts.KEY_PUSH_ALIYUN_ANDROID_APP_KEY,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.PUSH,
            "Android AppKey",
            "阿里云 EMAS Android 应用 AppKey（应用级凭据，非服务端 OpenAPI 的 AccessKey）",
            2),
    PUSH_ALIYUN_ANDROID_APP_SECRET(
            SystemConfigConsts.KEY_PUSH_ALIYUN_ANDROID_APP_SECRET,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.PUSH,
            "Android AppSecret",
            "阿里云 EMAS Android 应用 AppSecret；加密存储，保存后掩码显示，留空表示不修改",
            3),
    PUSH_ALIYUN_IOS_APP_KEY(
            SystemConfigConsts.KEY_PUSH_ALIYUN_IOS_APP_KEY,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.PUSH,
            "iOS AppKey（预留）",
            "阿里云 EMAS iOS 应用 AppKey，当前 iOS 走自建 APNs，此配置位预留",
            4),
    PUSH_ALIYUN_IOS_APP_SECRET(
            SystemConfigConsts.KEY_PUSH_ALIYUN_IOS_APP_SECRET,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.PUSH,
            "iOS AppSecret（预留）",
            "阿里云 EMAS iOS 应用 AppSecret；加密存储，保存后掩码显示，留空表示不修改（预留）",
            5),

    // ============ 微信公众号模板消息（WECHAT_MP） ============
    WECHAT_MP_APP_ID(
            SystemConfigConsts.KEY_WECHAT_MP_APP_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "公众号 AppID",
            "发送模板消息使用的微信公众号 AppID，需已在「渠道-微信-公众号」中配置",
            1),
    WECHAT_MP_LOGIN_NOTICE_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_LOGIN_NOTICE_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "登录成功通知模板 ID",
            "扫码登录成功通知的模板 ID 兕底值；优先使用后台模板库中用途标记为「登录成功通知」的模板",
            2),
    WECHAT_MP_MINI_PROGRAM_APP_ID(
            SystemConfigConsts.KEY_WECHAT_MP_MINI_PROGRAM_APP_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "小程序 AppID",
            "模板消息点击后跳转的小程序 AppID，须与公众号完成绑定关联，否则微信会拒绝发送",
            3),
    WECHAT_MP_MINI_PROGRAM_PAGE_PATH(
            SystemConfigConsts.KEY_WECHAT_MP_MINI_PROGRAM_PAGE_PATH,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "小程序打开路径",
            "点击模板消息跳转小程序后打开的页面路径，如 /pages/thread/thread-list",
            4),
    WECHAT_MP_BIND_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_BIND_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "绑定成功通知模板 ID",
            "公众号绑定成功通知模板消息的模板 ID，在公众号后台模板库中获取",
            5),
    WECHAT_MP_UNBIND_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_UNBIND_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "解绑成功通知模板 ID",
            "公众号解绑成功通知模板消息的模板 ID，在公众号后台模板库中获取",
            6),
    WECHAT_MP_RECHARGE_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_RECHARGE_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "充值成功通知模板 ID",
            "充值成功通知模板消息的模板 ID，在公众号后台模板库中获取",
            7),
    WECHAT_MP_RECHARGE_REMIND_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_RECHARGE_REMIND_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "续费提醒通知模板 ID",
            "服务即将到期续费提醒模板消息的模板 ID，在公众号后台模板库中获取",
            8),
    WECHAT_MP_PAYMENT_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_PAYMENT_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "续费成功通知模板 ID",
            "续费成功通知模板消息的模板 ID，在公众号后台模板库中获取",
            9),
    WECHAT_MP_STATISTIC_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_STATISTIC_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "客服日报统计模板 ID",
            "每日定时推送客服统计日报的模板 ID，点击可跳转小程序查看更多数据",
            10),
    WECHAT_MP_RATE_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_RATE_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "满意度评价提醒模板 ID",
            "会话结束邀请客户评价满意度的模板 ID，在公众号后台模板库中获取",
            11),
    WECHAT_MP_LEAVE_MSG_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_LEAVE_MSG_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "留言跟单提醒模板 ID",
            "客户留言后提醒客服跟进的模板 ID，在公众号后台模板库中获取",
            12),
    WECHAT_MP_TICKET_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_TICKET_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "工单跟单提醒模板 ID",
            "新工单提醒客服跟进的模板 ID，在公众号后台模板库中获取",
            13),
    WECHAT_MP_FEEDBACK_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_FEEDBACK_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "意见反馈提醒模板 ID",
            "新意见反馈提醒客服跟进的模板 ID，在公众号后台模板库中获取",
            14),
    WECHAT_MP_LOGIN_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_LOGIN_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "登录提醒模板 ID",
            "账号登录安全提醒模板消息的模板 ID（非扫码登录成功通知），在公众号后台模板库中获取",
            15),
    WECHAT_MP_NEW_VISITOR_TEMPLATE_ID(
            SystemConfigConsts.KEY_WECHAT_MP_NEW_VISITOR_TEMPLATE_ID,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.WECHAT_MP,
            "新访客接待提醒模板 ID",
            "新访客咨询提醒客服接待的模板 ID，在公众号后台模板库中获取",
            16),

    // ============ 文件上传（UPLOAD） ============
    UPLOAD_DIR(
            SystemConfigConsts.KEY_UPLOAD_DIR,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.UPLOAD,
            "本地上传目录",
            "本地磁盘存储目录（相对或绝对路径，末尾不要带 '/'），仅存储类型为「本地磁盘」时生效；修改后新文件写入新目录，历史文件不迁移",
            1),
    UPLOAD_URL(
            SystemConfigConsts.KEY_UPLOAD_URL,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.UPLOAD,
            "上传文件访问地址",
            "本地上传文件的访问地址（服务器实际地址，如 http://192.168.0.194:9003），用于拼接文件下载 URL",
            2),

    // ============ 上传安全（UPLOAD_SECURITY） ============
    UPLOAD_SECURITY_MAX_FILE_SIZE(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_MAX_FILE_SIZE,
            SystemConfigValueTypeEnum.INTEGER,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "最大文件大小（字节）",
            "允许上传的最大文件大小，单位字节（如 100MB = 104857600）；同时受 multipart 限制约束，实际生效值为两者较小值",
            1),
    UPLOAD_SECURITY_ENABLE_IMAGE_VALIDATION(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_IMAGE_VALIDATION,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "启用图片内容验证",
            "开启后对图片文件做内容校验，拦截损坏或伪造扩展名的图片",
            2),
    UPLOAD_SECURITY_ENABLE_FILE_NAME_FILTER(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_FILE_NAME_FILTER,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "启用文件名过滤",
            "开启后对上传文件名做危险字符过滤",
            3),
    UPLOAD_SECURITY_FORCE_RENAME(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_FORCE_RENAME,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "强制重命名文件",
            "开启后上传文件统一按时间戳重命名，避免重名覆盖与特殊字符问题",
            4),
    UPLOAD_SECURITY_MAX_FILE_NAME_LENGTH(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_MAX_FILE_NAME_LENGTH,
            SystemConfigValueTypeEnum.INTEGER,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "文件名最大长度",
            "上传文件名的最大字符长度",
            5),
    UPLOAD_SECURITY_ENABLE_UPLOAD_LOG(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_UPLOAD_LOG,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "记录上传日志",
            "开启后记录文件上传日志，用于审计追溯",
            6),
    UPLOAD_SECURITY_ENABLE_VIRUS_SCAN(
            SystemConfigConsts.KEY_UPLOAD_SECURITY_ENABLE_VIRUS_SCAN,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.UPLOAD_SECURITY,
            "启用病毒扫描（预留）",
            "预留接口，当前版本开启后不会实际扫描",
            7),

    // ============ 对象存储（STORAGE，企业版 S3 兼容） ============
    STORAGE_PROVIDER(
            SystemConfigConsts.KEY_STORAGE_PROVIDER,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.STORAGE,
            "存储类型",
            "local：本地磁盘（社区版默认）；s3：S3 兼容对象存储（MinIO/RustFS/阿里云 OSS/腾讯云 COS 等，企业版功能）。切换不会自动迁移历史文件",
            1),
    STORAGE_ENDPOINT(
            SystemConfigConsts.KEY_STORAGE_ENDPOINT,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.STORAGE,
            "服务地址 Endpoint",
            "S3 兼容服务地址，须同时能被应用和用户浏览器访问，如 http://127.0.0.1:19000",
            2),
    STORAGE_ACCESS_KEY(
            SystemConfigConsts.KEY_STORAGE_ACCESS_KEY,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.STORAGE,
            "AccessKey",
            "对象存储访问凭据 AccessKey（部分后端为 Username）",
            3),
    STORAGE_SECRET_KEY(
            SystemConfigConsts.KEY_STORAGE_SECRET_KEY,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.STORAGE,
            "SecretKey",
            "对象存储访问凭据 SecretKey（部分后端为 Password）；加密存储，保存后掩码显示，留空表示不修改",
            4),
    STORAGE_BUCKET_NAME(
            SystemConfigConsts.KEY_STORAGE_BUCKET_NAME,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.STORAGE,
            "存储桶 Bucket",
            "存储桶名称，不存在时启动/生效时自动创建",
            5),
    STORAGE_REGION(
            SystemConfigConsts.KEY_STORAGE_REGION,
            SystemConfigValueTypeEnum.STRING,
            SystemConfigGroupEnum.STORAGE,
            "区域 Region",
            "对象存储区域，如 us-east-1",
            6),
    STORAGE_SECURE(
            SystemConfigConsts.KEY_STORAGE_SECURE,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.STORAGE,
            "启用 HTTPS",
            "endpoint 为 https:// 时自动走 HTTPS，此开关供 S3 客户端强制会话加密",
            7),
    STORAGE_PUBLIC_READ(
            SystemConfigConsts.KEY_STORAGE_PUBLIC_READ,
            SystemConfigValueTypeEnum.BOOLEAN,
            SystemConfigGroupEnum.STORAGE,
            "桶公开读",
            "true：设置桶公开读策略并使用直链；false：跳过策略，使用有时限的预签名 URL。改为 false 不会撤销存储端已有的公开读策略",
            8),
    STORAGE_PRESIGNED_EXPIRY_DAYS(
            SystemConfigConsts.KEY_STORAGE_PRESIGNED_EXPIRY_DAYS,
            SystemConfigValueTypeEnum.INTEGER,
            SystemConfigGroupEnum.STORAGE,
            "预签名 URL 有效期（天）",
            "仅「桶公开读」关闭时生效，SigV4 协议上限 7 天，超出自动截断为 7",
            9),
    ;

    private final String key;
    private final SystemConfigValueTypeEnum valueType;
    private final SystemConfigGroupEnum group;
    private final String displayName;
    private final String description;
    private final int sortOrder;

    SystemConfigKeyEnum(String key, SystemConfigValueTypeEnum valueType, SystemConfigGroupEnum group,
            String displayName, String description, int sortOrder) {
        this.key = key;
        this.valueType = valueType;
        this.group = group;
        this.displayName = displayName;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public String getKey() {
        return key;
    }

    public SystemConfigValueTypeEnum getValueType() {
        return valueType;
    }

    public SystemConfigGroupEnum getGroup() {
        return group;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * 按 key 字符串查找枚举项，未注册返回 null
     */
    public static SystemConfigKeyEnum fromKey(String key) {
        if (key == null) {
            return null;
        }
        for (SystemConfigKeyEnum item : values()) {
            if (item.key.equals(key)) {
                return item;
            }
        }
        return null;
    }
}
