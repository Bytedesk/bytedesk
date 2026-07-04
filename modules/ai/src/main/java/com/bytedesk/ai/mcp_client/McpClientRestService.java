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
package com.bytedesk.ai.mcp_client;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class McpClientRestService extends BaseRestServiceWithExport<McpClientEntity, McpClientRequest, McpClientResponse, McpClientExcel> {

    private final McpClientRepository mcpClientRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    // 模块名称，用于权限检查
    private static final String MODULE_NAME = "MCPCLIENT";
    
    @Override
    public Page<McpClientEntity> queryByOrgEntity(McpClientRequest request) {
        Pageable pageable = request.getPageable();
        Specification<McpClientEntity> specs = McpClientSpecification.search(request, authService);
        return mcpClientRepository.findAll(specs, pageable);
    }

    @Override
    public Page<McpClientResponse> queryByOrg(McpClientRequest request) {
        Page<McpClientEntity> mcp_clientPage = queryByOrgEntity(request);
        return mcp_clientPage.map(this::convertToResponse);
    }

    @Override
    public Page<McpClientResponse> queryByUser(McpClientRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "mcp_client", key = "#uid", unless="#result==null")
    @Override
    public Optional<McpClientEntity> findByUid(String uid) {
        return mcpClientRepository.findByUid(uid);
    }

    @Cacheable(value = "mcp_client", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<McpClientEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return mcpClientRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return mcpClientRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public McpClientResponse create(McpClientRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public McpClientResponse createSystemMcpClient(McpClientRequest request) {
        return createInternal(request, true);
    }

    private McpClientResponse createInternal(McpClientRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<McpClientEntity> mcp_client = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (mcp_client.isPresent()) {
                return convertToResponse(mcp_client.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        // String level = request.getLevel();
        // if (!StringUtils.hasText(level)) {
        //     level = LevelEnum.ORGANIZATION.name();
        //     request.setLevel(level);
        // }
        
        // // 检查用户是否有权限创建该层级的数据
        // if (!skipPermissionCheck && !permissionService.canCreateAtLevel(MODULE_NAME, level)) {
        //     throw new RuntimeException("无权限创建该层级的标签数据");
        // }
        
        // 
        McpClientEntity entity = modelMapper.map(request, McpClientEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        McpClientEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create mcp_client failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public McpClientResponse update(McpClientRequest request) {
        Optional<McpClientEntity> optional = mcpClientRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            McpClientEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            McpClientEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update mcp_client failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("McpClient not found");
        }
    }

    @Override
    protected McpClientEntity doSave(McpClientEntity entity) {
        return mcpClientRepository.save(entity);
    }

    @Override
    public McpClientEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, McpClientEntity entity) {
        try {
            Optional<McpClientEntity> latest = mcpClientRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                McpClientEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return mcpClientRepository.save(latestEntity);
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
        Optional<McpClientEntity> optional = mcpClientRepository.findByUid(uid);
        if (optional.isPresent()) {
            McpClientEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // mcp_clientRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("McpClient not found");
        }
    }

    @Override
    public void delete(McpClientRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public McpClientResponse convertToResponse(McpClientEntity entity) {
        return modelMapper.map(entity, McpClientResponse.class);
    }

    @Override
    public McpClientExcel convertToExcel(McpClientEntity entity) {
        return modelMapper.map(entity, McpClientExcel.class);
    }

    @Override
    protected Specification<McpClientEntity> createSpecification(McpClientRequest request) {
        return McpClientSpecification.search(request, authService);
    }

    @Override
    protected Page<McpClientEntity> executePageQuery(Specification<McpClientEntity> spec, Pageable pageable) {
        return mcpClientRepository.findAll(spec, pageable);
    }
    
    public void initMcpClients(String orgUid) {
        // log.info("initMcpClientMcpClient");
        // for (String mcp_client : McpClientInitData.getAllMcpClients()) {
        //     McpClientRequest mcp_clientRequest = McpClientRequest.builder()
        //             .uid(Utils.formatUid(orgUid, mcp_client))
        //             .name(mcp_client)
        //             .order(0)
        //             .type(McpClientTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemMcpClient(mcp_clientRequest);
        // }
    }

    
    
}
