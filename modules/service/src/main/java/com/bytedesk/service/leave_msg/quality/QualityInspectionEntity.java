/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:40:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 14:08:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
@Table(name = "bytedesk_service_quality_inspection")
@EqualsAndHashCode(callSuper = true)
public class QualityInspectionEntity extends BaseEntity {

    @Column(name = "thread_uid", nullable = false)
    private String threadUid;  // 会话ID
    
    @Column(name = "agent_uid", nullable = false)
    private String agentUid;   // 客服ID
    
    @Column(name = "inspector_uid", nullable = false)
    private String inspectorUid; // 质检员ID
    
    @Column(nullable = false)
    private Integer score;      // 总分(0-100)
    
    @Column(name = "response_score")
    private Integer responseScore;  // 响应速度得分
    
    @Column(name = "attitude_score") 
    private Integer attitudeScore;  // 服务态度得分
    
    @Column(name = "solution_score")
    private Integer solutionScore;  // 解决方案得分
    
    @Column(name = "standard_score")
    private Integer standardScore;  // 服务规范得分
    
    private String comment;     // 质检评语
    
    private String status = "pending"; // pending, completed
} 