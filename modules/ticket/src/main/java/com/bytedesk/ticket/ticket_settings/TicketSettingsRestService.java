/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 18:05:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

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
public class TicketSettingsRestService extends BaseRestServiceWithExport<TicketSettingsEntity, TicketSettingsRequest, TicketSettingsResponse, TicketSettingsExcel> {

    private final TicketSettingsRepository ticketSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<TicketSettingsEntity> createSpecification(TicketSettingsRequest request) {
        return TicketSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketSettingsEntity> executePageQuery(Specification<TicketSettingsEntity> spec, Pageable pageable) {
        return ticketSettingsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "ticketSettings", key = "#uid", unless="#result==null")
    @Override
    public Optional<TicketSettingsEntity> findByUid(String uid) {
        return ticketSettingsRepository.findByUid(uid);
    }

    @Cacheable(value = "ticketSettings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TicketSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return ticketSettingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return ticketSettingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TicketSettingsResponse create(TicketSettingsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TicketSettingsEntity> ticketSettings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (ticketSettings.isPresent()) {
                return convertToResponse(ticketSettings.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TicketSettingsEntity entity = modelMapper.map(request, TicketSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TicketSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ticketSettings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TicketSettingsResponse update(TicketSettingsRequest request) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TicketSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TicketSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ticketSettings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    @Override
    protected TicketSettingsEntity doSave(TicketSettingsEntity entity) {
        return ticketSettingsRepository.save(entity);
    }

    @Override
    public TicketSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TicketSettingsEntity entity) {
        try {
            Optional<TicketSettingsEntity> latest = ticketSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return ticketSettingsRepository.save(latestEntity);
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
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ticketSettingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    @Override
    public void delete(TicketSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TicketSettingsResponse convertToResponse(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsResponse.class);
    }

    @Override
    public TicketSettingsExcel convertToExcel(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsExcel.class);
    }
    
    public void initTicketSettings(String orgUid) {
        // log.info("initThreadTicketSettings");
        // for (String ticketSettings : TicketSettingsInitData.getAllTicketSettingss()) {
        //     TicketSettingsRequest ticketSettingsRequest = TicketSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, ticketSettings))
        //             .name(ticketSettings)
        //             .order(0)
        //             .type(TicketSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(ticketSettingsRequest);
        // }
    }

    
    
}
