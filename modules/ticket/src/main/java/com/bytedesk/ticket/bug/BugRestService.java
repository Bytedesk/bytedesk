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
package com.bytedesk.ticket.bug;

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
public class BugRestService extends BaseRestServiceWithExport<BugEntity, BugRequest, BugResponse, BugExcel> {

    private final BugRepository bugRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<BugEntity> queryByOrgEntity(BugRequest request) {
        Pageable pageable = request.getPageable();
        Specification<BugEntity> specs = BugSpecification.search(request, authService);
        return bugRepository.findAll(specs, pageable);
    }

    @Override
    public Page<BugResponse> queryByOrg(BugRequest request) {
        Page<BugEntity> bugPage = queryByOrgEntity(request);
        return bugPage.map(this::convertToResponse);
    }

    @Override
    public Page<BugResponse> queryByUser(BugRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "bug", key = "#uid", unless="#result==null")
    @Override
    public Optional<BugEntity> findByUid(String uid) {
        return bugRepository.findByUid(uid);
    }

    @Cacheable(value = "bug", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<BugEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return bugRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return bugRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public BugResponse create(BugRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public BugResponse createSystemBug(BugRequest request) {
        return createInternal(request, true);
    }

    private BugResponse createInternal(BugRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<BugEntity> bug = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (bug.isPresent()) {
                return convertToResponse(bug.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(BugPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        BugEntity entity = modelMapper.map(request, BugEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        BugEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create bug failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public BugResponse update(BugRequest request) {
        Optional<BugEntity> optional = bugRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            BugEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(BugPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            BugEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update bug failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Bug not found");
        }
    }

    @Override
    protected BugEntity doSave(BugEntity entity) {
        return bugRepository.save(entity);
    }

    @Override
    public BugEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, BugEntity entity) {
        try {
            Optional<BugEntity> latest = bugRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                BugEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return bugRepository.save(latestEntity);
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
        Optional<BugEntity> optional = bugRepository.findByUid(uid);
        if (optional.isPresent()) {
            BugEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(BugPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // bugRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Bug not found");
        }
    }

    @Override
    public void delete(BugRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public BugResponse convertToResponse(BugEntity entity) {
        return modelMapper.map(entity, BugResponse.class);
    }

    @Override
    public BugExcel convertToExcel(BugEntity entity) {
        return modelMapper.map(entity, BugExcel.class);
    }

    @Override
    protected Specification<BugEntity> createSpecification(BugRequest request) {
        return BugSpecification.search(request, authService);
    }

    @Override
    protected Page<BugEntity> executePageQuery(Specification<BugEntity> spec, Pageable pageable) {
        return bugRepository.findAll(spec, pageable);
    }
    
    public void initBugs(String orgUid) {
        // log.info("initBugBug");
        // for (String bug : BugInitData.getAllBugs()) {
        //     BugRequest bugRequest = BugRequest.builder()
        //             .uid(Utils.formatUid(orgUid, bug))
        //             .name(bug)
        //             .order(0)
        //             .type(BugTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemBug(bugRequest);
        // }
    }

    
    
}
