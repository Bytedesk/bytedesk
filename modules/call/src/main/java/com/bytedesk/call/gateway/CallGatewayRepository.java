/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:24:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Call网关仓库接口
 */
public interface CallGatewayRepository extends JpaRepository<CallGatewayEntity, Long>, JpaSpecificationExecutor<CallGatewayEntity> {

    Optional<CallGatewayEntity> findByUid(String uid);

    /**
     * 根据网关名称查找网关
     */
    Optional<CallGatewayEntity> findByGatewayName(String gatewayName);

    /**
     * 查找启用的网关
     */
    List<CallGatewayEntity> findByEnabledTrue();

    /**
     * 查找禁用的网关
     */
    List<CallGatewayEntity> findByEnabledFalse();

    /**
     * 根据状态查找网关
     */
    List<CallGatewayEntity> findByStatus(String status);

    /**
     * 查找在线网关
     */
    List<CallGatewayEntity> findByStatusAndEnabledTrue(String status);

    /**
     * 根据用户名查找网关
     */
    List<CallGatewayEntity> findByUsername(String username);

    /**
     * 根据代理地址查找网关
     */
    List<CallGatewayEntity> findByProxy(String proxy);

    /**
     * 根据注册标志查找网关
     */
    List<CallGatewayEntity> findByRegisterTrue();

    /**
     * 根据网关名称模糊搜索
     */
    Page<CallGatewayEntity> findByGatewayNameContainingIgnoreCase(String gatewayName, Pageable pageable);

    /**
     * 根据描述模糊搜索
     */
    Page<CallGatewayEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * 检查网关名称是否存在
     */
    boolean existsByGatewayName(String gatewayName);

    /**
     * 查找在线的启用网关
     */
    @Query("SELECT g FROM CallGatewayEntity g WHERE g.enabled = true AND g.status = 'UP'")
    List<CallGatewayEntity> findOnlineGateways();

    /**
     * 查找离线的启用网关
     */
    @Query("SELECT g FROM CallGatewayEntity g WHERE g.enabled = true AND g.status != 'UP'")
    List<CallGatewayEntity> findOfflineGateways();

    /**
     * 根据代理地址模糊查找网关
     */
    List<CallGatewayEntity> findByProxyContainingIgnoreCase(String proxy);

    /**
     * 统计启用的网关数量
     */
    long countByEnabledTrue();

    /**
     * 统计在线网关数量
     */
    @Query("SELECT COUNT(g) FROM CallGatewayEntity g WHERE g.enabled = true AND g.status = 'UP'")
    long countOnlineGateways();

    /**
     * 统计指定状态和启用状态的网关数量
     */
    long countByStatusAndEnabledTrue(String status);

    /**
     * 统计需要注册的网关数量
     */
    long countByRegisterTrue();

    /**
     * 查找指定传输协议的网关
     */
    List<CallGatewayEntity> findByRegisterTransport(String registerTransport);

    /**
     * 更新网关状态
     */
    @Query("UPDATE CallGatewayEntity g SET g.status = :status WHERE g.gatewayName = :gatewayName")
    int updateGatewayStatus(@Param("gatewayName") String gatewayName, @Param("status") String status);

    /**
     * 查找启用且需要注册的网关
     */
    List<CallGatewayEntity> findByEnabledTrueAndRegisterTrue();
}
