/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 18:00:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_thread;

import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotThreadService extends BaseRestService<RobotThreadEntity, RobotThreadRequest, RobotThreadResponse> {

    private final RobotThreadRepository visitorThreadRepository;

    // private final ThreadRestService threadRestService;

    // private final ModelMapper modelMapper;

    // private final UidUtils uidUtils;

    @Override
    public Page<RobotThreadResponse> queryByOrg(RobotThreadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<RobotThreadEntity> spec = RobotThreadSpecification.search(request);
        Page<RobotThreadEntity> threads = visitorThreadRepository.findAll(spec, pageable);
        return threads.map(this::convertToResponse);
    }

    @Override
    public Page<RobotThreadResponse> queryByUser(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "robot_thread", key = "#uid", unless = "#result == null")
    @Override
    public Optional<RobotThreadEntity> findByUid(String uid) {
        return visitorThreadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return visitorThreadRepository.existsByUid(uid);
    }

    @Cacheable(value = "robot_thread", key = "#topic", unless = "#result == null")
    public Optional<RobotThreadEntity> findFirstByTopic(String topic) {
        return visitorThreadRepository.findFirstByTopic(topic);
    }

    @Override
    public RobotThreadResponse initVisitor(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public RobotThreadResponse update(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    protected RobotThreadEntity doSave(RobotThreadEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doSave'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public RobotThreadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            RobotThreadEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public RobotThreadResponse convertToResponse(RobotThreadEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }



}
