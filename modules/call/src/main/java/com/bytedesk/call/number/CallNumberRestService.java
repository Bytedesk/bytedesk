package com.bytedesk.call.number;

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
public class CallNumberRestService extends BaseRestServiceWithExcel<CallNumberEntity, CallNumberRequest, CallNumberResponse, CallNumberExcel> {

    private final CallNumberRepository freeSwitchNumberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CallNumberEntity> queryByOrgEntity(CallNumberRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallNumberEntity> specification = CallNumberSpecification.search(request);
        return freeSwitchNumberRepository.findAll(specification, pageable);
    }

    @Override
    public Page<CallNumberResponse> queryByOrg(CallNumberRequest request) {
        Page<CallNumberEntity> entities = queryByOrgEntity(request);
        return entities.map(this::convertToResponse);
    }

    public Page<CallNumberEntity> queryByNumberEntity(CallNumberRequest request) {
        // NumberEntity user = authService.getUser();
        // request.setOrgUid(user.getOrgUid());
        return queryByOrgEntity(request);
    }

    @Override
    public Page<CallNumberResponse> queryByUser(CallNumberRequest request) {
        Page<CallNumberEntity> entities = queryByNumberEntity(request);
        return entities.map(this::convertToResponse);
    }

    @Override
    public Optional<CallNumberEntity> findByUid(String uid) {
        return freeSwitchNumberRepository.findByUid(uid);
    }

    @Override
    public CallNumberResponse convertToResponse(CallNumberEntity entity) {
        return modelMapper.map(entity, CallNumberResponse.class);
    }

    public CallNumberEntity convertToEntity(CallNumberRequest request) {
        return modelMapper.map(request, CallNumberEntity.class);
    }

    @Override
    public CallNumberExcel convertToExcel(CallNumberEntity entity) {
        return modelMapper.map(entity, CallNumberExcel.class);
    }

    @Override
    public CallNumberResponse create(CallNumberRequest request) {
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

        CallNumberEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        CallNumberEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建Call用户失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public CallNumberResponse update(CallNumberRequest request) {
        Optional<CallNumberEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("Call用户不存在");
        }

        CallNumberEntity entity = optional.get();
        
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

        CallNumberEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新Call用户失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public CallNumberEntity save(CallNumberEntity entity) {
        try {
            return freeSwitchNumberRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    public CallNumberEntity doSave(CallNumberEntity entity) {
        return freeSwitchNumberRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CallNumberEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    public void delete(CallNumberRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    @Cacheable(value = "freeswitch_user", key = "#uid", unless = "#result == null")
    public CallNumberResponse queryByUid(CallNumberRequest request) {
        Optional<CallNumberEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("Call用户不存在");
    }

    @Override
    public CallNumberEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallNumberEntity entity) {
        log.warn("Call用户保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        // 重新查询最新版本并重试
        try {
            Optional<CallNumberEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallNumberEntity latestEntity = latest.get();
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
