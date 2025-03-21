/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-22 20:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 16:38:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.batch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 批处理作业控制器
 * 提供手动触发批处理作业的REST接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchJobController {
    
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    
    private final Job dailyReportJob;
    private final Job knowledgeExportJob;
    
    /**
     * 获取所有作业实例
     * 
     * @return 作业实例列表
     */
    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs() {
        try {
            Map<String, Object> result = new HashMap<>();
            // result.put("dailyReport", getJobDetails("dailyReportJob"));
            result.put("knowledgeExport", getJobDetails("knowledgeExportJob"));
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("获取作业信息失败", e);
            return ResponseEntity.ok(JsonResult.error("获取作业信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取指定作业的详细信息
     * 
     * @param jobName 作业名称
     * @return 作业详细信息
     */
    private Map<String, Object> getJobDetails(String jobName) {
        List<JobInstance> instances = jobExplorer.findJobInstancesByJobName(jobName, 0, 10);
        Map<String, Object> details = new HashMap<>();
        details.put("name", jobName);
        try {
            details.put("instanceCount", jobExplorer.getJobInstanceCount(jobName));
        } catch (org.springframework.batch.core.launch.NoSuchJobException e) {
            details.put("instanceCount", 0);
            log.warn("Job not found: {}", jobName);
        }
        details.put("recentInstances", instances);
        
        return details;
    }
    
    /**
     * 手动触发每日报表生成作业
     * 
     * @param reportDate 报表日期，格式为yyyy-MM-dd，默认为昨天
     * @return 作业执行结果
     */
    @PostMapping("/daily-report")
    public ResponseEntity<?> runDailyReportJob(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        try {
            // 如果未提供日期，默认使用昨天
            if (reportDate == null) {
                reportDate = LocalDate.now().minusDays(1);
            }
            
            String reportDateStr = reportDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            log.info("手动触发每日报表作业，报表日期: {}", reportDateStr);
            
            Map<String, JobParameter<?>> confMap = new HashMap<>();
            confMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
            confMap.put("reportDate", new JobParameter<>(reportDateStr, String.class));
            confMap.put("manual", new JobParameter<>(true, Boolean.class));
            
            JobParameters jobParameters = new JobParameters(confMap);
            
            JobExecution jobExecution = jobLauncher.run(dailyReportJob, jobParameters);
            log.info("每日报表作业已触发，执行ID: {}, 状态: {}", 
                     jobExecution.getId(), jobExecution.getStatus());
            
            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "jobExecutionId", jobExecution.getId(),
                    "status", jobExecution.getStatus().toString(),
                    "startTime", jobExecution.getStartTime(),
                    "reportDate", reportDateStr
            )));
        } catch (Exception e) {
            log.error("触发每日报表作业失败", e);
            return ResponseEntity.ok(JsonResult.error("触发作业失败: " + e.getMessage()));
        }
    }
    
    /**
     * 手动触发知识库导出作业
     * 
     * @param format 导出格式，支持csv、excel，默认为csv
     * @return 作业执行结果
     */
    @PostMapping("/knowledge-export")
    public ResponseEntity<?> runKnowledgeExportJob(
            @RequestParam(defaultValue = "csv") String format) {
        try {
            log.info("手动触发知识库导出作业，格式: {}", format);
            
            Map<String, JobParameter<?>> confMap = new HashMap<>();
            confMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
            confMap.put("format", new JobParameter<>(format, String.class));
            confMap.put("outputPath", new JobParameter<>("exports/knowledge_" + 
                                                      System.currentTimeMillis() + "." + format, String.class));
            confMap.put("manual", new JobParameter<>(true, Boolean.class));
            
            JobParameters jobParameters = new JobParameters(confMap);
            
            JobExecution jobExecution = jobLauncher.run(knowledgeExportJob, jobParameters);
            log.info("知识库导出作业已触发，执行ID: {}, 状态: {}", 
                     jobExecution.getId(), jobExecution.getStatus());
            
            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "jobExecutionId", jobExecution.getId(),
                    "status", jobExecution.getStatus().toString(),
                    "startTime", jobExecution.getStartTime(),
                    "format", format
            )));
        } catch (Exception e) {
            log.error("触发知识库导出作业失败", e);
            return ResponseEntity.ok(JsonResult.error("触发作业失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取作业执行状态
     * 
     * @param executionId 作业执行ID
     * @return 作业执行状态
     */
    @GetMapping("/status/{executionId}")
    public ResponseEntity<?> getJobStatus(@PathVariable Long executionId) {
        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
            if (jobExecution == null) {
                return ResponseEntity.ok(JsonResult.error("未找到指定的作业执行"));
            }
            
            return ResponseEntity.ok(JsonResult.success(Map.of(
                    "jobExecutionId", jobExecution.getId(),
                    "jobName", jobExecution.getJobInstance().getJobName(),
                    "status", jobExecution.getStatus().toString(),
                    "startTime", jobExecution.getStartTime(),
                    "endTime", jobExecution.getEndTime(),
                    "exitStatus", jobExecution.getExitStatus().getExitCode(),
                    "exitDescription", jobExecution.getExitStatus().getExitDescription()
            )));
        } catch (Exception e) {
            log.error("获取作业状态失败", e);
            return ResponseEntity.ok(JsonResult.error("获取作业状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 停止正在运行的作业
     * 
     * @param executionId 作业执行ID
     * @return 操作结果
     */
    @PostMapping("/stop/{executionId}")
    public ResponseEntity<?> stopJob(@PathVariable Long executionId) {
        try {
            boolean result = jobOperator.stop(executionId);
            if (result) {
                return ResponseEntity.ok(JsonResult.success("作业已停止"));
            } else {
                return ResponseEntity.ok(JsonResult.error("无法停止作业，可能已完成或不存在"));
            }
        } catch (Exception e) {
            log.error("停止作业失败", e);
            return ResponseEntity.ok(JsonResult.error("停止作业失败: " + e.getMessage()));
        }
    }
} 