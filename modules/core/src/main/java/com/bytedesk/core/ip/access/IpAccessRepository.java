/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:49:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-01 14:57:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface IpAccessRepository extends JpaRepository<IpAccessEntity, Long>, JpaSpecificationExecutor<IpAccessEntity> {
    
    Optional<IpAccessEntity> findFirstByIpAndEndpointAndAccessTimeAfter(String ip, String endpoint, ZonedDateTime time);
    
    Optional<IpAccessEntity> findByUid(String uid);
    
    Boolean existsByUid(String uid);
    
    /**
     * 原子更新访问次数和最后访问时间
     * 使用原生SQL避免乐观锁冲突
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE bytedesk_core_ip_access SET access_count = access_count + 1, " +
                   "last_access_time = :lastAccessTime, updated_at = :updatedAt " +
                   "WHERE id = :id", nativeQuery = true)
    int incrementAccessCount(@Param("id") Long id, 
                           @Param("lastAccessTime") ZonedDateTime lastAccessTime,
                           @Param("updatedAt") ZonedDateTime updatedAt);
    
    /**
     * 批量查询IP访问记录
     * 用于批量检查多个IP的访问情况
     */
    @Query("SELECT a FROM IpAccessEntity a WHERE a.ip IN :ips AND a.endpoint = :endpoint AND a.accessTime > :time")
    List<IpAccessEntity> findByIpsAndEndpointAndAccessTimeAfter(@Param("ips") List<String> ips, 
                                                               @Param("endpoint") String endpoint, 
                                                               @Param("time") ZonedDateTime time);
    
    /**
     * 统计指定IP在指定时间范围内的访问次数
     */
    @Query("SELECT COUNT(a) FROM IpAccessEntity a WHERE a.ip = :ip AND a.endpoint = :endpoint AND a.accessTime > :time")
    long countByIpAndEndpointAndAccessTimeAfter(@Param("ip") String ip, 
                                               @Param("endpoint") String endpoint, 
                                               @Param("time") ZonedDateTime time);
} 