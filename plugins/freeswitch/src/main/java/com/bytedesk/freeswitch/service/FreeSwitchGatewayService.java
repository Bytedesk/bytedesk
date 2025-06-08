package com.bytedesk.freeswitch.service;

import com.bytedesk.freeswitch.model.FreeSwitchGatewayEntity;
import com.bytedesk.freeswitch.repository.FreeSwitchGatewayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public FreeSwitchGatewayEntity createGateway(String name, String profile, String proxy,
                                               String username, String password, Integer port,
                                               Boolean register, Integer expireSeconds) {
        FreeSwitchGatewayEntity gateway = new FreeSwitchGatewayEntity();
        gateway.setName(name);
        gateway.setProfile(profile);
        gateway.setProxy(proxy);
        gateway.setUsername(username);
        gateway.setPassword(password);
        gateway.setPort(port);
        gateway.setRegister(register);
        gateway.setExpireSeconds(expireSeconds);
        gateway.setEnabled(true);
        gateway.setStatus("CREATED");
        
        FreeSwitchGatewayEntity saved = gatewayRepository.save(gateway);
        log.info("创建网关: {} (ID: {}) -> {}:{}", name, saved.getId(), proxy, port);
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
    public Optional<FreeSwitchGatewayEntity> findByName(String name) {
        return gatewayRepository.findByName(name);
    }

    /**
     * 根据配置文件查找网关
     */
    public List<FreeSwitchGatewayEntity> findByProfile(String profile) {
        return gatewayRepository.findByProfile(profile);
    }

    /**
     * 获取所有启用的网关
     */
    public List<FreeSwitchGatewayEntity> getEnabledGateways() {
        return gatewayRepository.findByEnabledTrue();
    }

    /**
     * 根据状态查找网关
     */
    public List<FreeSwitchGatewayEntity> findByStatus(String status) {
        return gatewayRepository.findByStatus(status);
    }

    /**
     * 搜索网关
     */
    public Page<FreeSwitchGatewayEntity> searchGateways(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return gatewayRepository.findAll(pageable);
        }
        return gatewayRepository.findByNameContainingIgnoreCaseOrProxyContainingIgnoreCase(
                keyword, keyword, pageable);
    }

    /**
     * 根据代理服务器查找网关
     */
    public List<FreeSwitchGatewayEntity> findByProxyContaining(String proxy) {
        return gatewayRepository.findByProxyContainingIgnoreCase(proxy);
    }

    /**
     * 更新网关状态
     */
    @Transactional
    public void updateGatewayStatus(String name, String status) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findByName(name);
        if (gatewayOpt.isPresent()) {
            FreeSwitchGatewayEntity gateway = gatewayOpt.get();
            String oldStatus = gateway.getStatus();
            gateway.setStatus(status);
            gateway.setLastRegisterTime(LocalDateTime.now());
            gatewayRepository.save(gateway);
            
            log.info("网关状态更新: {} {} -> {}", name, oldStatus, status);
        } else {
            log.warn("网关不存在，无法更新状态: {}", name);
        }
    }

    /**
     * 更新网关注册状态
     */
    @Transactional
    public void updateRegistrationStatus(String name, boolean registered) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findByName(name);
        if (gatewayOpt.isPresent()) {
            FreeSwitchGatewayEntity gateway = gatewayOpt.get();
            gateway.setStatus(registered ? "REGISTERED" : "UNREGISTERED");
            gateway.setLastRegisterTime(LocalDateTime.now());
            gatewayRepository.save(gateway);
            
            log.info("网关注册状态更新: {} -> {}", name, registered ? "已注册" : "未注册");
        }
    }

    /**
     * 更新网关信息
     */
    @Transactional
    public FreeSwitchGatewayEntity updateGateway(Long id, String name, String profile, String proxy,
                                               String username, String password, Integer port,
                                               Boolean register, Integer expireSeconds, Boolean enabled) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findById(id);
        if (gatewayOpt.isEmpty()) {
            throw new RuntimeException("网关不存在: " + id);
        }

        FreeSwitchGatewayEntity gateway = gatewayOpt.get();
        if (name != null) gateway.setName(name);
        if (profile != null) gateway.setProfile(profile);
        if (proxy != null) gateway.setProxy(proxy);
        if (username != null) gateway.setUsername(username);
        if (password != null) gateway.setPassword(password);
        if (port != null) gateway.setPort(port);
        if (register != null) gateway.setRegister(register);
        if (expireSeconds != null) gateway.setExpireSeconds(expireSeconds);
        if (enabled != null) gateway.setEnabled(enabled);

        FreeSwitchGatewayEntity updated = gatewayRepository.save(gateway);
        log.info("更新网关: {} (ID: {})", gateway.getName(), id);
        return updated;
    }

    /**
     * 删除网关
     */
    @Transactional
    public void deleteGateway(Long id) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findById(id);
        if (gatewayOpt.isPresent()) {
            FreeSwitchGatewayEntity gateway = gatewayOpt.get();
            log.info("删除网关: {} (ID: {})", gateway.getName(), id);
            gatewayRepository.deleteById(id);
        }
    }

    /**
     * 启用/禁用网关
     */
    @Transactional
    public void toggleGatewayStatus(Long id) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findById(id);
        if (gatewayOpt.isPresent()) {
            FreeSwitchGatewayEntity gateway = gatewayOpt.get();
            gateway.setEnabled(!gateway.getEnabled());
            gatewayRepository.save(gateway);
            log.info("切换网关状态: {} -> {}", gateway.getName(), 
                    gateway.getEnabled() ? "启用" : "禁用");
        }
    }

    /**
     * 测试网关连接
     */
    public boolean testGatewayConnection(Long id) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findById(id);
        if (gatewayOpt.isEmpty()) {
            return false;
        }

        FreeSwitchGatewayEntity gateway = gatewayOpt.get();
        
        // 这里可以实现实际的网关连接测试逻辑
        // 例如发送SIP OPTIONS请求或检查注册状态
        
        log.info("测试网关连接: {} -> {}:{}", gateway.getName(), gateway.getProxy(), gateway.getPort());
        
        // 暂时返回基于状态的简单检查
        return gateway.getEnabled() && "REGISTERED".equals(gateway.getStatus());
    }

    /**
     * 重新注册网关
     */
    @Transactional
    public void reregisterGateway(Long id) {
        Optional<FreeSwitchGatewayEntity> gatewayOpt = gatewayRepository.findById(id);
        if (gatewayOpt.isPresent()) {
            FreeSwitchGatewayEntity gateway = gatewayOpt.get();
            gateway.setStatus("REREGISTERING");
            gateway.setLastRegisterTime(LocalDateTime.now());
            gatewayRepository.save(gateway);
            
            log.info("重新注册网关: {}", gateway.getName());
            
            // 这里可以发送实际的重新注册命令到FreeSwitch
            // 例如通过ESL API发送命令
        }
    }

    /**
     * 获取网关统计信息
     */
    public GatewayStatistics getStatistics() {
        long totalGateways = gatewayRepository.count();
        long enabledGateways = gatewayRepository.countByEnabledTrue();
        long registeredGateways = gatewayRepository.countByStatus("REGISTERED");
        
        return new GatewayStatistics(totalGateways, enabledGateways, registeredGateways);
    }

    /**
     * 获取需要注册的网关列表
     */
    public List<FreeSwitchGatewayEntity> getGatewaysNeedingRegistration() {
        return gatewayRepository.findByEnabledTrueAndRegisterTrue();
    }

    /**
     * 检查网关健康状态
     */
    public List<FreeSwitchGatewayEntity> checkGatewayHealth() {
        // 查找可能有问题的网关（启用但未注册，或长时间未更新状态）
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        
        List<FreeSwitchGatewayEntity> problematicGateways = gatewayRepository
                .findByEnabledTrueAndLastRegisterTimeBefore(threshold);
        
        if (!problematicGateways.isEmpty()) {
            log.warn("发现{}个可能有问题的网关", problematicGateways.size());
            for (FreeSwitchGatewayEntity gateway : problematicGateways) {
                log.warn("问题网关: {} (状态: {}, 最后注册时间: {})", 
                        gateway.getName(), gateway.getStatus(), gateway.getLastRegisterTime());
            }
        }
        
        return problematicGateways;
    }

    /**
     * 批量更新网关状态
     */
    @Transactional
    public void batchUpdateGatewayStatus(List<String> gatewayNames, String status) {
        for (String name : gatewayNames) {
            updateGatewayStatus(name, status);
        }
        log.info("批量更新{}个网关状态为: {}", gatewayNames.size(), status);
    }

    /**
     * 网关统计信息数据类
     */
    public static class GatewayStatistics {
        private final long totalGateways;
        private final long enabledGateways;
        private final long registeredGateways;

        public GatewayStatistics(long totalGateways, long enabledGateways, long registeredGateways) {
            this.totalGateways = totalGateways;
            this.enabledGateways = enabledGateways;
            this.registeredGateways = registeredGateways;
        }

        public long getTotalGateways() { return totalGateways; }
        public long getEnabledGateways() { return enabledGateways; }
        public long getRegisteredGateways() { return registeredGateways; }
    }
}
