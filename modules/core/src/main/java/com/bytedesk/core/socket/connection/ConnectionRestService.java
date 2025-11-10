/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.connection;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ConnectionRestService extends BaseRestServiceWithExport<ConnectionEntity, ConnectionRequest, ConnectionResponse, ConnectionExcel> {

    private final ConnectionRepository connectionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<ConnectionEntity> createSpecification(ConnectionRequest request) {
        return ConnectionSpecification.search(request, authService);
    }

    @Override
    protected Page<ConnectionEntity> executePageQuery(Specification<ConnectionEntity> spec, Pageable pageable) {
        return connectionRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "connection", key = "#uid", unless="#result==null")
    @Override
    public Optional<ConnectionEntity> findByUid(String uid) {
        return connectionRepository.findByUid(uid);
    }

    @Cacheable(value = "connection", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ConnectionEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return connectionRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return connectionRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ConnectionResponse create(ConnectionRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ConnectionEntity> connection = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (connection.isPresent()) {
                return convertToResponse(connection.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        ConnectionEntity entity = modelMapper.map(request, ConnectionEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ConnectionEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create connection failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ConnectionResponse update(ConnectionRequest request) {
        Optional<ConnectionEntity> optional = connectionRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ConnectionEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ConnectionEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update connection failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Connection not found");
        }
    }

    @Override
    protected ConnectionEntity doSave(ConnectionEntity entity) {
        return connectionRepository.save(entity);
    }

    @Override
    public ConnectionEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ConnectionEntity entity) {
        try {
            Optional<ConnectionEntity> latest = connectionRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ConnectionEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return connectionRepository.save(latestEntity);
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
        Optional<ConnectionEntity> optional = connectionRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // connectionRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Connection not found");
        }
    }

    @Override
    public void delete(ConnectionRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ConnectionResponse convertToResponse(ConnectionEntity entity) {
        return modelMapper.map(entity, ConnectionResponse.class);
    }

    @Override
    public ConnectionExcel convertToExcel(ConnectionEntity entity) {
        return modelMapper.map(entity, ConnectionExcel.class);
    }
    
    public void initConnections(String orgUid) {
        // log.info("initThreadConnection");
        for (String connection : ConnectionInitData.getAllConnections()) {
            ConnectionRequest connectionRequest = ConnectionRequest.builder()
                    .uid(Utils.formatUid(orgUid, connection))
                    .name(connection)
                    .type(ConnectionTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(connectionRequest);
        }
    }

    
    
}
