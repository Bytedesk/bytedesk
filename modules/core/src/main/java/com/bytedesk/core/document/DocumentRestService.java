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
package com.bytedesk.core.document;

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
public class DocumentRestService extends BaseRestServiceWithExport<DocumentEntity, DocumentRequest, DocumentResponse, DocumentExcel> {

    private final DocumentRepository documentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<DocumentEntity> queryByOrgEntity(DocumentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<DocumentEntity> specs = DocumentSpecification.search(request, authService);
        return documentRepository.findAll(specs, pageable);
    }

    @Override
    public Page<DocumentResponse> queryByOrg(DocumentRequest request) {
        Page<DocumentEntity> documentPage = queryByOrgEntity(request);
        return documentPage.map(this::convertToResponse);
    }

    @Override
    public Page<DocumentResponse> queryByUser(DocumentRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "document", key = "#uid", unless="#result==null")
    @Override
    public Optional<DocumentEntity> findByUid(String uid) {
        return documentRepository.findByUid(uid);
    }

    @Cacheable(value = "document", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<DocumentEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return documentRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return documentRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public DocumentResponse create(DocumentRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public DocumentResponse createSystemDocument(DocumentRequest request) {
        return createInternal(request, true);
    }

    private DocumentResponse createInternal(DocumentRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<DocumentEntity> document = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (document.isPresent()) {
                return convertToResponse(document.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(DocumentPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        DocumentEntity entity = modelMapper.map(request, DocumentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        DocumentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create document failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public DocumentResponse update(DocumentRequest request) {
        Optional<DocumentEntity> optional = documentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            DocumentEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(DocumentPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            DocumentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update document failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Document not found");
        }
    }

    @Override
    protected DocumentEntity doSave(DocumentEntity entity) {
        return documentRepository.save(entity);
    }

    @Override
    public DocumentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, DocumentEntity entity) {
        try {
            Optional<DocumentEntity> latest = documentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                DocumentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return documentRepository.save(latestEntity);
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
        Optional<DocumentEntity> optional = documentRepository.findByUid(uid);
        if (optional.isPresent()) {
            DocumentEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(DocumentPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // documentRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Document not found");
        }
    }

    @Override
    public void delete(DocumentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public DocumentResponse convertToResponse(DocumentEntity entity) {
        return modelMapper.map(entity, DocumentResponse.class);
    }

    @Override
    public DocumentExcel convertToExcel(DocumentEntity entity) {
        return modelMapper.map(entity, DocumentExcel.class);
    }

    @Override
    protected Specification<DocumentEntity> createSpecification(DocumentRequest request) {
        return DocumentSpecification.search(request, authService);
    }

    @Override
    protected Page<DocumentEntity> executePageQuery(Specification<DocumentEntity> spec, Pageable pageable) {
        return documentRepository.findAll(spec, pageable);
    }
    
    public void initDocuments(String orgUid) {
        // log.info("initDocumentDocument");
        // for (String document : DocumentInitData.getAllDocuments()) {
        //     DocumentRequest documentRequest = DocumentRequest.builder()
        //             .uid(Utils.formatUid(orgUid, document))
        //             .name(document)
        //             .order(0)
        //             .type(DocumentTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemDocument(documentRequest);
        // }
    }

    
    
}
