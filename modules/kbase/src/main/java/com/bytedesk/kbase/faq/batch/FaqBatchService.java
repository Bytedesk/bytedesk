/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 15:58:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ批量导入服务
 * 用于启动和管理FAQ批量导入作业
 */
@Slf4j
@Service
public class FaqBatchService {

    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    @Qualifier("importFaqJob")
    private Job importFaqJob;
    
    @Autowired
    private JobExplorer jobExplorer;
    
    @Autowired
    private FaqItemProcessor faqItemProcessor;
    
    @Autowired
    private FaqExcelReader faqExcelReader;
    
    /**
     * 启动FAQ批量导入作业
     * 
     * @param filePath Excel文件路径
     * @param kbType 知识库类型
     * @param fileUid 文件UID
     * @param kbUid 知识库UID
     * @param orgUid 组织UID
     * @return 作业执行实例
     * @throws Exception 导入过程中的异常
     */
    public JobExecution importFaqFromExcel(String filePath, String kbType, String fileUid, String kbUid, String orgUid) throws Exception {
        log.info("开始导入FAQ, filePath: {}, kbType: {}, kbUid: {}", filePath, kbType, kbUid);
        
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Excel文件不存在: " + filePath);
        }
        
        // 设置读取器的文件资源
        faqExcelReader.setResource(resource);
        
        // 设置处理器的参数
        faqItemProcessor.setParameters(kbType, fileUid, kbUid, orgUid);
        
        // 创建作业参数
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", filePath)
                .addString("kbType", kbType)
                .addString("fileUid", fileUid)
                .addString("kbUid", kbUid)
                .addString("orgUid", orgUid)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        
        // 启动作业
        JobExecution jobExecution = jobLauncher.run(importFaqJob, jobParameters);
        
        log.info("FAQ导入作业启动成功, 执行ID: {}, 状态: {}", 
                jobExecution.getId(), 
                jobExecution.getStatus());
        
        return jobExecution;
    }
    
    /**
     * 检查作业执行状态
     * 
     * @param jobExecutionId 作业执行ID
     * @return 作业执行实例
     */
    public JobExecution getJobExecution(Long jobExecutionId) {
        return jobExplorer.getJobExecution(jobExecutionId);
    }
}
