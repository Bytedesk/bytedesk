/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 12:14:35
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
// import com.bytedesk.core.ip.IpService;
// import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.strategy.CsThreadCreationContext;
import com.bytedesk.service.utils.ConvertServiceUtils;

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
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<VisitorEntity> spec = VisitorSpecification.search(request);
        Page<VisitorEntity> page = visitorRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
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

    // TODO: 模拟压力测试：随机生成 10000 个访客，分配给1个技能组中10个客服账号，并随机分配给1个客服账号，每秒发送1条消息
    public void prepareStressTest() {
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // // 随机生成10000个访客
        // List<Visitor> visitors = new ArrayList<>();
        // for (int i = 0; i < 10000; i++) {
        // String uid = uidUtils.getCacheSerialUid();
        // // visitors.add(new Visitor(uid, "visitor" + i));
        // }
        // 随机分配给1个技能组中10个客服账号
        // List<Robot> robots = robotService.findAllByOrgUidAndDeleted(orgUid, false);
        // if (robots == null || robots.isEmpty()) {
        // return;
        // }
        // Random random = new Random();
        // for (Visitor visitor : visitors) {
        // Robot robot = robots.get(random.nextInt(robots.size()));
        // visitor.setAgentUid(robot.getUid());
        // save(visitor);
        // }
    }

    @Override
    public Page<VisitorResponse> queryByUser(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }


    @Override
    public VisitorResponse update(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(VisitorRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, VisitorEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public VisitorResponse convertToResponse(VisitorEntity entity) {
        return ConvertServiceUtils.convertToVisitorResponse(entity);
    }

    public String getAvatar(String client) {
        if (client == null) {
            return AvatarConsts.DEFAULT_VISITOR_AVATAR_URL;
        }
        if (client.toUpperCase().contains(ClientEnum.WEB.name())) {
            return AvatarConsts.DEFAULT_WEB_AVATAR_URL;
        } else if (client.toUpperCase().contains(ClientEnum.ANDROID.name())) {
            return AvatarConsts.DEFAULT_ANDROID_AVATAR_URL;
        } else if (client.toUpperCase().contains(ClientEnum.IOS.name())) {
           return AvatarConsts.DEFAULT_IOS_AVATAR_URL;
        } else if (client.toUpperCase().contains(ClientEnum.UNIAPP.name())) {
            return AvatarConsts.DEFAULT_UNIAPP_AVATAR_URL;
        }
        return AvatarConsts.DEFAULT_VISITOR_AVATAR_URL;
    }
}
