/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:19:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-25 23:48:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotJsonService.ModelJson;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LlmModelService extends BaseService<LlmModel, LlmModelRequest, LlmModelResponse> {

    private final LlmModelRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<LlmModelResponse> queryByOrg(LlmModelRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");

        Specification<LlmModel> specification = LlmModelSpecification.search(request);

        Page<LlmModel> page = repository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<LlmModelResponse> queryByUser(LlmModelRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<LlmModel> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return repository.existsByUid(uid);
    }

    // public Optional<LlmModel> findByName(String name) {
    // return repository.findByName(name);
    // }

    // public Boolean existsByName(String name) {
    // return repository.existsByName(name);
    // }

    @Override
    public LlmModelResponse create(LlmModelRequest request) {

        // if (findByUid(request.getUid()).isPresent()) {
        //     throw new RuntimeException("LlmModel name already exists");
        // }

        LlmModel entity = modelMapper.map(request, LlmModel.class);
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getUid());
        }
        //
        LlmModel savedModel = save(entity);
        if (savedModel == null) {
            throw new RuntimeException("Create LlmModel failed");
        }

        return convertToResponse(savedModel);
    }

    public LlmModelResponse createFromModelJson(String providerName, ModelJson modelJson) {

        LlmModelRequest request = LlmModelRequest.builder()
                .provider(providerName)
                .nickname(modelJson.getNickname())
                .category(modelJson.getCategory())
                .build();
        request.setUid(modelJson.getUid());

        return create(request);
    }

    @Override
    public LlmModelResponse update(LlmModelRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public LlmModel save(LlmModel entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(LlmModel entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmModel entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public LlmModelResponse convertToResponse(LlmModel entity) {
        return modelMapper.map(entity, LlmModelResponse.class);
    }

}
