/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 17:46:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.announcement;

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
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AnnouncementRestService extends BaseRestServiceWithExport<AnnouncementEntity, AnnouncementRequest, AnnouncementResponse, AnnouncementExcel> {

    private final AnnouncementRepository announcementRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<AnnouncementEntity> createSpecification(AnnouncementRequest request) {
        return AnnouncementSpecification.search(request, authService);
    }

    @Override
    protected Page<AnnouncementEntity> executePageQuery(Specification<AnnouncementEntity> spec, Pageable pageable) {
        return announcementRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "announcement", key = "#uid", unless="#result==null")
    @Override
    public Optional<AnnouncementEntity> findByUid(String uid) {
        return announcementRepository.findByUid(uid);
    }

    @Cacheable(value = "announcement", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<AnnouncementEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return announcementRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return announcementRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public AnnouncementResponse create(AnnouncementRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<AnnouncementEntity> announcement = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (announcement.isPresent()) {
                return convertToResponse(announcement.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        AnnouncementEntity entity = modelMapper.map(request, AnnouncementEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        AnnouncementEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create announcement failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public AnnouncementResponse update(AnnouncementRequest request) {
        Optional<AnnouncementEntity> optional = announcementRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AnnouncementEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            AnnouncementEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update announcement failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Announcement not found");
        }
    }

    @Override
    protected AnnouncementEntity doSave(AnnouncementEntity entity) {
        return announcementRepository.save(entity);
    }

    @Override
    public AnnouncementEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AnnouncementEntity entity) {
        try {
            Optional<AnnouncementEntity> latest = announcementRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AnnouncementEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return announcementRepository.save(latestEntity);
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
        Optional<AnnouncementEntity> optional = announcementRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // announcementRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Announcement not found");
        }
    }

    @Override
    public void delete(AnnouncementRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AnnouncementResponse convertToResponse(AnnouncementEntity entity) {
        return modelMapper.map(entity, AnnouncementResponse.class);
    }

    @Override
    public AnnouncementExcel convertToExcel(AnnouncementEntity entity) {
        return modelMapper.map(entity, AnnouncementExcel.class);
    }
    
    public void initAnnouncements(String orgUid) {
        // log.info("initThreadAnnouncement");
        // for (String announcement : AnnouncementInitData.getAllAnnouncements()) {
        //     AnnouncementRequest announcementRequest = AnnouncementRequest.builder()
        //             .uid(Utils.formatUid(orgUid, announcement))
        //             .name(announcement)
        //             .type(AnnouncementTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(announcementRequest);
        // }
    }

    
    
}
