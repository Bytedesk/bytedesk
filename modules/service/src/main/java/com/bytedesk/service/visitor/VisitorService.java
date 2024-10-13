/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-10 15:48:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.strategy.CsThreadCreationContext;
import com.bytedesk.service.utils.ConvertServiceUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorService extends BaseService<Visitor, VisitorRequest, VisitorResponse> {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final IpService ipService;

    private final CsThreadCreationContext csThreadCreationContext;

    @Override
    public Page<VisitorResponse> queryByOrg(VisitorRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<Visitor> spec = VisitorSpecification.search(request);
        Page<Visitor> page = visitorRepository.findAll(spec, pageable);

        return page.map(ConvertServiceUtils::convertToVisitorResponse);
    }

    public VisitorResponse query(VisitorRequest visitorRequest) {

        Optional<Visitor> visitorOptional = findByUid(visitorRequest.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }
        return ConvertServiceUtils.convertToVisitorResponse(visitorOptional.get());
    }

    /**
     * create visitor record
     * 
     * @param visitorRequest
     * @return
     */
    public UserProtobuf create(VisitorRequest visitorRequest, HttpServletRequest request) {
        //
        String uid = visitorRequest.getUid();
        log.info("visitor init, uid: {}", uid);
        Visitor visitor = findByUid(uid).orElse(null);
        if (visitor != null) {
            return ConvertServiceUtils.convertToUserProtobuf(visitor);
        }
        if (!StringUtils.hasText(uid)) {
            visitorRequest.setUid(uidUtils.getCacheSerialUid());
        }
        if (!StringUtils.hasText(visitorRequest.getNickname())) {
            visitorRequest.setNickname(ipService.createVisitorNickname(request));
        }
        if (!StringUtils.hasText(visitorRequest.getAvatar())) {
            visitorRequest.setAvatar(getAvatar(visitorRequest.getClient()));
        }
        //
        String ip = ipService.getIp(request);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        //
        log.info("visitorRequest {}", visitorRequest);
        visitor = modelMapper.map(visitorRequest, Visitor.class);
        // visitor.setUid(uidUtils.getCacheSerialUid());
        visitor.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
        visitor.setOrgUid(visitorRequest.getOrgUid());
        //
        VisitorDevice device = modelMapper.map(visitorRequest, VisitorDevice.class);
        visitor.setDevice(device);
        //
        Visitor savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        //
        return ConvertServiceUtils.convertToUserProtobuf(savedVisitor);
    }

    /** 策略模式 */
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return csThreadCreationContext.createCsThread(visitorRequest);
    }

    @Cacheable(value = "visitor", key = "#uid", unless = "#result == null")
    public Optional<Visitor> findByUid(String uid) {
        return visitorRepository.findByUidAndDeleted(uid, false);
    }

    public List<Visitor> findByStatus(String status) {
        return visitorRepository.findByStatusAndDeleted(status, false);
    }

    public int updateStatus(String uid, String newStatus) {
        return visitorRepository.updateStatusByUid(uid, newStatus);
    }

    @Caching(put = {
            @CachePut(value = "visitor", key = "#visitor.uid"),
    })
    @Override
    public Visitor save(Visitor visitor) {
        try {
            return visitorRepository.save(visitor);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    // public void notifyAgent(MessageProtobuf messageProtobuf) {
    // String json = JSON.toJSONString(messageProtobuf);
    // bytedeskEventPublisher.publishMessageJsonEvent(json);
    // }

    // TODO: 模拟压力测试：随机生成 10000 个访客，分配给1个技能组中10个客服账号，并随机分配给1个客服账号，每秒发送1条消息
    public void prepeareStressTest() {
        // String orgUid = BdConstants.DEFAULT_ORGANIZATION_UID;
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
    public VisitorResponse create(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
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
    public void delete(Visitor entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Visitor entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public VisitorResponse convertToResponse(Visitor entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
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
