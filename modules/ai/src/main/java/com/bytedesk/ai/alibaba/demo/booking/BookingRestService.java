/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 10:28:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.booking;

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
import com.bytedesk.core.base.BaseRestServiceWithExcel;
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
public class BookingRestService extends BaseRestServiceWithExcel<BookingEntity, BookingRequest, BookingResponse, BookingExcel> {

    private final BookingRepository bookingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<BookingEntity> queryByOrgEntity(BookingRequest request) {
        Pageable pageable = request.getPageable();
        Specification<BookingEntity> spec = BookingSpecification.search(request);
        return bookingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<BookingResponse> queryByOrg(BookingRequest request) {
        Page<BookingEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<BookingResponse> queryByUser(BookingRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public BookingResponse queryByUid(BookingRequest request) {
        Optional<BookingEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            BookingEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("Booking not found");
        }
    }

    @Cacheable(value = "booking", key = "#uid", unless="#result==null")
    @Override
    public Optional<BookingEntity> findByUid(String uid) {
        return bookingRepository.findByUid(uid);
    }

    @Cacheable(value = "booking", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<BookingEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return bookingRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return bookingRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public BookingResponse create(BookingRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<BookingEntity> booking = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (booking.isPresent()) {
                return convertToResponse(booking.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        BookingEntity entity = modelMapper.map(request, BookingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        BookingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create booking failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public BookingResponse update(BookingRequest request) {
        Optional<BookingEntity> optional = bookingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            BookingEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            BookingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update booking failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Booking not found");
        }
    }

    @Override
    protected BookingEntity doSave(BookingEntity entity) {
        return bookingRepository.save(entity);
    }

    @Override
    public BookingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, BookingEntity entity) {
        try {
            Optional<BookingEntity> latest = bookingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                BookingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return bookingRepository.save(latestEntity);
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
        Optional<BookingEntity> optional = bookingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // bookingRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Booking not found");
        }
    }

    @Override
    public void delete(BookingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public BookingResponse convertToResponse(BookingEntity entity) {
        return modelMapper.map(entity, BookingResponse.class);
    }

    @Override
    public BookingExcel convertToExcel(BookingEntity entity) {
        return modelMapper.map(entity, BookingExcel.class);
    }
    
    public void initBookings(String orgUid) {
        // log.info("initThreadBooking");
        for (String booking : BookingInitData.getAllBookings()) {
            BookingRequest bookingRequest = BookingRequest.builder()
                    .uid(Utils.formatUid(orgUid, booking))
                    .name(booking)
                    .order(0)
                    .type(BookingClassEnum.ECONOMY.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(bookingRequest);
        }
    }
    
}
