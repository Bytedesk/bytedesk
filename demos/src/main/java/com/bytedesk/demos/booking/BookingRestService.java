/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:40:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.demos.booking;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.demos.consumer.ConsumerRestService;
import com.bytedesk.demos.consumer.ConsumerEntity;
import com.bytedesk.demos.consumer.ConsumerRequest;
import com.bytedesk.demos.consumer.ConsumerResponse;
import com.bytedesk.demos.consumer.ConsumerTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class BookingRestService extends BaseRestServiceWithExport<BookingEntity, BookingRequest, BookingResponse, BookingExcel> {

    private final BookingRepository bookingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ConsumerRestService consumerRestService;

    @Override
    protected Specification<BookingEntity> createSpecification(BookingRequest request) {
        return BookingSpecification.search(request, authService);
    }

    @Override
    protected Page<BookingEntity> executePageQuery(Specification<BookingEntity> spec, Pageable pageable) {
        return bookingRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "booking", key = "#uid", unless="#result==null")
    @Override
    public Optional<BookingEntity> findByUid(String uid) {
        return bookingRepository.findByUid(uid);
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
    
    // 初始化预订，来自于spring ai alibaba demo
    public void initBookings(String orgUid) {

        if (bookingRepository.count() > 0) {
            log.info("Bookings already initialized for orgUid: {}", orgUid);
            return;
        }
        log.info("Initializing demo bookings for orgUid: {}", orgUid);

        List<String> names = List.of("张三", "李四", "王五", "赵六", "钱七");
        List<String> airportCodes = List.of("北京", "上海", "广州", "深圳", "杭州", "南京", "青岛", "成都", "武汉", "西安", "重庆", "大连", "天津");
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            String name = names.get(i);
            String from = airportCodes.get(random.nextInt(airportCodes.size()));
            String to = airportCodes.get(random.nextInt(airportCodes.size()));
            
            // 创建消费者
            ConsumerRequest consumerRequest = ConsumerRequest.builder()
                    .uid(Utils.formatUid(orgUid, "consumer_" + (i + 1)))
                    .name(name)
                    .description("Demo consumer " + (i + 1))
                    .type(ConsumerTypeEnum.BOOKING.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            ConsumerResponse consumerResponse = consumerRestService.create(consumerRequest);
            
            // 创建预订
            BookingClassEnum bookingClass = BookingClassEnum.values()[random.nextInt(BookingClassEnum.values().length)];
            LocalDate date = LocalDate.now().plusDays(2 * (i + 1));
            // 
            BookingRequest bookingRequest = BookingRequest.builder()
                    .uid(Utils.formatUid(orgUid, "booking_" + (i + 1)))
                    .bookingNumber("10" + (i + 1))
                    .bookingDate(date.atStartOfDay(ZoneId.systemDefault()))
                    .status(BookingStatusEnum.CONFIRMED.name())
                    .from(from)
                    .to(to)
                    .bookingClass(bookingClass.name())
                    .consumerUid(consumerResponse.getUid())
                    .orgUid(orgUid)
                    .build();
            
            // 创建预订实体并设置详细信息
            BookingEntity bookingEntity = modelMapper.map(bookingRequest, BookingEntity.class);
            bookingEntity.setBookingNumber("10" + (i + 1));
            bookingEntity.setBookingDate(date.atStartOfDay(ZoneId.systemDefault()));
            bookingEntity.setStatus(BookingStatusEnum.CONFIRMED.name());
            bookingEntity.setFrom(from);
            bookingEntity.setTo(to);
            bookingEntity.setBookingClass(bookingClass.name());
            
            // 设置消费者关联
            Optional<ConsumerEntity> consumerEntity = 
                consumerRestService.findByUid(consumerResponse.getUid());
            if (consumerEntity.isPresent()) {
                bookingEntity.setConsumer(consumerEntity.get());
            }
            
            // 保存预订
            save(bookingEntity);
            
            log.info("Created booking: {} for consumer: {}", bookingEntity.getBookingNumber(), name);
        }
        
        log.info("Demo bookings initialization completed for orgUid: {}", orgUid);
    }
    
}
