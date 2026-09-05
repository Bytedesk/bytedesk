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
