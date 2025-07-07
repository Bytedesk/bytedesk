/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:16:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AssistantRestService extends BaseRestService<AssistantEntity, AssistantRequest, AssistantResponse> {

    private final AssistantRepository assistantRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<AssistantResponse> queryByOrg(AssistantRequest request) {
        Pageable pageable = request.getPageable();
        Page<AssistantEntity> assistantPage = assistantRepository.findAll(pageable);
        return assistantPage.map(assistant -> convertToResponse(assistant));
    }

    @Override
    public Page<AssistantResponse> queryByUser(AssistantRequest request) {
        return queryByOrg(request);
    }

    @Override
    public Optional<AssistantEntity> findByUid(String uid) {
        return assistantRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return assistantRepository.existsByUid(uid);
    }

    @Override
    public AssistantResponse create(AssistantRequest request) {
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        AssistantEntity assistant = modelMapper.map(request, AssistantEntity.class);
        if (!StringUtils.hasText(assistant.getUid())) {
            assistant.setUid(uidUtils.getUid());
        }

        // 保存
        AssistantEntity savedAssistant = save(assistant);
        if (savedAssistant == null) {
            throw new RuntimeException("Create assistant failed");
        }

        return convertToResponse(savedAssistant);

    }
    

    @Override
    public AssistantResponse update(AssistantRequest request) {
        Optional<AssistantEntity> assistantOptional = findByUid(request.getUid());
        if (assistantOptional.isPresent()) {
            AssistantEntity assistant = assistantOptional.get();
            // modelMapper.map(request, assistant);
            // AssistantEntity savedAssistant = save(assistant);


            return convertToResponse(save(assistant));
        }
        return null;
    }

    @Override
    public AssistantEntity save(AssistantEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    protected AssistantEntity doSave(AssistantEntity entity) {
        return assistantRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AssistantEntity> assistantOptional = findByUid(uid);
        if (assistantOptional.isPresent()) {
            AssistantEntity assistant = assistantOptional.get();
            assistant.setDeleted(false);
            save(assistant);
            // delete(assistant.get());
        }
    }

    @Override
    public void delete(AssistantRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AssistantEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AssistantEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public AssistantResponse convertToResponse(AssistantEntity assistant) {
        return modelMapper.map(assistant, AssistantResponse.class);
    }

}
