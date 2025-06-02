/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-02 10:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 08:39:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工作组缓存管理工具类
 * 提供清除和刷新工作组缓存的功能
 */
@Slf4j
@Component
@AllArgsConstructor
public class WorkgroupCacheManager {
    
    private final CacheManager cacheManager;
    
    /**
     * 主动清除工作组缓存，保证后续的工作组查询能获取最新数据
     * 
     * @param workgroupUid 工作组UID
     */
    public void evictWorkgroupCache(String workgroupUid) {
        if (cacheManager.getCache("workgroup") != null) {
            log.info("主动清除工作组缓存: {}", workgroupUid);
            cacheManager.getCache("workgroup").evict(workgroupUid);
        } else {
            log.warn("工作组缓存不存在，无法清除: {}", workgroupUid);
        }
    }
}
