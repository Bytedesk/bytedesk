/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 11:40:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.black.IpBlacklistEntity;
import com.bytedesk.core.ip.black.IpBlacklistRestService;
import com.bytedesk.core.ip.white.IpWhitelistRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class IpAccessRestService extends BaseRestService<IpAccessEntity, IpAccessRequest, IpAccessResponse> {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_RETRY_COUNT = 3;

    private final IpAccessRepository ipAccessRepository;

    private final IpWhitelistRepository ipWhitelistRepository;

    private final IpBlacklistRestService ipBlacklistRestService;

    private final IpService ipService;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    @Override
    public Page<IpAccessResponse> queryByOrg(IpAccessRequest request) {
        Pageable pageable = request.getPageable();
        Specification<IpAccessEntity> spec = IpAccessSpecification.search(request);
        Page<IpAccessEntity> page = ipAccessRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<IpAccessResponse> queryByUser(IpAccessRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public IpAccessResponse queryByUid(IpAccessRequest request) {
        Optional<IpAccessEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            IpAccessEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("IpAccess not found");
        }
    }

    @Override
    public Optional<IpAccessEntity> findByUid(String uid) {
        return ipAccessRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return ipAccessRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public IpAccessResponse create(IpAccessRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        IpAccessEntity entity = modelMapper.map(request, IpAccessEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        IpAccessEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create IpAccess failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public IpAccessResponse update(IpAccessRequest request) {
        Optional<IpAccessEntity> optional = ipAccessRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            IpAccessEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            IpAccessEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update IpAccess failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("IpAccess not found");
        }
    }

    @Override
    protected IpAccessEntity doSave(IpAccessEntity entity) {
        return ipAccessRepository.save(entity);
    }

    @Override
    public IpAccessEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpAccessEntity entity) {
        log.warn("Optimistic locking failure for IP: {} with endpoint: {}", entity.getIp(), entity.getEndpoint());
        // 尝试刷新实体状态
        if (entity.getId() != null) {
            Optional<IpAccessEntity> refreshedEntity = ipAccessRepository.findById(entity.getId());
            if (refreshedEntity.isPresent()) {
                IpAccessEntity fresh = refreshedEntity.get();
                // 更新访问计数和最后访问时间
                fresh.setAccessCount(fresh.getAccessCount() + 1);
                fresh.setLastAccessTime(BdDateUtils.now());
                ipAccessRepository.save(fresh);

                // 如果访问次数超过限制，加入黑名单
                if (fresh.getAccessCount() > MAX_REQUESTS_PER_MINUTE) {
                    ipBlacklistRestService.addToBlacklistSystem(fresh.getIp());
                }

                return fresh;
            }
        }
        // 如果无法刷新实体，则返回原始实体
        return entity;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<IpAccessEntity> optional = ipAccessRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ipAccessRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("IpAccess not found");
        }
    }

    @Override
    public void delete(IpAccessRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public IpAccessResponse convertToResponse(IpAccessEntity entity) {
        return modelMapper.map(entity, IpAccessResponse.class);
    }

    public Boolean isIpBlocked(String ip) {
        // 检查是否在白名单中
        if (ipWhitelistRepository.existsByIp(ip)) {
            return false;
        }

        // 检查是否在黑名单中且未过期
        Optional<IpBlacklistEntity> blacklist = ipBlacklistRestService.findByIp(ip);
        if (blacklist.isPresent() && blacklist.get().getEndTime().isAfter(BdDateUtils.now())) {
            return true;
        }

        return false;
    }

    @Transactional
    public void recordAccess(String ip, String endpoint, String params) {
        Assert.notNull(ip, "IP address must not be null");
        Assert.notNull(endpoint, "Endpoint must not be null");
        //
        if (endpoint.toLowerCase().contains("ping")) {
            return;
        }
        //
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                ZonedDateTime now = BdDateUtils.now();
                ZonedDateTime oneMinuteAgo = now.minusMinutes(1);

                // 获取最近一分钟的访问记录
                Optional<IpAccessEntity> accessOptional = ipAccessRepository
                        .findFirstByIpAndEndpointAndAccessTimeAfter(ip, endpoint, oneMinuteAgo);
                
                // 如果访问记录不存在，则创建新的访问记录
                if (accessOptional.isEmpty()) {
                    String ipLocation = ipService.getIpLocation(ip);
                    //
                    IpAccessEntity access = new IpAccessEntity();
                    access.setIp(ip);
                    access.setIpLocation(ipLocation);
                    access.setUid(uidUtils.getUid());
                    access.setEndpoint(endpoint);
                    access.setParams(params);
                    access.setAccessTime(now);
                    access.setAccessCount(1);
                    access.setLastAccessTime(now);
                    
                    // 保存新记录
                    ipAccessRepository.save(access);
                } else {
                    // 使用原子更新避免乐观锁冲突
                    IpAccessEntity existingAccess = accessOptional.get();
                    int updatedRows = ipAccessRepository.incrementAccessCount(
                        existingAccess.getId(), now, now);
                    
                    if (updatedRows == 0) {
                        // 如果原子更新失败，说明记录可能已被删除，重新创建
                        log.debug("Atomic update failed for IP: {}, creating new record", ip);
                        String ipLocation = ipService.getIpLocation(ip);
                        IpAccessEntity newAccess = new IpAccessEntity();
                        newAccess.setIp(ip);
                        newAccess.setIpLocation(ipLocation);
                        newAccess.setUid(uidUtils.getUid());
                        newAccess.setEndpoint(endpoint);
                        newAccess.setParams(params);
                        newAccess.setAccessTime(now);
                        newAccess.setAccessCount(1);
                        newAccess.setLastAccessTime(now);
                        ipAccessRepository.save(newAccess);
                    } else {
                        // 原子更新成功，检查是否需要加入黑名单
                        // 由于原子更新无法返回更新后的计数，我们需要重新查询
                        Optional<IpAccessEntity> updatedAccess = ipAccessRepository.findById(existingAccess.getId());
                        if (updatedAccess.isPresent() && updatedAccess.get().getAccessCount() > MAX_REQUESTS_PER_MINUTE) {
                            ipBlacklistRestService.addToBlacklistSystem(ip);
                        }
                    }
                }
                
                // 成功处理，退出重试循环
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                log.debug("Optimistic locking failure for IP: {} with endpoint: {}, retry: {}/{}", 
                         ip, endpoint, retryCount, MAX_RETRY_COUNT);
                
                // 如果已经达到最大重试次数，则处理异常
                if (retryCount >= MAX_RETRY_COUNT) {
                    log.warn("Failed to update IP access record after {} retries for IP: {}", MAX_RETRY_COUNT, ip);
                    // 尝试添加到黑名单，以防止潜在的攻击
                    ipBlacklistRestService.addToBlacklistSystem(ip);
                    break;
                }
                
                // 短暂休眠以避免立即重试，使用指数退避
                try {
                    Thread.sleep(50 * (1 << retryCount)); // 指数退避：50, 100, 200ms
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                log.error("Unexpected error while recording IP access for IP: {} with endpoint: {}", ip, endpoint, e);
                break;
            }
        }
    }

}