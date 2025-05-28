/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 09:10:30
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.routing_strategy.ThreadRoutingContext;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorRestService extends BaseRestServiceWithExcel<VisitorEntity, VisitorRequest, VisitorResponse, VisitorExcel> {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRoutingContext csThreadCreationContext;


    @Override
    public Page<VisitorEntity> queryByOrgEntity(VisitorRequest request) {
        Pageable pageable = request.getPageable();
        Specification<VisitorEntity> spec = VisitorSpecification.search(request);
        return visitorRepository.findAll(spec, pageable);
    }

    @Override
    public Page<VisitorResponse> queryByOrg(VisitorRequest request) {
        Page<VisitorEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<VisitorResponse> queryByUser(VisitorRequest request) {
        return queryByOrg(request);
    }

    // @Override
    public VisitorResponse queryByUid(VisitorRequest request) {
        Optional<VisitorEntity> visitorOptional = findByUid(request.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        return convertToResponse(visitorOptional.get());
    }

    public VisitorResponse create(VisitorRequest request) {
        //
        String uid =  request.getUid();
        String orgUid = request.getOrgUid();
        // if (!StringUtils.hasText(uid)) {
        //     request.setUid(uidUtils.getUid());
        // }
        // 前端自定义uid，用于区别于自动生成uid
        request.setVisitorUid(request.getUid());
        // uid使用自动生成的uid，防止前端uid冲突
        request.setUid(uidUtils.getUid());
        // 
        log.info("visitor init, uid: {}", uid);
        VisitorEntity visitor = findByVisitorUidAndOrgUid(uid, orgUid).orElse(null);
        if (visitor != null) {
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
        // 
        if (!StringUtils.hasText(request.getAvatar())) {
            request.setAvatar(getAvatar(request.getClient()));
        }
        //
        log.info("request {}", request);
        visitor = modelMapper.map(request, VisitorEntity.class);
        //
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

    @Override
    public VisitorResponse update(VisitorRequest request) {
        Optional<VisitorEntity> visitorOptional = findByUid(request.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        // 
        VisitorEntity visitor = visitorOptional.get();
        visitor.setNickname(request.getNickname());
        visitor.setAvatar(request.getAvatar());
        visitor.setMobile(request.getMobile());
        visitor.setEmail(request.getEmail());
        visitor.setNote(request.getNote());
        // 
        VisitorEntity savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        // 
        return convertToResponse(savedVisitor);
    }

    public VisitorResponse updateTagList(VisitorRequest request) {
        Optional<VisitorEntity> visitorOptional = findByUid(request.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        VisitorEntity visitor = visitorOptional.get();
        visitor.setTagList(request.getTagList());
        VisitorEntity savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        return convertToResponse(savedVisitor);
    }

    /** 策略模式 */
    public MessageProtobuf requestThread(VisitorRequest request) {
        return csThreadCreationContext.createCsThread(request);
    }

    @Cacheable(value = "visitor", key = "#uid", unless = "#result == null")
    public Optional<VisitorEntity> findByUid(String uid) {
        return visitorRepository.findByUidAndDeleted(uid, false);
    }

    @Cacheable(value = "visitor", key = "#visitorUid-#orgUid", unless = "#result == null")
    public Optional<VisitorEntity> findByVisitorUidAndOrgUid(String visitorUid, String orgUid) {
        return visitorRepository.findByVisitorUidAndOrgUidAndDeleted(visitorUid, orgUid, false);
    }

    public List<VisitorEntity> findByStatus(String status) {
        return visitorRepository.findByStatusAndDeleted(status, false);
    }

    public int updateStatus(String uid, String newStatus) {
        return visitorRepository.updateStatusByUid(uid, newStatus);
    }

    @Override
    protected VisitorEntity doSave(VisitorEntity entity) {
        return visitorRepository.save(entity);
    }

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

    public String getAvatar(String client) {
        if (client == null) {
            return AvatarConsts.getDefaultVisitorAvatarUrl();
        }
        if (client.toUpperCase().contains(ClientEnum.WEB.name())) {
            return AvatarConsts.getDefaultWebAvatarUrl();
        } else if (client.toUpperCase().contains(ClientEnum.ANDROID.name())) {
            return AvatarConsts.getDefaultAndroidAvatarUrl();
        } else if (client.toUpperCase().contains(ClientEnum.IOS.name())) {
            return AvatarConsts.getDefaultIosAvatarUrl();
        } else if (client.toUpperCase().contains(ClientEnum.UNIAPP.name())) {
            return AvatarConsts.getDefaultUniappAvatarUrl();
        }
        return AvatarConsts.getDefaultVisitorAvatarUrl();
    }

    @Override
    public VisitorExcel convertToExcel(VisitorEntity entity) {
        // return VisitorExcelConverter.convert(entity);
        VisitorExcel excel = modelMapper.map(entity, VisitorExcel.class);
        excel.setClient(ClientEnum.toChineseDisplay(entity.getClient()));
        return excel;
    }
}
