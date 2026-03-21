/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 16:36:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
        
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlackRestService extends BaseRestServiceWithExport<BlackEntity, BlackRequest, BlackResponse, BlackExcel> {

    private final BlackRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<BlackEntity> createSpecification(BlackRequest request) {
        return BlackSpecification.search(request, authService);
    }

    @Override
    protected Page<BlackEntity> executePageQuery(Specification<BlackEntity> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Cacheable(value = "black", key = "#uid", unless = "#result == null")
    @Override
    public Optional<BlackEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    public List<BlackEntity> findByEndTimeBefore(ZonedDateTime endTime) {
        return repository.findByEndTimeBeforeAndDeletedFalse(endTime);
    }

    @Cacheable(value = "black", key = "'platform_' + #blackUid", unless = "#result == null")
    public Optional<BlackEntity> findPlatformBlackByUid(String blackUid) {
        return repository.findFirstByBlackUidAndLevelAndDeletedFalse(blackUid, LevelEnum.PLATFORM.name());
    }

    @Cacheable(value = "blacks", key = "#visitorUid + '_' + #orgUid", unless = "#result == null")
    public Optional<BlackEntity> findByVisitorUidAndOrgUid(String visitorUid, String orgUid) {
        return repository.findFirstByBlackUidAndOrgUidAndLevelAndDeletedFalse(
                visitorUid,
                orgUid,
                LevelEnum.ORGANIZATION.name());
    }

    @Cacheable(value = "blacks", key = "#blackUid + '_' + #orgUid + '_' + #level", unless = "#result == null")
    public Optional<BlackEntity> findByBlackUidAndScope(String blackUid, String orgUid, String level) {
        String normalizedLevel = normalizeLevel(level);
        if (LevelEnum.PLATFORM.name().equals(normalizedLevel)) {
            return findPlatformBlackByUid(blackUid);
        }
        if (!StringUtils.hasText(orgUid)) {
            return Optional.empty();
        }
        return repository.findFirstByBlackUidAndOrgUidAndLevelAndDeletedFalse(blackUid, orgUid, normalizedLevel);
    }

    public Optional<BlackEntity> findActiveByBlackUidAndOrgUid(String blackUid, String orgUid) {
        Optional<BlackEntity> platformBlack = findPlatformBlackByUid(blackUid);
        if (platformBlack.isPresent()) {
            return platformBlack;
        }
        if (!StringUtils.hasText(orgUid)) {
            return Optional.empty();
        }
        return findByVisitorUidAndOrgUid(blackUid, orgUid);
    }
    
    public Boolean existsByBlackUid(BlackRequest request) {
        String level = normalizeLevel(request.getLevel());
        if (LevelEnum.PLATFORM.name().equals(level)) {
            return findByBlackUidAndScope(request.getBlackUid(), request.getOrgUid(), level).isPresent();
        }
        return findActiveByBlackUidAndOrgUid(request.getBlackUid(), request.getOrgUid()).isPresent();
    }

    public void unblockByBlackUid(BlackRequest request) {
        Optional<BlackEntity> black = findByBlackUidAndScope(request.getBlackUid(), request.getOrgUid(), request.getLevel());
        if (black.isPresent()) {
            BlackEntity entity = black.get();
            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException("unblockByBlackUid Black not found " + request.getBlackUid());
        }
    }

    @Override
    public BlackResponse create(BlackRequest request) {
        String level = normalizeLevel(request.getLevel());
        request.setLevel(level);

        Optional<BlackEntity> black = LevelEnum.PLATFORM.name().equals(level)
                ? findByBlackUidAndScope(request.getBlackUid(), request.getOrgUid(), level)
                : findActiveByBlackUidAndOrgUid(request.getBlackUid(), request.getOrgUid());
        if (black.isPresent()) {
            return convertToResponse(black.get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_USER_NOT_FOUND);
        }
        //
        BlackEntity entity = modelMapper.map(request, BlackEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setLevel(level);
        entity.setUserUid(user.getUid());
        entity.setUserNickname(user.getNickname());
        entity.setUserAvatar(user.getAvatar());
        //
        BlackEntity savedBlack = save(entity);
        if (savedBlack == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedBlack);
    }

    @Override
    public BlackResponse update(BlackRequest request) {
        Optional<BlackEntity> black = findByUid(request.getUid());
        if (black.isPresent()) {
            BlackEntity entity = black.get();
            modelMapper.map(request, entity);
            // 
            BlackEntity savedBlack = save(entity);
            if (savedBlack == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedBlack);
        } else {
            throw new RuntimeException("Black not found");
        }
    }

    @Override
    protected BlackEntity doSave(BlackEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<BlackEntity> black = findByUid(uid);
        if (black.isPresent()) {
            BlackEntity entity = black.get();
            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException("deleteByUid Black not found " + uid);
        }
    }

    @Override
    public void delete(BlackRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public BlackEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, BlackEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public BlackResponse convertToResponse(BlackEntity entity) {
        return modelMapper.map(entity, BlackResponse.class);
    }
    
    @Override
    public BlackExcel convertToExcel(BlackEntity entity) {
        return modelMapper.map(entity, BlackExcel.class);
    }

    public BlackResponse createFromExcelRow(BlackExcel row, String orgUid) {
        if (row == null) {
            throw new RuntimeException("Black import row is null");
        }
        String blackUid = row.getBlackUid();
        if (!StringUtils.hasText(blackUid)) {
            throw new RuntimeException("Black import blackUid is required");
        }
        BlackRequest request = BlackRequest.builder()
            .blackUid(blackUid)
                .blackNickname(row.getBlackNickname())
                .reason(row.getReason())
                .blockIp(parseBlockIp(row.getBlockIp()))
                .userNickname(row.getUserNickname())
                .startTime(row.getStartTime())
                .endTime(row.getEndTime())
                .build();
        request.setOrgUid(orgUid);
        return create(request);
    }

    private Boolean parseBlockIp(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return "true".equals(normalized)
                || "1".equals(normalized)
                || "yes".equals(normalized)
                || "y".equals(normalized)
                || "是".equals(value.trim());
    }

    private String normalizeLevel(String level) {
        if (!StringUtils.hasText(level)) {
            return LevelEnum.ORGANIZATION.name();
        }
        return LevelEnum.fromValue(level).name();
    }

    

}
