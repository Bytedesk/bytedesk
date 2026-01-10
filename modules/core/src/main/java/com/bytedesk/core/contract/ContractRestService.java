/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.contract;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ContractRestService extends BaseRestServiceWithExport<ContractEntity, ContractRequest, ContractResponse, ContractExcel> {

    private final ContractRepository contractRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ContractEntity> queryByOrgEntity(ContractRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ContractEntity> specs = ContractSpecification.search(request, authService);
        return contractRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ContractResponse> queryByOrg(ContractRequest request) {
        Page<ContractEntity> contractPage = queryByOrgEntity(request);
        return contractPage.map(this::convertToResponse);
    }

    @Override
    public Page<ContractResponse> queryByUser(ContractRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "contract", key = "#uid", unless="#result==null")
    @Override
    public Optional<ContractEntity> findByUid(String uid) {
        return contractRepository.findByUid(uid);
    }

    @Cacheable(value = "contract", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ContractEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return contractRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return contractRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ContractResponse create(ContractRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ContractResponse createSystemContract(ContractRequest request) {
        return createInternal(request, true);
    }

    private ContractResponse createInternal(ContractRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ContractEntity> contract = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (contract.isPresent()) {
                return convertToResponse(contract.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ContractPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ContractEntity entity = modelMapper.map(request, ContractEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ContractEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create contract failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ContractResponse update(ContractRequest request) {
        Optional<ContractEntity> optional = contractRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ContractEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ContractPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ContractEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update contract failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Contract not found");
        }
    }

    @Override
    protected ContractEntity doSave(ContractEntity entity) {
        return contractRepository.save(entity);
    }

    @Override
    public ContractEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ContractEntity entity) {
        try {
            Optional<ContractEntity> latest = contractRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ContractEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return contractRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<ContractEntity> optional = contractRepository.findByUid(uid);
        if (optional.isPresent()) {
            ContractEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ContractPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // contractRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Contract not found");
        }
    }

    @Override
    public void delete(ContractRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ContractResponse convertToResponse(ContractEntity entity) {
        return modelMapper.map(entity, ContractResponse.class);
    }

    @Override
    public ContractExcel convertToExcel(ContractEntity entity) {
        return modelMapper.map(entity, ContractExcel.class);
    }

    @Override
    protected Specification<ContractEntity> createSpecification(ContractRequest request) {
        return ContractSpecification.search(request, authService);
    }

    @Override
    protected Page<ContractEntity> executePageQuery(Specification<ContractEntity> spec, Pageable pageable) {
        return contractRepository.findAll(spec, pageable);
    }
    
    public void initContracts(String orgUid) {
        // log.info("initContractContract");
        // for (String contract : ContractInitData.getAllContracts()) {
        //     ContractRequest contractRequest = ContractRequest.builder()
        //             .uid(Utils.formatUid(orgUid, contract))
        //             .name(contract)
        //             .order(0)
        //             .type(ContractTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemContract(contractRequest);
        // }
    }

    
    
}
