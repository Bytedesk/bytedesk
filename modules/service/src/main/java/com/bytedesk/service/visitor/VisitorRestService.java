/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-07 16:30:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.robot_to_agent_settings.event.VisitorRobotMessageEvent;
import com.bytedesk.service.routing_strategy.ThreadRoutingContext;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Description("Visitor Management Service - Visitor information and interaction management service")
public class VisitorRestService extends BaseRestServiceWithExport<VisitorEntity, VisitorRequest, VisitorResponse, VisitorExcel> {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRoutingContext threadRoutingContext;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected Specification<VisitorEntity> createSpecification(VisitorRequest request) {
        return VisitorSpecification.search(request, authService);
    }

    @Override
    protected Page<VisitorEntity> executePageQuery(Specification<VisitorEntity> spec, Pageable pageable) {
        return visitorRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public VisitorResponse create(VisitorRequest request) {
        // 
        String visitorUid = request.getVisitorUid();
        // 不能去掉，否则会不断生成新访客
        if (!StringUtils.hasText(visitorUid)) {
            visitorUid = request.getUid();
        }
        String orgUid = request.getOrgUid();
        // 
        log.info("visitor init, visitorUid: {}, orgUid: {}", visitorUid, orgUid);
        Optional<VisitorEntity> visitorOptional = findByVisitorUidAndOrgUid(visitorUid, orgUid);
        if (visitorOptional.isPresent()) {
            VisitorEntity visitor = visitorOptional.get();
            // 如果访客信息已存在，则更新访客信息
            if (StringUtils.hasText(request.getNickname())) {
                visitor.setNickname(request.getNickname());
            }
            if (StringUtils.hasText(request.getAvatar())) {
                visitor.setAvatar(request.getAvatar());
            }
            if (StringUtils.hasText(request.getMobile())) {
                visitor.setMobile(request.getMobile());
            }
            if (StringUtils.hasText(request.getEmail())) {
                visitor.setEmail(request.getEmail());
            }
            if (StringUtils.hasText(request.getNote())) {
                visitor.setNote(request.getNote());
            }
            // 对比ip是否有变化
            if (visitor.getIp() == null || !visitor.getIp().equals(request.getIp())) {
                // 更新浏览信息
                visitor.setIp(request.getIp());
                visitor.setIpLocation(request.getIpLocation());
            }
            visitor.setVipLevel(request.getVipLevel());
            if (visitor.getDeviceInfo() == null) {
                visitor.setDeviceInfo(new VisitorDevice());
            }
            visitor.getDeviceInfo().setBrowser(request.getBrowser());
            visitor.getDeviceInfo().setOs(request.getOs());
            visitor.getDeviceInfo().setDevice(request.getDevice());
            visitor.setExtra(request.getExtra());
            // 
            VisitorEntity savedVisitor = save(visitor);
            if (savedVisitor == null) {
                throw new RuntimeException("visitor not saved");
            }
            return convertToResponse(savedVisitor);
        }
        // 如果用户不存在，则创建新用户
        if (!StringUtils.hasText(request.getAvatar())) {
            request.setAvatar(AvatarConsts.getAvatar(request.getChannel()));
        }
        // uid使用自动生成的uid，防止前端uid冲突
        request.setUid(uidUtils.getUid());
        // 如果前端没有传递visitorUid，则使用uid作为visitorUid
        if (!StringUtils.hasText(request.getVisitorUid())) {
            request.setVisitorUid(request.getUid());
        }
        // log.info("request {}", request);
        VisitorEntity visitor = modelMapper.map(request, VisitorEntity.class);
        VisitorDevice device = modelMapper.map(request, VisitorDevice.class);
        visitor.setDeviceInfo(device);
        //
        VisitorEntity savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        //
        return convertToResponse(savedVisitor);
    }

    @Transactional
    @Override
    public VisitorResponse update(VisitorRequest request) {
        Optional<VisitorEntity> visitorOptional = findByUid(request.getUid());
        if (visitorOptional.isEmpty()) {
            throw new NotFoundException("visitor not found");
        }
        // 
        VisitorEntity visitor = visitorOptional.get();
        visitor.setNickname(request.getNickname());
        visitor.setAvatar(request.getAvatar());
        visitor.setMobile(request.getMobile());
        visitor.setEmail(request.getEmail());
        visitor.setNote(request.getNote());
        // visitor.setTagList(request.getTagList()); // 标签列表不在这里更新，使用 updateTagList 方法更新
        // 
        VisitorEntity savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        // 
        return convertToResponse(savedVisitor);
    }

    @Transactional
    public VisitorResponse updateTagList(VisitorRequest request) {
        // 
        Optional<VisitorEntity> visitorOptional = findByUid(request.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        VisitorEntity visitor = visitorOptional.get();
        visitor.setTagList(request.getTagList());
        // 
        VisitorEntity savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        return convertToResponse(savedVisitor);
    }

    public MessageProtobuf requestThread(VisitorRequest request) {
        return threadRoutingContext.createCsThread(request);
    }

    @Cacheable(value = "visitor", key = "#uid", unless = "#result == null")
    @Override
    public Optional<VisitorEntity> findByUid(@NonNull String uid) {
        // 如果参数为空，则返回空
        if (!StringUtils.hasText(uid)) {
            return Optional.empty();
        }
        return visitorRepository.findByUidAndDeleted(uid, false);
    }
    
    @Transactional
    @Cacheable(value = "visitor", key = "#visitorUid + '-' + #orgUid", unless = "#result == null")
    public Optional<VisitorEntity> findByVisitorUidAndOrgUid(@NonNull String visitorUid, @NonNull String orgUid) {
        // 如果参数为空，则返回空
        if (!StringUtils.hasText(visitorUid) || !StringUtils.hasText(orgUid)) {
            return Optional.empty();
        }
        return visitorRepository.findByVisitorUidAndOrgUidAndDeleted(visitorUid, orgUid, false);
    }

    public List<VisitorEntity> findByStatus(@NonNull String status) {
        return visitorRepository.findByStatusAndDeleted(status, false);
    }

    public int updateStatus(@NonNull String uid, @NonNull String newStatus) {
        return visitorRepository.updateStatusByUid(uid, newStatus);
    }

    @CachePut(value = "visitor", key = "#entity.uid", unless = "#entity == null")
    @Override
    protected VisitorEntity doSave(VisitorEntity entity) {
        return visitorRepository.save(entity);
    }

    @CacheEvict(value = "visitor", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<VisitorEntity> visitorOptional = findByUid(uid);
        if (visitorOptional.isPresent()) {
            VisitorEntity visitor = visitorOptional.get();
            visitor.setDeleted(true);
            save(visitor);
        }
    }

    @Override
    public void delete(VisitorRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public VisitorEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorEntity entity) {
        try {
            Optional<VisitorEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                VisitorEntity latestEntity = latest.get();
                // 合并访客信息
                latestEntity.setNickname(entity.getNickname());
                latestEntity.setAvatar(entity.getAvatar());
                latestEntity.setMobile(entity.getMobile());
                latestEntity.setEmail(entity.getEmail());
                latestEntity.setIp(entity.getIp());
                latestEntity.setIpLocation(entity.getIpLocation());
                latestEntity.setVipLevel(entity.getVipLevel());
                latestEntity.setTagList(entity.getTagList());
                latestEntity.setNote(entity.getNote());
                latestEntity.setExtra(entity.getExtra());
                latestEntity.setDeviceInfo(entity.getDeviceInfo());
                return visitorRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突", ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public VisitorResponse convertToResponse(VisitorEntity entity) {
        return ServiceConvertUtils.convertToVisitorResponse(entity);
    }

    @Override
    public VisitorExcel convertToExcel(VisitorEntity entity) {
        VisitorExcel excel = modelMapper.map(entity, VisitorExcel.class);
        excel.setChannel(ChannelEnum.toChineseDisplay(entity.getChannel()));
        return excel;
    }

    public void publishVisitorMessageEvent(String messageJson) {
        if (!StringUtils.hasText(messageJson)) {
            return;
        }
        try {
            applicationEventPublisher.publishEvent(new VisitorRobotMessageEvent(this, messageJson));
        } catch (Exception ex) {
            log.warn("Failed to publish VisitorRobotMessageEvent for visitor SSE payload", ex);
        }
    }
    

}
