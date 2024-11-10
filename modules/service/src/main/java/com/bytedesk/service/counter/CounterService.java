/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 09:24:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:19:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.counter;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CounterService extends BaseRestService<CounterEntity, CounterRequest, CounterResponse> {

    private final CounterRepository counterRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    // agentUid 代表 技能组uid/客服uid
    public CounterResponse getNumber(String orgUid, String topic, String visitor) {
        Optional<CounterEntity> counterOptional = findByTopic(topic);
        if (counterOptional.isPresent()) {
            CounterEntity counter = counterOptional.get();
            counter.increaseSerialNumber();
            try {
                return convertToResponse(counterRepository.save(counter));
            } catch (ObjectOptimisticLockingFailureException e) {
                return getNumber(orgUid, topic, visitor);
            }
        } else {
            CounterRequest request = CounterRequest.builder()
                .topic(topic)
            .build();
            request.setOrgUid(orgUid);
            return create(request);
        }
    }

    @Override
    public Page<CounterResponse> queryByOrg(CounterRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<CounterResponse> queryByUser(CounterRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "counter", key = "#uid")
    @Override
    public Optional<CounterEntity> findByUid(String uid) {
        return counterRepository.findByUid(uid);
    }

    // @Cacheable(value = "counter", key = "#sid")
    // public Optional<Counter> findBySid(String sid) {
    //     return counterRepository.findBySid(sid);
    // }

    @Cacheable(value = "counter", key = "#topic")
    public Optional<CounterEntity> findByTopic(String topic) {
        return counterRepository.findByTopic(topic);
    }

    @Override
    public CounterResponse create(CounterRequest request) {
        CounterEntity counter = modelMapper.map(request, CounterEntity.class);
        counter.setUid(uidUtils.getUid());
        // 
        CounterEntity savedCounter = save(counter);
        if (savedCounter == null) {
            throw new RuntimeException("save counter failed");
        }
        return convertToResponse(savedCounter);
    }

    @Override
    public CounterResponse update(CounterRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public CounterEntity save(CounterEntity entity) {
        try {
            return counterRepository.save(entity);
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
    public void delete(CounterRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public void deleteAll() {
        counterRepository.deleteAll();
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CounterEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public CounterResponse convertToResponse(CounterEntity entity) {
        return modelMapper.map(entity, CounterResponse.class);
    }
    
}
