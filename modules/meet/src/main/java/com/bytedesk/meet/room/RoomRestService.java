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
package com.bytedesk.meet.room;

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
public class RoomRestService extends BaseRestServiceWithExport<RoomEntity, RoomRequest, RoomResponse, RoomExcel> {

    private final RoomRepository roomRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<RoomEntity> queryByOrgEntity(RoomRequest request) {
        Pageable pageable = request.getPageable();
        Specification<RoomEntity> specs = RoomSpecification.search(request, authService);
        return roomRepository.findAll(specs, pageable);
    }

    @Override
    public Page<RoomResponse> queryByOrg(RoomRequest request) {
        Page<RoomEntity> roomPage = queryByOrgEntity(request);
        return roomPage.map(this::convertToResponse);
    }

    @Override
    public Page<RoomResponse> queryByUser(RoomRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "room", key = "#uid", unless="#result==null")
    @Override
    public Optional<RoomEntity> findByUid(String uid) {
        return roomRepository.findByUid(uid);
    }

    @Cacheable(value = "room", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<RoomEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return roomRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return roomRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public RoomResponse create(RoomRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public RoomResponse createSystemRoom(RoomRequest request) {
        return createInternal(request, true);
    }

    private RoomResponse createInternal(RoomRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<RoomEntity> room = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (room.isPresent()) {
                return convertToResponse(room.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(RoomPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        RoomEntity entity = modelMapper.map(request, RoomEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        RoomEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create room failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public RoomResponse update(RoomRequest request) {
        Optional<RoomEntity> optional = roomRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            RoomEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(RoomPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            RoomEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update room failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Room not found");
        }
    }

    @Override
    protected RoomEntity doSave(RoomEntity entity) {
        return roomRepository.save(entity);
    }

    @Override
    public RoomEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RoomEntity entity) {
        try {
            Optional<RoomEntity> latest = roomRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RoomEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return roomRepository.save(latestEntity);
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
        Optional<RoomEntity> optional = roomRepository.findByUid(uid);
        if (optional.isPresent()) {
            RoomEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(RoomPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // roomRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Room not found");
        }
    }

    @Override
    public void delete(RoomRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RoomResponse convertToResponse(RoomEntity entity) {
        return modelMapper.map(entity, RoomResponse.class);
    }

    @Override
    public RoomExcel convertToExcel(RoomEntity entity) {
        return modelMapper.map(entity, RoomExcel.class);
    }

    @Override
    protected Specification<RoomEntity> createSpecification(RoomRequest request) {
        return RoomSpecification.search(request, authService);
    }

    @Override
    protected Page<RoomEntity> executePageQuery(Specification<RoomEntity> spec, Pageable pageable) {
        return roomRepository.findAll(spec, pageable);
    }
    
    public void initRooms(String orgUid) {
        // log.info("initRoomRoom");
        // for (String room : RoomInitData.getAllRooms()) {
        //     RoomRequest roomRequest = RoomRequest.builder()
        //             .uid(Utils.formatUid(orgUid, room))
        //             .name(room)
        //             .order(0)
        //             .type(RoomTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemRoom(roomRequest);
        // }
    }

    
    
}
