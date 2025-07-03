/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 17:53:53
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.black.IpBlacklistEntity;
import com.bytedesk.core.ip.black.IpBlacklistRestService;
import com.bytedesk.core.ip.white.IpWhitelistRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.base.BaseRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class IpAccessRestService extends BaseRestService<IpAccessEntity, IpAccessRequest, IpAccessResponse> {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_RETRY_COUNT = 3;

    private final IpAccessRepository ipAccessRepository;

    private final IpWhitelistRepository whitelistRepository;

    private final IpBlacklistRestService ipBlacklistService;

    private final IpService ipService;

    private final UidUtils uidUtils;

    public Boolean isIpBlocked(String ip) {
        // 检查是否在白名单中
        if (whitelistRepository.existsByIp(ip)) {
            return false;
        }

        // 检查是否在黑名单中且未过期
        Optional<IpBlacklistEntity> blacklist = ipBlacklistService.findByIp(ip);
        if (blacklist.isPresent() && blacklist.get().getEndTime().isAfter(ZonedDateTime.now())) {
            return true;
        }

        return false;
    }

    public void recordAccess(String ip, String endpoint, String params) {
        Assert.notNull(ip, "IP address must not be null");
        Assert.notNull(endpoint, "Endpoint must not be null");
        //
        if (endpoint.equals("/visitor/api/v1/ping")) {
            return;
        }
        //
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                ZonedDateTime now = ZonedDateTime.now();
                ZonedDateTime oneMinuteAgo = now.minusMinutes(1);

                // 获取最近一分钟的访问记录
                Optional<IpAccessEntity> accessOptional = ipAccessRepository
                        .findFirstByIpAndEndpointAndAccessTimeAfter(ip, endpoint, oneMinuteAgo);
                // 如果访问记录不存在，则创建新的访问记录
                IpAccessEntity access;
                if (accessOptional.isEmpty()) {
                    String ipLocation = ipService.getIpLocation(ip);
                    //
                    access = new IpAccessEntity();
                    access.setIp(ip);
                    access.setIpLocation(ipLocation);
                    access.setUid(uidUtils.getUid());
                    access.setEndpoint(endpoint);
                    access.setParams(params);
                    access.setAccessTime(now);
                    access.setAccessCount(1);
                } else {
                    access = accessOptional.get();
                    access.setAccessCount(access.getAccessCount() + 1);
                }
                access.setLastAccessTime(now);
                // 保存访问记录
                ipAccessRepository.save(access);
                // 检查是否需要加入黑名单
                if (access.getAccessCount() > MAX_REQUESTS_PER_MINUTE) {
                    //
                    ipBlacklistService.addToBlacklistSystem(ip);
                }
                // 成功保存，退出重试循环
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                // 如果已经达到最大重试次数，则处理异常
                if (retryCount >= MAX_RETRY_COUNT) {
                    log.warn("Failed to update IP access record after {} retries for IP: {}", MAX_RETRY_COUNT, ip);
                    // 尝试添加到黑名单，以防止潜在的攻击
                    ipBlacklistService.addToBlacklistSystem(ip);
                    break;
                }
                // 短暂休眠以避免立即重试
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
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
                fresh.setLastAccessTime(ZonedDateTime.now());
                ipAccessRepository.save(fresh);

                // 如果访问次数超过限制，加入黑名单
                if (fresh.getAccessCount() > MAX_REQUESTS_PER_MINUTE) {
                    ipBlacklistService.addToBlacklistSystem(fresh.getIp());
                }

                return fresh;
            }
        }
        // 如果无法刷新实体，则返回原始实体
        return entity;
    }

    @Override
    public Page<IpAccessResponse> queryByOrg(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<IpAccessResponse> queryByUser(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<IpAccessEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public IpAccessResponse create(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public IpAccessResponse update(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    protected IpAccessEntity doSave(IpAccessEntity entity) {
        return ipAccessRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public IpAccessResponse convertToResponse(IpAccessEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    @Override
    public IpAccessResponse queryByUid(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
}