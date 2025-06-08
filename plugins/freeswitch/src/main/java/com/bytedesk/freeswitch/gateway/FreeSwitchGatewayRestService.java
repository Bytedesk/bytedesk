/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 18:57:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.gateway;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.ObjectOptimisticLockingFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FreeSwitch网关REST服务
 */
@Service
@AllArgsConstructor
public class FreeSwitchGatewayRestService extends BaseRestServiceWithExcel<FreeSwitchGatewayEntity, FreeSwitchGatewayRequest, FreeSwitchGatewayResponse, FreeSwitchGatewayExcel> {

    private final FreeSwitchGatewayRepository gatewayRepository;
    
    private final ModelMapper modelMapper;
    
    private final AuthService authService;

    private static final Logger log = LoggerFactory.getLogger(FreeSwitchGatewayRestService.class);

    @Override
    @Cacheable(value = "gateway", key = "#request.orgUid + ':' + #request.page + ':' + #request.size")
    public Page<FreeSwitchGatewayResponse> queryByOrg(FreeSwitchGatewayRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.Direction.DESC, "updatedAt");
        Specification<FreeSwitchGatewayEntity> spec = FreeSwitchGatewaySpecification.search(request);
        Page<FreeSwitchGatewayEntity> gatewayPage = gatewayRepository.findAll(spec, pageable);
        
        return gatewayPage.map(this::convertToResponse);
    }

    @Override
    @Cacheable(value = "gateway", key = "#request.userUid + ':' + #request.page + ':' + #request.size")
    public Page<FreeSwitchGatewayResponse> queryByUser(FreeSwitchGatewayRequest request) {
        
        // 设置当前用户的组织UID
        request.setOrgUid(authService.getOrgUid());
        
        return queryByOrg(request);
    }

    @Override
    @Cacheable(value = "gateway", key = "#request.uid")
    public FreeSwitchGatewayResponse query(FreeSwitchGatewayRequest request) {
        
        return findByUid(request.getUid());
    }

    @Override
    @CacheEvict(value = "gateway", allEntries = true)
    public FreeSwitchGatewayResponse create(FreeSwitchGatewayRequest request) {
        
        // 设置组织UID
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(authService.getOrgUid());
        }
        
        // 检查网关名称是否已存在
        if (gatewayRepository.existsByGatewayNameAndOrgUid(request.getGatewayName(), request.getOrgUid())) {
            throw new RuntimeException("网关名称已存在");
        }
        
        FreeSwitchGatewayEntity gateway = modelMapper.map(request, FreeSwitchGatewayEntity.class);
        gateway.setUid(UidUtils.uid());
        gateway.setStatus("DOWN"); // 新创建的网关默认为离线状态
        
        FreeSwitchGatewayEntity savedGateway = gatewayRepository.save(gateway);
        
        return convertToResponse(savedGateway);
    }

    @Override
    @CacheEvict(value = "gateway", allEntries = true)
    public FreeSwitchGatewayResponse update(FreeSwitchGatewayRequest request) {
        
        Optional<FreeSwitchGatewayEntity> gatewayOptional = gatewayRepository.findByUidAndDeleted(request.getUid(), false);
        if (gatewayOptional.isEmpty()) {
            throw new RuntimeException("网关不存在");
        }
        
        FreeSwitchGatewayEntity gateway = gatewayOptional.get();
        
        // 检查网关名称是否与其他网关重复
        if (!gateway.getGatewayName().equals(request.getGatewayName()) && 
            gatewayRepository.existsByGatewayNameAndOrgUid(request.getGatewayName(), gateway.getOrgUid())) {
            throw new RuntimeException("网关名称已存在");
        }
        
        // 更新字段
        modelMapper.map(request, gateway);
        
        FreeSwitchGatewayEntity updatedGateway = gatewayRepository.save(gateway);
        
        return convertToResponse(updatedGateway);
    }

    @Override
    @CacheEvict(value = "gateway", allEntries = true)
    public void deleteByUid(String uid) {
        
        Optional<FreeSwitchGatewayEntity> gatewayOptional = gatewayRepository.findByUidAndDeleted(uid, false);
        if (gatewayOptional.isEmpty()) {
            throw new RuntimeException("网关不存在");
        }
        
        FreeSwitchGatewayEntity gateway = gatewayOptional.get();
        gateway.setDeleted(true);
        
        gatewayRepository.save(gateway);
    }

    @Override
    public void export(FreeSwitchGatewayRequest request, HttpServletResponse response) {
        
        // 设置组织UID
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(authService.getOrgUid());
        }
        
        // 查询所有数据，不分页
        Specification<FreeSwitchGatewayEntity> spec = FreeSwitchGatewaySpecification.search(request);
        List<FreeSwitchGatewayEntity> gateways = gatewayRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "updatedAt"));
        
        List<FreeSwitchGatewayExcel> excelList = gateways.stream()
                .map(FreeSwitchGatewayExcel::fromEntity)
                .toList();
        
        exportToExcel(excelList, "网关列表", FreeSwitchGatewayExcel.class, response);
    }

    private FreeSwitchGatewayResponse findByUid(String uid) {
        
        Optional<FreeSwitchGatewayEntity> gatewayOptional = gatewayRepository.findByUidAndDeleted(uid, false);
        if (gatewayOptional.isEmpty()) {
            throw new RuntimeException("网关不存在");
        }
        
        return convertToResponse(gatewayOptional.get());
    }

    @Override
    public Page<FreeSwitchGatewayEntity> queryByOrgEntity(FreeSwitchGatewayRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.Direction.DESC, "updatedAt");
        Specification<FreeSwitchGatewayEntity> spec = FreeSwitchGatewaySpecification.search(request);
        return gatewayRepository.findAll(spec, pageable);
    }

    @Override
    public void delete(FreeSwitchGatewayRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FreeSwitchGatewayEntity doSave(FreeSwitchGatewayEntity entity) {
        return gatewayRepository.save(entity);
    }

    @Override
    public FreeSwitchGatewayEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FreeSwitchGatewayEntity entity) {
        log.warn("FreeSwitch Gateway保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        try {
            Optional<FreeSwitchGatewayEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                FreeSwitchGatewayEntity latestEntity = latest.get();
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public FreeSwitchGatewayExcel convertToExcel(FreeSwitchGatewayEntity entity) {
        return modelMapper.map(entity, FreeSwitchGatewayExcel.class);
    }

    @Override
    public Optional<FreeSwitchGatewayEntity> findByUid(String uid) {
        return gatewayRepository.findByUid(uid);
    }

    @Override
    public FreeSwitchGatewayResponse convertToResponse(FreeSwitchGatewayEntity entity) {
        return modelMapper.map(entity, FreeSwitchGatewayResponse.class);
    }
}
