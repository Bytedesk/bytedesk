/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-10 17:41:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 14:50:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 储存不同VIP等级的答案内容
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqAnswer {
    
    // VIP等级，会员等级，千人千面，根据用户等级显示不同答案
    private int vipLevel;
    
    // 针对该VIP等级的答案内容
    private String answer;
    
    // 可选：答案描述或备注
    private String description;
}
