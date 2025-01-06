/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:40:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 14:07:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg.quality;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_service_quality_rule")
@EqualsAndHashCode(callSuper = true)
public class QualityRuleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "category_uid")
    private String categoryUid;  // 适用的分类
    
    @Column(name = "min_response_time")
    private Integer minResponseTime;  // 最低响应时间(秒)
    
    @Column(name = "min_solution_time")
    private Integer minSolutionTime;  // 最低解决时间(秒)
    
    @Column(name = "forbidden_words")
    private String forbiddenWords;    // 禁用词,逗号分隔
    
    @Column(name = "required_words")
    private String requiredWords;     // 必用词,逗号分隔
    
    @Column(name = "score_rules", columnDefinition = "TEXT")
    private String scoreRules;        // 评分规则(JSON)
    
    private Boolean enabled = true;
} 