/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 16:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website.crawl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 抓取任务数据访问层
 */
public interface CrawlTaskRepository extends JpaRepository<CrawlTask, Long>, JpaSpecificationExecutor<CrawlTask> {
    
    /**
     * 根据任务ID查找任务
     */
    Optional<CrawlTask> findByTaskId(String taskId);
    
    /**
     * 根据网站UID查找任务
     */
    List<CrawlTask> findByWebsiteUidOrderByCreatedAtDesc(String websiteUid);
    
    /**
     * 根据状态查找任务
     */
    List<CrawlTask> findByStatusOrderByCreatedAtDesc(CrawlStatus status);
    
    /**
     * 查找正在运行的任务
     */
    @Query("SELECT t FROM CrawlTask t WHERE t.status IN ('RUNNING', 'PENDING') ORDER BY t.createdAt DESC")
    List<CrawlTask> findActiveTasks();
    
    /**
     * 根据网站UID和状态查找任务
     */
    List<CrawlTask> findByWebsiteUidAndStatusOrderByCreatedAtDesc(String websiteUid, CrawlStatus status);
    
    /**
     * 查找指定网站的最新任务
     */
    @Query("SELECT t FROM CrawlTask t WHERE t.websiteUid = :websiteUid ORDER BY t.createdAt DESC LIMIT 1")
    Optional<CrawlTask> findLatestByWebsiteUid(@Param("websiteUid") String websiteUid);
    
    /**
     * 统计指定网站的任务数量
     */
    long countByWebsiteUid(String websiteUid);
    
    /**
     * 统计指定状态的任务数量
     */
    long countByStatus(CrawlStatus status);
    
    /**
     * 删除指定网站的所有任务
     */
    void deleteByWebsiteUid(String websiteUid);
}
