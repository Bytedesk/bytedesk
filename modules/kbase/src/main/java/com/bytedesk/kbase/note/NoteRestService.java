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
package com.bytedesk.kbase.note;

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
public class NoteRestService extends BaseRestServiceWithExport<NoteEntity, NoteRequest, NoteResponse, NoteExcel> {

    private final NoteRepository noteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<NoteEntity> queryByOrgEntity(NoteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<NoteEntity> specs = NoteSpecification.search(request, authService);
        return noteRepository.findAll(specs, pageable);
    }

    @Override
    public Page<NoteResponse> queryByOrg(NoteRequest request) {
        Page<NoteEntity> notePage = queryByOrgEntity(request);
        return notePage.map(this::convertToResponse);
    }

    @Override
    public Page<NoteResponse> queryByUser(NoteRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "note", key = "#uid", unless="#result==null")
    @Override
    public Optional<NoteEntity> findByUid(String uid) {
        return noteRepository.findByUid(uid);
    }

    @Cacheable(value = "note", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<NoteEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return noteRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return noteRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public NoteResponse create(NoteRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public NoteResponse createSystemNote(NoteRequest request) {
        return createInternal(request, true);
    }

    private NoteResponse createInternal(NoteRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<NoteEntity> note = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (note.isPresent()) {
                return convertToResponse(note.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(NotePermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        NoteEntity entity = modelMapper.map(request, NoteEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        NoteEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create note failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public NoteResponse update(NoteRequest request) {
        Optional<NoteEntity> optional = noteRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            NoteEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(NotePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            NoteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update note failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Note not found");
        }
    }

    @Override
    protected NoteEntity doSave(NoteEntity entity) {
        return noteRepository.save(entity);
    }

    @Override
    public NoteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, NoteEntity entity) {
        try {
            Optional<NoteEntity> latest = noteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                NoteEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return noteRepository.save(latestEntity);
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
        Optional<NoteEntity> optional = noteRepository.findByUid(uid);
        if (optional.isPresent()) {
            NoteEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(NotePermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // noteRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Note not found");
        }
    }

    @Override
    public void delete(NoteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public NoteResponse convertToResponse(NoteEntity entity) {
        return modelMapper.map(entity, NoteResponse.class);
    }

    @Override
    public NoteExcel convertToExcel(NoteEntity entity) {
        return modelMapper.map(entity, NoteExcel.class);
    }

    @Override
    protected Specification<NoteEntity> createSpecification(NoteRequest request) {
        return NoteSpecification.search(request, authService);
    }

    @Override
    protected Page<NoteEntity> executePageQuery(Specification<NoteEntity> spec, Pageable pageable) {
        return noteRepository.findAll(spec, pageable);
    }
    
    public void initNotes(String orgUid) {
        // log.info("initNoteNote");
        // for (String note : NoteInitData.getAllNotes()) {
        //     NoteRequest noteRequest = NoteRequest.builder()
        //             .uid(Utils.formatUid(orgUid, note))
        //             .name(note)
        //             .order(0)
        //             .type(NoteTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemNote(noteRequest);
        // }
    }

    
    
}
