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
public class FreeSwitchNumberRestService extends BaseRestServiceWithExcel<FreeSwitchNumberEntity, FreeSwitchNumberRequest, FreeSwitchNumberResponse, FreeSwitchNumberExcel> {

    private final FreeSwitchNumberRepository freeSwitchNumberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FreeSwitchNumberEntity> queryByOrgEntity(FreeSwitchNumberRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FreeSwitchNumberEntity> specification = FreeSwitchNumberSpecification.search(request);
        return freeSwitchNumberRepository.findAll(specification, pageable);
    }

    @Override
    public Page<FreeSwitchNumberResponse> queryByOrg(FreeSwitchNumberRequest request) {
        Page<FreeSwitchNumberEntity> entities = queryByOrgEntity(request);
        return entities.map(this::convertToResponse);
    }

    public Page<FreeSwitchNumberEntity> queryByNumberEntity(FreeSwitchNumberRequest request) {
        // NumberEntity user = authService.getUser();
        // request.setOrgUid(user.getOrgUid());
        return queryByOrgEntity(request);
    }

    @Override
    public Page<FreeSwitchNumberResponse> queryByUser(FreeSwitchNumberRequest request) {
        Page<FreeSwitchNumberEntity> entities = queryByNumberEntity(request);
        return entities.map(this::convertToResponse);
    }

    @Override
    public Optional<FreeSwitchNumberEntity> findByUid(String uid) {
        return freeSwitchNumberRepository.findByUid(uid);
    }

    @Override
    public FreeSwitchNumberResponse convertToResponse(FreeSwitchNumberEntity entity) {
        return modelMapper.map(entity, FreeSwitchNumberResponse.class);
    }

    public FreeSwitchNumberEntity convertToEntity(FreeSwitchNumberRequest request) {
        return modelMapper.map(request, FreeSwitchNumberEntity.class);
    }

    @Override
    public FreeSwitchNumberExcel convertToExcel(FreeSwitchNumberEntity entity) {
        return modelMapper.map(entity, FreeSwitchNumberExcel.class);
    }

    @Override
    public FreeSwitchNumberResponse create(FreeSwitchNumberRequest request) {
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

        FreeSwitchNumberEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        FreeSwitchNumberEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建FreeSwitch用户失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public FreeSwitchNumberResponse update(FreeSwitchNumberRequest request) {
        Optional<FreeSwitchNumberEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("FreeSwitch用户不存在");
        }

        FreeSwitchNumberEntity entity = optional.get();
        
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

        FreeSwitchNumberEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新FreeSwitch用户失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public FreeSwitchNumberEntity save(FreeSwitchNumberEntity entity) {
        try {
            return freeSwitchNumberRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    public FreeSwitchNumberEntity doSave(FreeSwitchNumberEntity entity) {
        return freeSwitchNumberRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FreeSwitchNumberEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    public void delete(FreeSwitchNumberRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    @Cacheable(value = "freeswitch_user", key = "#uid", unless = "#result == null")
    public FreeSwitchNumberResponse queryByUid(FreeSwitchNumberRequest request) {
        Optional<FreeSwitchNumberEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("FreeSwitch用户不存在");
    }

    @Override
    public FreeSwitchNumberEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FreeSwitchNumberEntity entity) {
        log.warn("FreeSwitch用户保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        // 重新查询最新版本并重试
        try {
            Optional<FreeSwitchNumberEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                FreeSwitchNumberEntity latestEntity = latest.get();
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
