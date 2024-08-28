/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 07:05:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-19 08:12:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.prompt;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PromptService extends BaseService<Prompt, PromptRequest, PromptResponse> {

    private final PromptRepository promptRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<PromptResponse> queryByOrg(PromptRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");

        Specification<Prompt> specification = PromptSpecification.search(request);

        Page<Prompt> page = promptRepository.findAll(specification, pageable);

        return page.map(prompt -> modelMapper.map(prompt, PromptResponse.class));
    }

    @Override
    public Page<PromptResponse> queryByUser(PromptRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Prompt> findByUid(String uid) {
        return promptRepository.findByUid(uid);
    }

    @Override
    public PromptResponse create(PromptRequest request) {
        
        Prompt prompt = modelMapper.map(request, Prompt.class);
        prompt.setUid(uidUtils.getCacheSerialUid());
        prompt.setOrgUid(request.getOrgUid());

        // UserProtobuf user =

        Prompt promptSaved = save(prompt);
        if (promptSaved == null) {
            throw new RuntimeException("save prompt failed");
        }
        return modelMapper.map(promptSaved, PromptResponse.class);
    }

    @Override
    public PromptResponse update(PromptRequest request) {
        
        Optional<Prompt> prompt = promptRepository.findByUid(request.getUid());
        if (prompt.isPresent()) {
            Prompt promptUpdate = prompt.get();
            promptUpdate.setTitle(request.getTitle());
            promptUpdate.setContent(request.getContent());
            // 
            Prompt promptSaved = save(promptUpdate);
            return convertToResponse(promptSaved);
        }

        throw new RuntimeException("prompt not found");
    }

    @Override
    public Prompt save(Prompt entity) {
        try {
            return promptRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Prompt> prompt = promptRepository.findByUid(uid);
        prompt.ifPresent(p -> {
            p.setDeleted(true);
            save(p);
        });
    }

    @Override
    public void delete(Prompt entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Prompt entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public PromptResponse convertToResponse(Prompt entity) {
        return modelMapper.map(entity, PromptResponse.class);
    }
    
}
