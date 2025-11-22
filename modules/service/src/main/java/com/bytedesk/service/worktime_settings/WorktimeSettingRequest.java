/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:16:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime_settings;

import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class WorktimeSettingRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    @lombok.Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 是否启用该工作时间设置
     */
    private Boolean enabled;

    /**
     * 常规工作时间段列表（例如周一~周五的周期性工作时间）
     */
    private List<WorktimeSlotValue> regularWorktimes;

    /**
     * 特殊工作时间段列表（节假日、活动期间等覆盖性时间段）
     */
    private List<WorktimeSlotValue> specialWorktimes;

    /**
     * 节假日配置，存为 JSON 文本（key=date, value=name）
     */
    private String holidays;

    // @Builder.Default
    // private String type = WorktimeSettingTypeEnum.CUSTOMER.name();

}
