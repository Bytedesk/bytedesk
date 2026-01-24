/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 08:32:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.model.CustomFieldItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String nickname;

    private String email;

    private String mobile;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // 标签列表
    private List<String> tagList;

    // 用户自定义字段：字段昵称/字段key/字段值
    private List<CustomFieldItem> customFieldList;

    // 扩展信息，JSON格式
    @Builder.Default
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 备注信息
    private String notes;

    private String visitorUid;
}
