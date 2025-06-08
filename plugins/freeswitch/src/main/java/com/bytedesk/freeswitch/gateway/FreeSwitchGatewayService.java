/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.gateway;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * FreeSwitch网关服务类
 * 处理SIP网关配置、状态监控、连接管理等业务逻辑
 */
@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchGatewayService {

    private final FreeSwitchGatewayRepository gatewayRepository;

    /**
     * 创建新网关
     */
    @Transactional
    public FreeSwitchGatewayEntity createGateway(String gatewayName, String description, String proxy,
                                               String username, String password, String fromUser,
                                               String fromDomain, Boolean register, String registerTransport) {
        FreeSwitchGatewayEntity gateway = FreeSwitchGatewayEntity.builder()
                .gatewayName(gatewayName)
                .description(description)
                .proxy(proxy)
                .username(username)
                .password(password)
                .fromUser(fromUser)
                .fromDomain(fromDomain)
                .register(register != null ? register : true)
                .registerTransport(registerTransport != null ? registerTransport : "udp")
                .enabled(true)
                .status("DOWN")
                .build();
        
        FreeSwitchGatewayEntity saved = gatewayRepository.save(gateway);
        log.info("创建网关: {} (ID: {}) -> {}", gatewayName, saved.getId(), proxy);
        return saved;
    }

    /**
     * 根据ID查找网关
     */
    public Optional<FreeSwitchGatewayEntity> findById(Long id) {
        return gatewayRepository.findById(id);
    }

    /**
     * 根据名称查找网关
     */
    public Optional<FreeSwitchGatewayEntity> findByGatewayName(String gatewayName) {
        return gatewayRepository.findByGatewayName(gatewayName);
    }

    /**
     * 获取所有启用的网关
     */
    public List<FreeSwitchGatewayEntity> findEnabledGateways() {
        return gatewayRepository.findByEnabledTrue();
    }

    /**
     * 获取所有网关（分页）
     */
    public Page<FreeSwitchGatewayEntity> findAll(Pageable pageable) {
        return gatewayRepository.findAll(pageable);
    }

    /**
     * 根据状态查找网关
     */
    public List<FreeSwitchGatewayEntity> findByStatus(String status) {
        return gatewayRepository.findByStatus(status);
    }

    /**
     * 更新网关信息
     */
    @Transactional
    public FreeSwitchGatewayEntity updateGateway(Long id, String description, String proxy,
                                               String username, String password, String fromUser,
                                               String fromDomain, Boolean register, String registerTransport) {
        FreeSwitchGatewayEntity gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("网关不存在: " + id));

        if (description != null) gateway.setDescription(description);
        if (proxy != null) gateway.setProxy(proxy);
        if (username != null) gateway.setUsername(username);
        if (password != null) gateway.setPassword(password);
        if (fromUser != null) gateway.setFromUser(fromUser);
        if (fromDomain != null) gateway.setFromDomain(fromDomain);
        if (register != null) gateway.setRegister(register);
        if (registerTransport != null) gateway.setRegisterTransport(registerTransport);

        FreeSwitchGatewayEntity saved = gatewayRepository.save(gateway);
        log.info("更新网关: {} (ID: {})", gateway.getGatewayName(), id);
        return saved;
    }

    /**
     * 启用网关
     */
    @Transactional
    public void enableGateway(Long id) {
        FreeSwitchGatewayEntity gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("网关不存在: " + id));
        
        gateway.setEnabled(true);
        gatewayRepository.save(gateway);
        log.info("启用网关: {} (ID: {})", gateway.getGatewayName(), id);
    }

    /**
     * 禁用网关
     */
    @Transactional
    public void disableGateway(Long id) {
        FreeSwitchGatewayEntity gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("网关不存在: " + id));
        
        gateway.setEnabled(false);
        gatewayRepository.save(gateway);
        log.info("禁用网关: {} (ID: {})", gateway.getGatewayName(), id);
    }

    /**
     * 更新网关状态
     */
    @Transactional
    public void updateGatewayStatus(Long id, String status) {
        FreeSwitchGatewayEntity gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("网关不存在: " + id));
        
        gateway.setStatus(status);
        gatewayRepository.save(gateway);
        log.debug("更新网关状态: {} (ID: {}) -> {}", gateway.getGatewayName(), id, status);
    }

    /**
     * 根据网关名称更新状态
     */
    @Transactional
    public void updateGatewayStatusByName(String gatewayName, String status) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findByGatewayName(gatewayName);
        if (gatewayOpt.isPresent()) {
            FreeSwitchGatewayEntity gateway = gatewayOpt.get();
            gateway.setStatus(status);
            gatewayRepository.save(gateway);
            log.debug("更新网关状态: {} -> {}", gatewayName, status);
        } else {
            log.warn("网关不存在: {}", gatewayName);
        }
    }

    /**
     * 删除网关
     */
    @Transactional
    public void deleteGateway(Long id) {
        FreeSwitchGatewayEntity gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("网关不存在: " + id));
        
        gatewayRepository.delete(gateway);
        log.info("删除网关: {} (ID: {})", gateway.getGatewayName(), id);
    }

    /**
     * 获取所有在线的网关
     */
    public List<FreeSwitchGatewayEntity> findOnlineGateways() {
        return gatewayRepository.findByStatusAndEnabledTrue("UP");
    }

    /**
     * 获取网关总数
     */
    public long countTotal() {
        return gatewayRepository.count();
    }

    /**
     * 获取启用的网关数量
     */
    public long countEnabled() {
        return gatewayRepository.countByEnabledTrue();
    }

    /**
     * 获取在线的网关数量
     */
    public long countOnline() {
        return gatewayRepository.countByStatusAndEnabledTrue("UP");
    }

    /**
     * 检查网关名称是否存在
     */
    public boolean existsByGatewayName(String gatewayName) {
        return gatewayRepository.existsByGatewayName(gatewayName);
    }
}
