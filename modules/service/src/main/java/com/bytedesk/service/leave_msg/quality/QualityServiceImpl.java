/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:40:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 11:18:06
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.leave_msg.quality.dto.QualityInspectionDTO;
import com.bytedesk.service.leave_msg.quality.dto.QualityRuleDTO;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class QualityServiceImpl implements QualityService {

    @Autowired
    private QualityInspectionRepository inspectionRepository;
    
    @Autowired
    private QualityRuleRepository ruleRepository;

    @Override
    @Transactional
    public QualityInspectionEntity createInspection(String threadUid, String inspectorUid) {
        QualityInspectionEntity inspection = new QualityInspectionEntity();
        inspection.setThreadUid(threadUid);
        inspection.setInspectorUid(inspectorUid);
        inspection.setStatus("pending");
        
        return inspectionRepository.save(inspection);
    }

    @Override
    @Transactional
    public void autoInspectThread(String threadUid) {
        // 1. 获取会话内容
        // ThreadEntity thread = threadService.getThread(threadUid);
        
        // 2. 获取适用的质检规则
        // List<QualityRuleEntity> rules = ruleRepository.findByEnabledTrue();
        
        // 3. 应用规则进行评分
        int totalScore = 0;
        // for (QualityRuleEntity rule : rules) {
            // totalScore += applyRule(rule, thread);
        // }
        
        // 4. 创建质检记录
        QualityInspectionEntity inspection = new QualityInspectionEntity();
        inspection.setThreadUid(threadUid);
        inspection.setScore(totalScore);
        inspection.setStatus("completed");
        
        inspectionRepository.save(inspection);
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    @Override
    public void scheduleAutoInspection() {
        // 获取昨天的会话列表
        // List<ThreadEntity> threads = threadService.getThreadsByDate(yesterday);
        
        // 批量执行自动质检
        // threads.forEach(thread -> autoInspectThread(thread.getUid()));
    }

    public int applyRule(QualityRuleEntity rule, ThreadEntity thread) {
        int score = 100;
        
        // 1. 检查响应时间
        if (rule.getMinResponseTime() != null) {
            long responseTime = calculateResponseTime(thread);
            if (responseTime > rule.getMinResponseTime()) {
                score -= 10;
            }
        }
        
        // 2. 检查禁用词
        if (rule.getForbiddenWords() != null) {
            String[] words = rule.getForbiddenWords().split(",");
            for (String word : words) {
                if (thread.getContent().contains(word)) {
                    score -= 5;
                }
            }
        }
        
        // 3. 检查必用词
        if (rule.getRequiredWords() != null) {
            String[] words = rule.getRequiredWords().split(",");
            for (String word : words) {
                if (!thread.getContent().contains(word)) {
                    score -= 5;
                }
            }
        }
        
        return score;
    }

    @Override
    public QualityInspectionEntity updateInspection(Long inspectionId, QualityInspectionDTO dto) {
        QualityInspectionEntity inspection = getInspection(inspectionId);
        
        inspection.setScore(dto.getScore());
        inspection.setResponseScore(dto.getResponseScore());
        inspection.setAttitudeScore(dto.getAttitudeScore());
        inspection.setSolutionScore(dto.getSolutionScore());
        inspection.setStandardScore(dto.getStandardScore());
        inspection.setComment(dto.getComment());
        inspection.setStatus("completed");
        
        return inspectionRepository.save(inspection);
    }

    @Override
    public void deleteInspection(Long inspectionId) {
        inspectionRepository.deleteById(inspectionId);
    }

    @Override
    public QualityInspectionEntity getInspection(Long inspectionId) {
        return inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new RuntimeException("Inspection not found"));
    }

    @Override
    public Page<QualityInspectionEntity> getInspections(Pageable pageable) {
        return inspectionRepository.findAll(pageable);
    }

    @Override
    public Page<QualityInspectionEntity> getInspectionsByAgent(String agentUid, Pageable pageable) {
        return inspectionRepository.findByAgentUid(agentUid, pageable);
    }

    @Override
    public Page<QualityInspectionEntity> getInspectionsByInspector(String inspectorUid, Pageable pageable) {
        return inspectionRepository.findByInspectorUid(inspectorUid, pageable);
    }

    @Override
    public Double getAverageScore(String agentUid, LocalDateTime startTime, LocalDateTime endTime) {
        return inspectionRepository.getAverageScore(agentUid, startTime, endTime);
    }

    @Override
    public Map<String, Double> getScoreDistribution(String agentUid) {
        Map<String, Double> distribution = new HashMap<>();
        distribution.put("response", getAverageScoreByType(agentUid, "response_score"));
        distribution.put("attitude", getAverageScoreByType(agentUid, "attitude_score"));
        distribution.put("solution", getAverageScoreByType(agentUid, "solution_score"));
        distribution.put("standard", getAverageScoreByType(agentUid, "standard_score"));
        return distribution;
    }

    @Override
    public List<QualityInspectionEntity> getLowScoreInspections(Integer threshold) {
        return inspectionRepository.findLowScoreInspections(threshold);
    }

    // 规则管理相关方法
    @Override
    public QualityRuleEntity createRule(QualityRuleDTO dto) {
        QualityRuleEntity rule = new QualityRuleEntity();
        updateRuleFromDTO(rule, dto);
        return ruleRepository.save(rule);
    }

    @Override
    public QualityRuleEntity updateRule(Long ruleId, QualityRuleDTO dto) {
        QualityRuleEntity rule = ruleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Rule not found"));
        updateRuleFromDTO(rule, dto);
        return ruleRepository.save(rule);
    }

    @Override
    public void deleteRule(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    @Override
    public List<QualityRuleEntity> getRules() {
        return ruleRepository.findAll();
    }

    private void updateRuleFromDTO(QualityRuleEntity rule, QualityRuleDTO dto) {
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setCategoryUid(dto.getCategoryUid());
        rule.setMinResponseTime(dto.getMinResponseTime());
        rule.setMinSolutionTime(dto.getMinSolutionTime());
        rule.setForbiddenWords(dto.getForbiddenWords());
        rule.setRequiredWords(dto.getRequiredWords());
        rule.setScoreRules(dto.getScoreRules());
        rule.setEnabled(dto.getEnabled());
    }

    private Double getAverageScoreByType(String agentUid, String scoreType) {
        // TODO: 实现各维度平均分统计
        return 0.0;
    }

    private long calculateResponseTime(ThreadEntity thread) {
        // TODO: 实现响应时间计算
        return 0;
    }

} 