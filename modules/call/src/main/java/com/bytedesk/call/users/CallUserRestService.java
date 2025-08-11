package com.bytedesk.call.users;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CallUserRestService extends BaseRestServiceWithExcel<CallUserEntity, CallUserRequest, CallUserResponse, CallUserExcel> {

    private final CallUserRepository freeSwitchNumberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CallUserEntity> queryByOrgEntity(CallUserRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallUserEntity> specification = CallUserSpecification.search(request);
        return freeSwitchNumberRepository.findAll(specification, pageable);
    }

    @Override
    public Page<CallUserResponse> queryByOrg(CallUserRequest request) {
        Page<CallUserEntity> entities = queryByOrgEntity(request);
        return entities.map(this::convertToResponse);
    }

    public Page<CallUserEntity> queryByNumberEntity(CallUserRequest request) {
        // NumberEntity user = authService.getUser();
        // request.setOrgUid(user.getOrgUid());
        return queryByOrgEntity(request);
    }

    @Override
    public Page<CallUserResponse> queryByUser(CallUserRequest request) {
        Page<CallUserEntity> entities = queryByNumberEntity(request);
        return entities.map(this::convertToResponse);
    }

    @Override
    public Optional<CallUserEntity> findByUid(String uid) {
        return freeSwitchNumberRepository.findByUid(uid);
    }

    @Override
    public CallUserResponse convertToResponse(CallUserEntity entity) {
        return modelMapper.map(entity, CallUserResponse.class);
    }

    public CallUserEntity convertToEntity(CallUserRequest request) {
        return modelMapper.map(request, CallUserEntity.class);
    }

    @Override
    public CallUserExcel convertToExcel(CallUserEntity entity) {
        return modelMapper.map(entity, CallUserExcel.class);
    }

    @Override
    public CallUserResponse create(CallUserRequest request) {
        UserEntity user = authService.getUser();
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (!StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getCacheSerialUid());
        }

        // 检查用户名是否已存在
        if (freeSwitchNumberRepository.existsByUsernameAndDomain(request.getUsername(), request.getDomain())) {
            throw new RuntimeException("用户名已存在: " + request.getUsername() + "@" + request.getDomain());
        }

        CallUserEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        CallUserEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建Call用户失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public CallUserResponse update(CallUserRequest request) {
        Optional<CallUserEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("Call用户不存在");
        }

        CallUserEntity entity = optional.get();
        
        // 更新字段
        if (StringUtils.hasText(request.getDisplayName())) {
            entity.setDisplayName(request.getDisplayName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            entity.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAccountcode())) {
            entity.setAccountcode(request.getAccountcode());
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
        if (StringUtils.hasText(request.getRemarks())) {
            entity.setRemarks(request.getRemarks());
        }

        CallUserEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新Call用户失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public CallUserEntity save(CallUserEntity entity) {
        try {
            return freeSwitchNumberRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    public CallUserEntity doSave(CallUserEntity entity) {
        return freeSwitchNumberRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CallUserEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    public void delete(CallUserRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    @Cacheable(value = "freeswitch_user", key = "#uid", unless = "#result == null")
    public CallUserResponse queryByUid(CallUserRequest request) {
        Optional<CallUserEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("Call用户不存在");
    }

    @Override
    public CallUserEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallUserEntity entity) {
        log.warn("Call用户保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        // 重新查询最新版本并重试
        try {
            Optional<CallUserEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallUserEntity latestEntity = latest.get();
                // 将当前修改应用到最新版本
                latestEntity.setPassword(entity.getPassword());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setDisplayName(entity.getDisplayName());
                latestEntity.setEmail(entity.getEmail());
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

}
