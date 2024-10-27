/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 10:10:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:19:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.counter_visitor;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CounterVisitorService extends BaseService<CounterVisitorEntity, CounterVisitorRequest, CounterVisitorResponse> {
    
    private final CounterVisitorRepository counterVisitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public void saveNumber(String orgUid, String topic, int serialNumber, String visitor) {
        CounterVisitorRequest request = CounterVisitorRequest.builder()
            .topic(topic)
            .serialNumber(serialNumber)
            .visitor(visitor)
            .build();
        request.setOrgUid(orgUid);
        create(request);
    } 
    @Override
    public Page<CounterVisitorResponse> queryByOrg(CounterVisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<CounterVisitorResponse> queryByUser(CounterVisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<CounterVisitorEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public CounterVisitorResponse create(CounterVisitorRequest request) {
        CounterVisitorEntity entity = modelMapper.map(request, CounterVisitorEntity.class);
        entity.setUid(uidUtils.getUid());

        CounterVisitorEntity savedCounterVisitor = save(entity);
        if (savedCounterVisitor == null) {
            throw new RuntimeException("Create counter_visitor failed");
        }
        return convertToResponse(savedCounterVisitor);
    }

    @Override
    public CounterVisitorResponse update(CounterVisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public CounterVisitorEntity save(CounterVisitorEntity entity) {
        try {
            return counterVisitorRepository.save(entity);
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
    public void delete(CounterVisitorRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            CounterVisitorEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public CounterVisitorResponse convertToResponse(CounterVisitorEntity entity) {
        return modelMapper.map(entity, CounterVisitorResponse.class);
    }
    
}
