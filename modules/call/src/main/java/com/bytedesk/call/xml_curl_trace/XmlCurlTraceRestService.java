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
package com.bytedesk.call.xml_curl_trace;

import java.util.Collections;
import java.util.List;
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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class XmlCurlTraceRestService extends BaseRestServiceWithExport<XmlCurlTraceEntity, XmlCurlTraceRequest, XmlCurlTraceResponse, XmlCurlTraceExcel> {

    private final XmlCurlTraceRepository xml_curl_traceRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<XmlCurlTraceEntity> queryByOrgEntity(XmlCurlTraceRequest request) {
        Pageable pageable = request.getPageable();
        Specification<XmlCurlTraceEntity> specs = XmlCurlTraceSpecification.search(request, authService);
        return xml_curl_traceRepository.findAll(specs, pageable);
    }

    @Override
    public Page<XmlCurlTraceResponse> queryByOrg(XmlCurlTraceRequest request) {
        Page<XmlCurlTraceEntity> xml_curl_tracePage = queryByOrgEntity(request);
        return xml_curl_tracePage.map(this::convertToResponse);
    }

    @Override
    public Page<XmlCurlTraceResponse> queryByUser(XmlCurlTraceRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "xml_curl_trace", key = "#uid", unless="#result==null")
    @Override
    public Optional<XmlCurlTraceEntity> findByUid(String uid) {
        return xml_curl_traceRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return xml_curl_traceRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public XmlCurlTraceResponse create(XmlCurlTraceRequest request) {
        return createInternal(request, false);
    }

    private XmlCurlTraceResponse createInternal(XmlCurlTraceRequest request, boolean skipPermissionCheck) {
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }

        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = user != null && user.isSuperUser() ? user.getLevel() : "ORGANIZATION";
            request.setLevel(level);
        }

        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(XmlCurlTracePermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }

        XmlCurlTraceEntity entity = modelMapper.map(request, XmlCurlTraceEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        XmlCurlTraceEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public XmlCurlTraceResponse update(XmlCurlTraceRequest request) {
        Optional<XmlCurlTraceEntity> optional = xml_curl_traceRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            XmlCurlTraceEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(XmlCurlTracePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            XmlCurlTraceEntity savedEntity = save(entity);
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
    protected XmlCurlTraceEntity doSave(XmlCurlTraceEntity entity) {
        return xml_curl_traceRepository.save(entity);
    }

    @Override
    public XmlCurlTraceEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, XmlCurlTraceEntity entity) {
        try {
            Optional<XmlCurlTraceEntity> latest = xml_curl_traceRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                XmlCurlTraceEntity latestEntity = latest.get();
                latestEntity.setSection(entity.getSection());
                latestEntity.setCategory(entity.getCategory());
                latestEntity.setRemote(entity.getRemote());
                latestEntity.setMethod(entity.getMethod());
                latestEntity.setUri(entity.getUri());
                latestEntity.setQuery(entity.getQuery());
                latestEntity.setFound(entity.getFound());
                latestEntity.setResponseSize(entity.getResponseSize());
                latestEntity.setCostMs(entity.getCostMs());
                latestEntity.setKeyFields(entity.getKeyFields());
                return xml_curl_traceRepository.save(latestEntity);
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
        Optional<XmlCurlTraceEntity> optional = xml_curl_traceRepository.findByUid(uid);
        if (optional.isPresent()) {
            XmlCurlTraceEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(XmlCurlTracePermissions.MODULE_NAME, "DELETE", entity)) {
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
    public void delete(XmlCurlTraceRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public XmlCurlTraceResponse convertToResponse(XmlCurlTraceEntity entity) {
        return modelMapper.map(entity, XmlCurlTraceResponse.class);
    }

    @Override
    public XmlCurlTraceExcel convertToExcel(XmlCurlTraceEntity entity) {
        return modelMapper.map(entity, XmlCurlTraceExcel.class);
    }

    public List<XmlCurlTraceResponse> recent(int limit, String section, String category) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        String sectionFilter = normalizeLower(section);
        String categoryFilter = normalizeLower(category);

        List<XmlCurlTraceEntity> rows;
        if (StringUtils.hasText(sectionFilter) && StringUtils.hasText(categoryFilter)) {
            rows = xml_curl_traceRepository.findTop200BySectionAndCategoryAndDeletedFalseOrderByCreatedAtDesc(sectionFilter, categoryFilter);
        } else if (StringUtils.hasText(sectionFilter)) {
            rows = xml_curl_traceRepository.findTop200BySectionAndDeletedFalseOrderByCreatedAtDesc(sectionFilter);
        } else if (StringUtils.hasText(categoryFilter)) {
            rows = xml_curl_traceRepository.findTop200ByCategoryAndDeletedFalseOrderByCreatedAtDesc(categoryFilter);
        } else {
            rows = xml_curl_traceRepository.findTop200ByDeletedFalseOrderByCreatedAtDesc();
        }

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        return rows.stream().limit(safeLimit).map(this::convertToResponse).toList();
    }

    @Override
    protected Specification<XmlCurlTraceEntity> createSpecification(XmlCurlTraceRequest request) {
        return XmlCurlTraceSpecification.search(request, authService);
    }

    @Override
    protected Page<XmlCurlTraceEntity> executePageQuery(Specification<XmlCurlTraceEntity> spec, Pageable pageable) {
        return xml_curl_traceRepository.findAll(spec, pageable);
    }

    private String normalizeLower(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toLowerCase();
    }
    
}
