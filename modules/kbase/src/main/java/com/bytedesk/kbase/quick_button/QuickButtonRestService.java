/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-12
 * @Description: REST service for managing quick buttons
 */
package com.bytedesk.kbase.quick_button;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuickButtonRestService extends BaseRestService<QuickButtonEntity, QuickButtonRequest, QuickButtonResponse> {

    private final QuickButtonRepository quickButtonRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final AuthService authService;

    @Override
    protected Specification<QuickButtonEntity> createSpecification(QuickButtonRequest request) {
        return QuickButtonSpecification.search(request, authService);
    }

    @Override
    protected Page<QuickButtonEntity> executePageQuery(Specification<QuickButtonEntity> spec, Pageable pageable) {
        return quickButtonRepository.findAll(spec, pageable);
    }

    @Override
    @Cacheable(value = "quickbutton", key = "#uid", unless = "#result == null")
    public Optional<QuickButtonEntity> findByUid(String uid) {
        return quickButtonRepository.findByUid(uid);
    }

    @Override
    public QuickButtonResponse create(QuickButtonRequest request) {
        // 
        QuickButtonEntity entity = new QuickButtonEntity();
        entity.setTitle(request.getTitle());
        entity.setSubtitle(request.getSubtitle());
        entity.setDescription(request.getDescription());
        entity.setIcon(request.getIcon());
        entity.setColor(request.getColor());
        entity.setBadge(request.getBadge());
        entity.setCode(request.getCode());
        entity.setImageUrl(request.getImageUrl());
        entity.setOrderIndex(request.getOrderIndex());
        entity.setHighlight(request.getHighlight());
        entity.setEnabled(request.getEnabled());
        entity.setKbUid(request.getKbUid());
        entity.setContent(request.getContent());
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getUid());
        }
        entity.setType(MessageTypeEnum.fromValue(request.getType()).name());
        // 
        UserEntity currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            entity.setUserUid(currentUser.getUid());
            entity.setOrgUid(currentUser.getOrgUid());
        } else {
            throw new NotLoginException("Unauthorized access");
        }
        // 
        QuickButtonEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Failed to create quick button");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public QuickButtonResponse update(QuickButtonRequest request) {
        Optional<QuickButtonEntity> optional = findByUid(request.getUid());
        if (optional.isEmpty()) {
            throw new RuntimeException("quick_button not found");
        }

        QuickButtonEntity entity = optional.get();
        entity.setTitle(request.getTitle());
        entity.setSubtitle(request.getSubtitle());
        entity.setDescription(request.getDescription());
        entity.setIcon(request.getIcon());
        entity.setColor(request.getColor());
        entity.setBadge(request.getBadge());
        entity.setCode(request.getCode());
        entity.setImageUrl(request.getImageUrl());
        entity.setType(MessageTypeEnum.fromValue(request.getType()).name());
        entity.setOrderIndex(request.getOrderIndex());
        entity.setHighlight(request.getHighlight());
        entity.setEnabled(request.getEnabled());
        entity.setKbUid(request.getKbUid());
        entity.setContent(request.getContent());
        // entity.setPayload(request.getPayload());

        QuickButtonEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Failed to update quick button");
        }
        return convertToResponse(savedEntity);
    }

    public QuickButtonResponse enable(QuickButtonRequest request) {
        Optional<QuickButtonEntity> optional = findByUid(request.getUid());
        if (optional.isEmpty()) {
            throw new RuntimeException("quick_button not found");
        }

        QuickButtonEntity entity = optional.get();
        entity.setEnabled(request.getEnabled());
        QuickButtonEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Failed to update quick button status");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    @CacheEvict(value = "quickbutton", key = "#uid")
    public void deleteByUid(String uid) {
        Optional<QuickButtonEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    public void delete(QuickButtonRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public QuickButtonEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QuickButtonEntity entity) {
        Optional<QuickButtonEntity> latest = quickButtonRepository.findByUid(entity.getUid());
        if (latest.isPresent()) {
            QuickButtonEntity latestEntity = latest.get();
            // latestEntity.setDeleted(entity.getDeleted());
            latestEntity.setEnabled(entity.getEnabled());
            return quickButtonRepository.save(latestEntity);
        }
        throw new RuntimeException("Unable to resolve optimistic locking failure", e);
    }

    @Override
    public QuickButtonResponse convertToResponse(QuickButtonEntity entity) {
        return modelMapper.map(entity, QuickButtonResponse.class);
    }

    @Override
    @CachePut(value = "quickbutton", key = "#entity.uid")
    protected QuickButtonEntity doSave(QuickButtonEntity entity) {
        return quickButtonRepository.save(entity);
    }
}