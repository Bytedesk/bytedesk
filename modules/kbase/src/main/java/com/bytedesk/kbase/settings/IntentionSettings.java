/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29 14:23:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:02:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IntentionSettings implements Serializable {
    
    /**
     * 意图分类选项
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "intention_list", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> intentionList = new ArrayList<>(Arrays.asList(
        "产品咨询",
        "价格查询",
        "订单查询",
        "投诉反馈",
        "售后服务",
        "退换货申请",
        "支付问题",
        "账号问题",
        "技术支持",
        "功能建议",
        "物流查询",
        "优惠活动",
        "其他"
    ));
    
}
