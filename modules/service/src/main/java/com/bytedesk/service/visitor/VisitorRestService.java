/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:32:50
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
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.strategy.CsThreadCreationContext;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorRestService extends BaseRestService<VisitorEntity, VisitorRequest, VisitorResponse> {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CsThreadCreationContext csThreadCreationContext;

    @Override
    public Page<VisitorResponse> queryByOrg(VisitorRequest request) {
        Pageable pageable = request.getPageable();
        Specification<VisitorEntity> spec = VisitorSpecification.search(request);
        Page<VisitorEntity> page = visitorRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<VisitorResponse> queryByUser(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    // @Override
    public Optional<VisitorResponse> queryByUid(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }


    public VisitorResponse query(VisitorRequest visitorRequest) {
        Optional<VisitorEntity> visitorOptional = findByUid(visitorRequest.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        return convertToResponse(visitorOptional.get());
    }

    public VisitorResponse create(VisitorRequest visitorRequest) {
        //
        String uid = visitorRequest.getUid();
        log.info("visitor init, uid: {}", uid);
        VisitorEntity visitor = findByUid(uid).orElse(null);
        if (visitor != null) {
            // 对比ip是否有变化
            if (visitor.getIp() == null || !visitor.getIp().equals(visitorRequest.getIp())) {
                // 更新浏览信息
                visitor.setIp(visitorRequest.getIp());
                visitor.setIpLocation(visitorRequest.getIpLocation());
                save(visitor);
            }
            //
            return convertToResponse(visitor);
        }
        if (!StringUtils.hasText(uid)) {
            visitorRequest.setUid(uidUtils.getUid());
        }
        if (!StringUtils.hasText(visitorRequest.getAvatar())) {
            visitorRequest.setAvatar(getAvatar(visitorRequest.getClient()));
        }
        //
        log.info("visitorRequest {}", visitorRequest);
        visitor = modelMapper.map(visitorRequest, VisitorEntity.class);
        if (!StringUtils.hasText(visitorRequest.getExtra())) {
            visitor.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        //
        VisitorDevice device = modelMapper.map(visitorRequest, VisitorDevice.class);
        visitor.setDevice(device);
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
        // 
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        // 
        VisitorEntity visitor = visitorOptional.get();
        visitor.setNickname(request.getNickname());
        visitor.setAvatar(request.getAvatar());
        // visitor.setLang(request.getLang());
        // visitor.setDevice(request.getDevice());
        visitor.setMobile(request.getMobile());
        visitor.setEmail(request.getEmail());
        visitor.setNote(request.getNote());
        // 
        return convertToResponse(save(visitor));
    }

    /** 策略模式 */
    public MessageProtobuf requestThread(VisitorRequest visitorRequest) {
        return csThreadCreationContext.createCsThread(visitorRequest);
    }

    @Cacheable(value = "visitor", key = "#uid", unless = "#result == null")
    public Optional<VisitorEntity> findByUid(String uid) {
        return visitorRepository.findByUidAndDeleted(uid, false);
    }

    public List<VisitorEntity> findByStatus(String status) {
        return visitorRepository.findByStatusAndDeleted(status, false);
    }

    public int updateStatus(String uid, String newStatus) {
        return visitorRepository.updateStatusByUid(uid, newStatus);
    }

    @Caching(put = {
            @CachePut(value = "visitor", key = "#visitor.uid"),
    })
    @Override
    public VisitorEntity save(VisitorEntity visitor) {
        try {
            return visitorRepository.save(visitor);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
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
}
