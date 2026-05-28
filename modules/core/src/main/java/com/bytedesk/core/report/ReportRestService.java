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
package com.bytedesk.core.report;

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
import com.bytedesk.core.constant.I18Consts;
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
public class ReportRestService extends BaseRestServiceWithExport<ReportEntity, ReportRequest, ReportResponse, ReportExcel> {

    private final ReportRepository reportRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ReportEntity> queryByOrgEntity(ReportRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ReportEntity> specs = ReportSpecification.search(request, authService);
        return reportRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ReportResponse> queryByOrg(ReportRequest request) {
        Page<ReportEntity> reportPage = queryByOrgEntity(request);
        return reportPage.map(this::convertToResponse);
    }

    @Override
    public Page<ReportResponse> queryByUser(ReportRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "report", key = "#uid", unless="#result==null")
    @Override
    public Optional<ReportEntity> findByUid(String uid) {
        return reportRepository.findByUid(uid);
    }

    @Cacheable(value = "report", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ReportEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return reportRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return reportRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ReportResponse create(ReportRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ReportResponse createSystemReport(ReportRequest request) {
        return createInternal(request, true);
    }

    private ReportResponse createInternal(ReportRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ReportEntity> report = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (report.isPresent()) {
                return convertToResponse(report.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ReportPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        ReportEntity entity = modelMapper.map(request, ReportEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ReportEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ReportResponse update(ReportRequest request) {
        Optional<ReportEntity> optional = reportRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ReportEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ReportPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            ReportEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected ReportEntity doSave(ReportEntity entity) {
        return reportRepository.save(entity);
    }

    @Override
    public ReportEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ReportEntity entity) {
        try {
            Optional<ReportEntity> latest = reportRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ReportEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return reportRepository.save(latestEntity);
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
        Optional<ReportEntity> optional = reportRepository.findByUid(uid);
        if (optional.isPresent()) {
            ReportEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ReportPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(ReportRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ReportResponse convertToResponse(ReportEntity entity) {
        return modelMapper.map(entity, ReportResponse.class);
    }

    @Override
    public ReportExcel convertToExcel(ReportEntity entity) {
        return modelMapper.map(entity, ReportExcel.class);
    }

    @Override
    protected Specification<ReportEntity> createSpecification(ReportRequest request) {
        return ReportSpecification.search(request, authService);
    }

    @Override
    protected Page<ReportEntity> executePageQuery(Specification<ReportEntity> spec, Pageable pageable) {
        return reportRepository.findAll(spec, pageable);
    }
    
    public void initReports(String orgUid) {
        // log.info("initReportReport");
    }

    
    
}
