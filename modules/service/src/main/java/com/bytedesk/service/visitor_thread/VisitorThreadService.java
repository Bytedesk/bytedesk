/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-29 13:47:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.service.visitor.Visitor;
import com.bytedesk.service.visitor.VisitorProtobuf;
import com.bytedesk.service.visitor.VisitorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VisitorThreadService extends BaseService<VisitorThread, VisitorThreadRequest, VisitorThreadResponse> {

    private final VisitorThreadRepository visitorThreadRepository;

    private final VisitorService visitorService;

    private final ModelMapper modelMapper;

    @Override
    public Page<VisitorThreadResponse> queryByOrg(VisitorThreadRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<VisitorThread> spec = VisitorThreadSpecification.search(request);
        Page<VisitorThread> threads = visitorThreadRepository.findAll(spec, pageable);

        return threads.map(this::convertToResponse);
    }

    @Override
    public Page<VisitorThreadResponse> queryByUser(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "visitor_thread", key = "#uid", unless = "#result == null")
    @Override
    public Optional<VisitorThread> findByUid(String uid) {
        return visitorThreadRepository.findByUid(uid);
    }

    public VisitorThread create(Thread thread) {

        if (visitorThreadRepository.existsByUid(thread.getUid())) {
            return null;
        }
        //
        VisitorThread visitorThread = modelMapper.map(thread, VisitorThread.class);
        //
        String visitorString = thread.getUser();
        VisitorProtobuf visitor = JSON.parseObject(visitorString, VisitorProtobuf.class);
        //
        Optional<Visitor> visitorOpt = visitorService.findByUid(visitor.getUid());
        if (visitorOpt.isPresent()) {
            visitorThread.setVisitor(visitorOpt.get());
        } else {
            throw new RuntimeException("Visitor " + visitor.getUid() + " not found");
        }
        //
        return save(visitorThread);
    }

    @Override
    public VisitorThreadResponse create(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public VisitorThreadResponse update(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public VisitorThread save(VisitorThread entity) {
        try {
            return visitorThreadRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(VisitorThread entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorThread entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public VisitorThreadResponse convertToResponse(VisitorThread entity) {
        return modelMapper.map(entity, VisitorThreadResponse.class);
    }

}
