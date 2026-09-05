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

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 系统全局配置响应
 * 
 * /query 由 SystemConfigKeyEnum 枚举合成完整清单（无需种子数据），
 * 每项包含：默认值（来自 properties 静态配置）、覆盖值（来自 DB）、当前生效值与来源。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /** 配置键 */
    private String key;

    /** 分组：BRAND / AGREEMENT */
    private String group;

    /** 值类型：STRING / BOOLEAN / INTEGER / JSON / TEXT */
    private String valueType;

    /** 展示名 */
    private String displayName;

    /** 配置项说明 */
    private String description;

    /** 默认值（来自 properties/compose 静态配置，可能为 null） */
    private String defaultValue;

    /** 覆盖值（来自 DB，未设置时为 null） */
    private String overrideValue;

    /** 当前生效值：覆盖值非空 ? 覆盖值 : 默认值 */
    private String effectiveValue;

    /** 生效来源：DB（后台覆盖）/ DEFAULT（静态默认） */
    private String source;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否启用自定义品牌（仅 custom.enabled 项返回，前端用于联动提示；其余项为 null） */
    private Boolean customBrandEnabled;

    /** 生效来源常量 */
    public static final String SOURCE_DB = "DB";
    public static final String SOURCE_DEFAULT = "DEFAULT";
}
