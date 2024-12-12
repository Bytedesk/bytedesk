/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:40:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 11:16:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.quality;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bytedesk.service.quality.dto.QualityInspectionDTO;
import com.bytedesk.service.quality.dto.QualityRuleDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface QualityService {

    // 质检管理
    QualityInspectionEntity createInspection(String threadUid, String inspectorUid);
    QualityInspectionEntity updateInspection(Long inspectionId, QualityInspectionDTO dto);
    void deleteInspection(Long inspectionId);
    QualityInspectionEntity getInspection(Long inspectionId);
    
    // 质检查询
    Page<QualityInspectionEntity> getInspections(Pageable pageable);
    Page<QualityInspectionEntity> getInspectionsByAgent(String agentUid, Pageable pageable);
    Page<QualityInspectionEntity> getInspectionsByInspector(String inspectorUid, Pageable pageable);
    
    // 质检统计
    Double getAverageScore(String agentUid, LocalDateTime startTime, LocalDateTime endTime);
    Map<String,Double> getScoreDistribution(String agentUid);
    List<QualityInspectionEntity> getLowScoreInspections(Integer threshold);
    
    // 规则管理
    QualityRuleEntity createRule(QualityRuleDTO dto);
    QualityRuleEntity updateRule(Long ruleId, QualityRuleDTO dto);
    void deleteRule(Long ruleId);
    List<QualityRuleEntity> getRules();
    
    // 自动质检
    void autoInspectThread(String threadUid);
    void scheduleAutoInspection();
} 