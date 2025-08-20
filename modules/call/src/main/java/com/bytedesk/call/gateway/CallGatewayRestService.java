/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:56:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call网关REST服务
 */
@Slf4j
@Service
@AllArgsConstructor
public class CallGatewayRestService extends BaseRestServiceWithExport<CallGatewayEntity, CallGatewayRequest, CallGatewayResponse, CallGatewayExcel> {

    private final CallGatewayRepository gatewayRepository;
    
    private final ModelMapper modelMapper;

    @Override
    protected Specification<CallGatewayEntity> createSpecification(CallGatewayRequest request) {
        return CallGatewaySpecification.search(request, authService);
    }

    @Override
    protected Page<CallGatewayEntity> executePageQuery(Specification<CallGatewayEntity> spec, Pageable pageable) {
        return gatewayRepository.findAll(spec, pageable);
    }

    @Override
    @Cacheable(value = "gateway", key = "#request.orgUid + ':' + #request.pageNumber + ':' + #request.pageSize")
    public Page<CallGatewayResponse> queryByOrg(CallGatewayRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallGatewayEntity> spec = CallGatewaySpecification.search(request, authService);
        Page<CallGatewayEntity> gatewayPage = gatewayRepository.findAll(spec, pageable);
        return gatewayPage.map(this::convertToResponse);
    }

    @Override
    @Cacheable(value = "gateway", key = "#request.userUid + ':' + #request.pageNumber + ':' + #request.pageSize")
    public Page<CallGatewayResponse> queryByUser(CallGatewayRequest request) {
        
        // 设置当前用户的组织UID
        // request.setOrgUid(authService.getOrgUid());
        
        return queryByOrg(request);
    }

    // @Override
    // @Cacheable(value = "gateway", key = "#request.uid")
    // public CallGatewayResponse query(CallGatewayRequest request) {
        
    //     return findByUid(request.getUid());
    // }

    @Override
    @CacheEvict(value = "gateway", allEntries = true)
    public CallGatewayResponse create(CallGatewayRequest request) {
        
        // 设置组织UID
        // if (!StringUtils.hasText(request.getOrgUid())) {
        //     request.setOrgUid(authService.getOrgUid());
        // }
        
        // 检查网关名称是否已存在
        // if (gatewayRepository.existsByGatewayNameAndOrgUid(request.getGatewayName(), request.getOrgUid())) {
        //     throw new RuntimeException("网关名称已存在");
        // }
        
        CallGatewayEntity gateway = modelMapper.map(request, CallGatewayEntity.class);
        // gateway.setUid(UidUtils.uid());
        gateway.setStatus("DOWN"); // 新创建的网关默认为离线状态
        
        CallGatewayEntity savedGateway = gatewayRepository.save(gateway);
        
        return convertToResponse(savedGateway);
    }

    @Override
    @CacheEvict(value = "gateway", allEntries = true)
    public CallGatewayResponse update(CallGatewayRequest request) {
        
        Optional<CallGatewayEntity> gatewayOptional = findByUid(request.getUid());
        if (gatewayOptional.isEmpty()) {
            throw new RuntimeException("网关不存在");
        }
        
        CallGatewayEntity gateway = gatewayOptional.get();
        
        // 检查网关名称是否与其他网关重复
        // if (!gateway.getGatewayName().equals(request.getGatewayName()) && 
        //     gatewayRepository.existsByGatewayNameAndOrgUid(request.getGatewayName(), gateway.getOrgUid())) {
        //     throw new RuntimeException("网关名称已存在");
        // }
        
        // 更新字段
        modelMapper.map(request, gateway);
        
        CallGatewayEntity updatedGateway = gatewayRepository.save(gateway);
        
        return convertToResponse(updatedGateway);
    }

    @Override
    @CacheEvict(value = "gateway", allEntries = true)
    public void deleteByUid(String uid) {
        
        Optional<CallGatewayEntity> gatewayOptional = findByUid(uid);
        if (gatewayOptional.isEmpty()) {
            throw new RuntimeException("网关不存在");
        }
        
        CallGatewayEntity gateway = gatewayOptional.get();
        gateway.setDeleted(true);
        
        gatewayRepository.save(gateway);
    }

    // @Override
    // public void export(CallGatewayRequest request, HttpServletResponse response) {
        
    //     // 设置组织UID
    //     // if (!StringUtils.hasText(request.getOrgUid())) {
    //     //     request.setOrgUid(authService.getOrgUid());
    //     // }
        
    //     // 查询所有数据，不分页
    //     Specification<CallGatewayEntity> spec = CallGatewaySpecification.search(request, authService);
    //     List<CallGatewayEntity> gateways = gatewayRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "updatedAt"));
        
    //     List<CallGatewayExcel> excelList = gateways.stream()
    //             .map(CallGatewayExcel::fromEntity)
    //             .toList();
        
    //     exportToExcel(excelList, "网关列表", CallGatewayExcel.class, response);
    // }

    @Override
    public Page<CallGatewayEntity> queryByOrgEntity(CallGatewayRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallGatewayEntity> spec = CallGatewaySpecification.search(request, authService);
        return gatewayRepository.findAll(spec, pageable);
    }

    @Override
    public void delete(CallGatewayRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CallGatewayEntity doSave(CallGatewayEntity entity) {
        return gatewayRepository.save(entity);
    }

    // @Override
    // public CallGatewayEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallGatewayEntity entity) {
    //     log.warn("Call Gateway保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
    //     try {
    //         Optional<CallGatewayEntity> latest = findByUid(entity.getUid());
    //         if (latest.isPresent()) {
    //             CallGatewayEntity latestEntity = latest.get();
    //             return doSave(latestEntity);
    //         }
    //     } catch (Exception ex) {
    //         throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
    //     }
    //     return null;
    // }

    @Override
    public CallGatewayExcel convertToExcel(CallGatewayEntity entity) {
        return modelMapper.map(entity, CallGatewayExcel.class);
    }

    @Override
    public Optional<CallGatewayEntity> findByUid(String uid) {
        return gatewayRepository.findByUid(uid);
    }

    @Override
    public CallGatewayResponse convertToResponse(CallGatewayEntity entity) {
        return modelMapper.map(entity, CallGatewayResponse.class);
    }

    @Override
    public CallGatewayEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            CallGatewayEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }
}
