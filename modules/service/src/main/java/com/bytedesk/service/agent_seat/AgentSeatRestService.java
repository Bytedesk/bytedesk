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
package com.bytedesk.service.agent_seat;

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
public class AgentSeatRestService
        extends BaseRestServiceWithExport<AgentSeatEntity, AgentSeatRequest, AgentSeatResponse, AgentSeatExcel> {

    private final AgentSeatRepository agentSeatRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final PermissionService permissionService;

    private final AgentSeatDomainService agentSeatDomainService;

    @Override
    public Page<AgentSeatEntity> queryByOrgEntity(AgentSeatRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AgentSeatEntity> specs = AgentSeatSpecification.search(request, authService);
        return agentSeatRepository.findAll(specs, pageable);
    }

    @Override
    public Page<AgentSeatResponse> queryByOrg(AgentSeatRequest request) {
        Page<AgentSeatEntity> agent_seatPage = queryByOrgEntity(request);
        return agent_seatPage.map(this::convertToResponse);
    }

    @Override
    public Page<AgentSeatResponse> queryByUser(AgentSeatRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Transactional
    public AgentSeatResponse queryByAssignedAgentUid(AgentSeatRequest request) {
        if (!StringUtils.hasText(request.getAssignedAgentUid())) {
            throw new RuntimeException("assignedAgentUid is required");
        }

        AgentSeatEntity seat = agentSeatDomainService.findManagedSeatByAgentUid(request.getAssignedAgentUid())
                .orElse(null);

        if (seat == null) {
            return null;
        }

        return convertToResponse(seat);
    }

    @Cacheable(value = "agent_seat", key = "#uid", unless = "#result==null")
    @Override
    public Optional<AgentSeatEntity> findByUid(String uid) {
        return agentSeatRepository.findByUid(uid);
    }

    // @Cacheable(value = "agent_seat", key = "#name + '_' + #orgUid + '_' + #type",
    // unless="#result==null")
    // public Optional<AgentSeatEntity> findByNameAndOrgUidAndType(String name,
    // String orgUid, String type) {
    // return agent_seatRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name,
    // orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return agentSeatRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public AgentSeatResponse create(AgentSeatRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public AgentSeatResponse createSystemAgentSeat(AgentSeatRequest request) {
        return createInternal(request, true);
    }

    private AgentSeatResponse createInternal(AgentSeatRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) &&
        // StringUtils.hasText(request.getOrgUid()) &&
        // StringUtils.hasText(request.getType())) {
        // Optional<AgentSeatEntity> agent_seat =
        // findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(),
        // request.getType());
        // if (agent_seat.isPresent()) {
        // return convertToResponse(agent_seat.get());
        // }
        // }

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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(AgentSeatPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }

        //
        AgentSeatEntity entity = modelMapper.map(request, AgentSeatEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        //
        AgentSeatEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create agent_seat failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public AgentSeatResponse update(AgentSeatRequest request) {
        Optional<AgentSeatEntity> optional = agentSeatRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AgentSeatEntity entity = optional.get();

            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(AgentSeatPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }

            modelMapper.map(request, entity);
            //
            AgentSeatEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update agent_seat failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("AgentSeat not found");
        }
    }

    @Override
    protected AgentSeatEntity doSave(AgentSeatEntity entity) {
        return agentSeatRepository.save(entity);
    }

    @Override
    public AgentSeatEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentSeatEntity entity) {
        try {
            Optional<AgentSeatEntity> latest = agentSeatRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentSeatEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setSeatNo(entity.getSeatNo());
                latestEntity.setSource(entity.getSource());
                latestEntity.setStatus(entity.getStatus());
                latestEntity.setBaseSeat(entity.getBaseSeat());
                latestEntity.setExpireAt(entity.getExpireAt());
                latestEntity.setAssignedAgentUid(entity.getAssignedAgentUid());
                latestEntity.setAssignedAt(entity.getAssignedAt());
                latestEntity.setReleasedAt(entity.getReleasedAt());
                latestEntity.setType(entity.getType());
                return agentSeatRepository.save(latestEntity);
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
        Optional<AgentSeatEntity> optional = agentSeatRepository.findByUid(uid);
        if (optional.isPresent()) {
            AgentSeatEntity entity = optional.get();

            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(AgentSeatPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }

            entity.setDeleted(true);
            save(entity);
            // agent_seatRepository.delete(optional.get());
        } else {
            throw new RuntimeException("AgentSeat not found");
        }
    }

    @Override
    public void delete(AgentSeatRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AgentSeatResponse convertToResponse(AgentSeatEntity entity) {
        return modelMapper.map(entity, AgentSeatResponse.class);
    }

    @Override
    public AgentSeatExcel convertToExcel(AgentSeatEntity entity) {
        return modelMapper.map(entity, AgentSeatExcel.class);
    }

    @Override
    protected Specification<AgentSeatEntity> createSpecification(AgentSeatRequest request) {
        return AgentSeatSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentSeatEntity> executePageQuery(Specification<AgentSeatEntity> spec, Pageable pageable) {
        return agentSeatRepository.findAll(spec, pageable);
    }

    public void initAgentSeats(String orgUid) {
        // log.info("initAgentSeatAgentSeat");
        // for (String agent_seat : AgentSeatInitData.getAllAgentSeats()) {
        // AgentSeatRequest agent_seatRequest = AgentSeatRequest.builder()
        // .uid(Utils.formatUid(orgUid, agent_seat))
        // .name(agent_seat)
        // .order(0)
        // .type(AgentSeatTypeEnum.THREAD.name())
        // .level(LevelEnum.ORGANIZATION.name())
        // .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        // .orgUid(orgUid)
        // .build();
        // createSystemAgentSeat(agent_seatRequest);
        // }
    }

}
