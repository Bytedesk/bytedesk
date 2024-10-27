/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:49:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:30:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.robot.RobotJsonService.ProviderJson;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LlmProviderService extends BaseService<LlmProviderEntity, LlmProviderRequest, LlmProviderResponse> {

    private final LlmProviderRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<LlmProviderResponse> queryByOrg(LlmProviderRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");

        Specification<LlmProviderEntity> specification = LlmProviderSpecification.search(request);

        Page<LlmProviderEntity> page = repository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<LlmProviderResponse> queryByUser(LlmProviderRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<LlmProviderEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    public Optional<LlmProviderEntity> findByName(String name) {
        return repository.findByName(name);
    }

    public Boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public LlmProviderResponse create(LlmProviderRequest request) {

        if (findByName(request.getName()).isPresent()) {
            throw new RuntimeException("provider name already exists");
        }

        LlmProviderEntity entity = modelMapper.map(request, LlmProviderEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        LlmProviderEntity savedProvider = save(entity);
        if (savedProvider == null) {
            throw new RuntimeException("create provider failed");
        }
        return convertToResponse(savedProvider);
    }

    public LlmProviderResponse createFromProviderJson(String providerName, ProviderJson providerJson) {

        LlmProviderRequest request = LlmProviderRequest.builder()
                .name(providerName)
                .nickname(providerJson.getApp().getNickname())
                .avatar("https://cdn.weiyuai.cn/assets/images/llm/provider/" + providerJson.getApp().getLogo())
                .apiUrl(providerJson.getApi().getUrl())
                .webUrl(providerJson.getWebsites().getWebUrl())
                .apiKeyUrl(providerJson.getWebsites().getApiKeyUrl())
                .docsUrl(providerJson.getWebsites().getDocsUrl())
                .modelsUrl(providerJson.getWebsites().getModelsUrl())
                .build();

        return create(request);
    }

    @Override
    public LlmProviderResponse update(LlmProviderRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public LlmProviderEntity save(LlmProviderEntity entity) {
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
    public void delete(LlmProviderRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LlmProviderEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public LlmProviderResponse convertToResponse(LlmProviderEntity entity) {
        return modelMapper.map(entity, LlmProviderResponse.class);
    }

}
