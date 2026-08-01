/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:57:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.holiday;

import java.time.LocalDate;
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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class HolidayRestService extends BaseRestService<HolidayEntity, HolidayRequest, HolidayResponse> {

    private final HolidayRepository holidayRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<HolidayEntity> createSpecification(HolidayRequest request) {
        return HolidaySpecification.search(request, authService);
    }

    @Override
    protected Page<HolidayEntity> executePageQuery(Specification<HolidayEntity> spec, Pageable pageable) {
        return holidayRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "holiday", key = "#uid", unless="#result==null")
    @Override
    public Optional<HolidayEntity> findByUid(String uid) {
        return holidayRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return holidayRepository.existsByUid(uid);
    }

    public boolean isOfficialOffDay(String countryCode, LocalDate holidayDate) {
        String resolvedCountryCode = StringUtils.hasText(countryCode) ? countryCode : "CN";
        return holidayRepository
            .findFirstByCountryCodeAndHolidayDateAndOffDayTrueAndDeletedFalse(resolvedCountryCode, holidayDate)
            .map(HolidayEntity::getOfficial)
            .orElse(false);
    }

    public List<HolidayEntity> findByCountryAndYear(String countryCode, Integer holidayYear) {
        String resolvedCountryCode = StringUtils.hasText(countryCode) ? countryCode : "CN";
        return holidayRepository.findByCountryCodeAndHolidayYearAndDeletedFalseOrderByHolidayDateAsc(resolvedCountryCode, holidayYear);
    }

    @Override
    public HolidayResponse create(HolidayRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        if (!StringUtils.hasText(request.getType())) {
            request.setType(HolidayTypeEnum.OFFICIAL.name());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        HolidayEntity entity = modelMapper.map(request, HolidayEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        HolidayEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create holiday failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public HolidayResponse update(HolidayRequest request) {
        Optional<HolidayEntity> optional = holidayRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            HolidayEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            HolidayEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update holiday failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Holiday not found");
        }
    }

    @Override
    protected HolidayEntity doSave(HolidayEntity entity) {
        // log.info("Saving holiday: {}", entity.getName());
        return holidayRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<HolidayEntity> optional = holidayRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // holidayRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Holiday not found");
        }
    }

    @Override
    public void delete(HolidayRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public HolidayEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, HolidayEntity entity) {
        try {
            Optional<HolidayEntity> latest = holidayRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                HolidayEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setType(entity.getType());
                latestEntity.setHolidayDate(entity.getHolidayDate());
                latestEntity.setHolidayYear(entity.getHolidayYear());
                latestEntity.setCountryCode(entity.getCountryCode());
                latestEntity.setOffDay(entity.getOffDay());
                latestEntity.setOfficial(entity.getOfficial());
                latestEntity.setSourceUrl(entity.getSourceUrl());
                latestEntity.setHolidayKey(entity.getHolidayKey());
                return holidayRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public HolidayResponse convertToResponse(HolidayEntity entity) {
        return modelMapper.map(entity, HolidayResponse.class);
    }

    public void initOrganizationHolidays(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            throw new RuntimeException("orgUid is required");
        }

        for (HolidayInitData.HolidaySeed seed : HolidayInitData.getChinaOfficialHolidays2026()) {
            HolidayRequest request = HolidayRequest.builder()
                .uid(Utils.formatUid(orgUid, seed.countryCode() + "_" + seed.holidayDate()))
                .orgUid(orgUid)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .name(seed.name())
                .description(seed.description())
                .type(HolidayTypeEnum.OFFICIAL.name())
                .holidayDate(seed.holidayDate())
                .holidayYear(seed.holidayDate().getYear())
                .countryCode(seed.countryCode())
                .offDay(seed.offDay())
                .official(Boolean.TRUE)
                .sourceUrl(seed.sourceUrl())
                .holidayKey(seed.holidayKey())
                .build();
            upsertSystemHoliday(request);
        }
    }

    private void upsertSystemHoliday(HolidayRequest request) {
        Optional<HolidayEntity> optional = findByUid(request.getUid());
        if (optional.isEmpty()) {
            createSystemHoliday(request);
            return;
        }

        HolidayEntity entity = optional.get();
        modelMapper.map(request, entity);
        entity.setDeleted(false);
        HolidayEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Update holiday failed");
        }
    }

    private void createSystemHoliday(HolidayRequest request) {
        HolidayEntity entity = modelMapper.map(request, HolidayEntity.class);
        HolidayEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create holiday failed");
        }
    }

}
