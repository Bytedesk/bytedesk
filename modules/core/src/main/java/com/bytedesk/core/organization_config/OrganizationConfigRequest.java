/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-14 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.organization_config;

import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 组织级配置请求
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationConfigRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /** 配置键（save 时校验必须为 OrganizationConfigKeyEnum 注册过的 key） */
    private String key;

    /** 配置值（blank = 删除覆盖记录，恢复系统级默认值） */
    private String value;

    /** 目标组织（可选；默认当前登录用户所属组织，仅 SUPER 可指定他人组织） */
    private String orgUid;

    /** 批量保存条目 */
    private List<OrganizationConfigItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationConfigItem {

        private String key;

        private String value;
    }
}
