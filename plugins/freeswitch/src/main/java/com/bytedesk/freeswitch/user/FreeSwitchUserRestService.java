package com.bytedesk.freeswitch.user;

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
public class FreeSwitchUserRestService extends BaseRestServiceWithExcel<FreeSwitchUserEntity, FreeSwitchUserRequest, FreeSwitchUserResponse, FreeSwitchUserExcel> {

    private final FreeSwitchUserRepository freeSwitchUserRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FreeSwitchUserEntity> queryByOrgEntity(FreeSwitchUserRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FreeSwitchUserEntity> specification = FreeSwitchUserSpecification.search(request);
        return freeSwitchUserRepository.findAll(specification, pageable);
    }

    @Override
    public Page<FreeSwitchUserResponse> queryByOrg(FreeSwitchUserRequest request) {
        Page<FreeSwitchUserEntity> entities = queryByOrgEntity(request);
        return entities.map(this::convertToResponse);
    }

    public Page<FreeSwitchUserEntity> queryByUserEntity(FreeSwitchUserRequest request) {
        UserEntity user = authService.getUser();
        request.setOrgUid(user.getOrgUid());
        return queryByOrgEntity(request);
    }

    @Override
    public Page<FreeSwitchUserResponse> queryByUser(FreeSwitchUserRequest request) {
        Page<FreeSwitchUserEntity> entities = queryByUserEntity(request);
        return entities.map(this::convertToResponse);
    }

    @Override
    public Optional<FreeSwitchUserEntity> findByUid(String uid) {
        return freeSwitchUserRepository.findByUid(uid);
    }

    @Override
    public FreeSwitchUserResponse convertToResponse(FreeSwitchUserEntity entity) {
        return modelMapper.map(entity, FreeSwitchUserResponse.class);
    }

    public FreeSwitchUserEntity convertToEntity(FreeSwitchUserRequest request) {
        return modelMapper.map(request, FreeSwitchUserEntity.class);
    }

    @Override
    public FreeSwitchUserExcel convertToExcel(FreeSwitchUserEntity entity) {
        return modelMapper.map(entity, FreeSwitchUserExcel.class);
    }

    @Override
    public FreeSwitchUserResponse create(FreeSwitchUserRequest request) {
        UserEntity user = authService.getUser();
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (!StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getCacheSerialUid());
        }

        // 检查用户名是否已存在
        if (freeSwitchUserRepository.existsByUsernameAndDomain(request.getUsername(), request.getDomain())) {
            throw new RuntimeException("用户名已存在: " + request.getUsername() + "@" + request.getDomain());
        }

        FreeSwitchUserEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        FreeSwitchUserEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建FreeSwitch用户失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public FreeSwitchUserResponse update(FreeSwitchUserRequest request) {
        Optional<FreeSwitchUserEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("FreeSwitch用户不存在");
        }

        FreeSwitchUserEntity entity = optional.get();
        
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

        FreeSwitchUserEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新FreeSwitch用户失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public FreeSwitchUserEntity save(FreeSwitchUserEntity entity) {
        try {
            return freeSwitchUserRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    public FreeSwitchUserEntity doSave(FreeSwitchUserEntity entity) {
        return freeSwitchUserRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FreeSwitchUserEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    public void delete(FreeSwitchUserRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    @Cacheable(value = "freeswitch_user", key = "#uid", unless = "#result == null")
    public FreeSwitchUserResponse queryByUid(FreeSwitchUserRequest request) {
        Optional<FreeSwitchUserEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("FreeSwitch用户不存在");
    }

    @Override
    public FreeSwitchUserEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FreeSwitchUserEntity entity) {
        log.warn("FreeSwitch用户保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        // 重新查询最新版本并重试
        try {
            Optional<FreeSwitchUserEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                FreeSwitchUserEntity latestEntity = latest.get();
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
