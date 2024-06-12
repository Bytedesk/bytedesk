/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:02:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-08 12:10:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quick_button;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuickButtonService extends BaseService<QuickButton, QuickButtonRequest, QuickButtonResponse> {

    private final QuickButtonRepository quickButtonRepository;
   
    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<QuickButtonResponse> queryByOrg(QuickButtonRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "id");

        Specification<QuickButton> spec = QuickButtonSpecification.search(request);
        
        Page<QuickButton> quickButtonPage = quickButtonRepository.findAll(spec, pageable);

        return quickButtonPage.map(this::convertToResponse);
    }

    @Override
    public Page<QuickButtonResponse> queryByUser(QuickButtonRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<QuickButton> findByUid(String uid) {
        return quickButtonRepository.findByUid(uid);
    }

    @Override
    public QuickButtonResponse create(QuickButtonRequest request) {
    
        QuickButton quickButton = modelMapper.map(request, QuickButton.class);
    
        quickButton.setUid(uidUtils.getCacheSerialUid());
    
        return convertToResponse(save(quickButton));
    }

    @Override
    public QuickButtonResponse update(QuickButtonRequest request) {
        
        Optional<QuickButton> quickButton = findByUid(request.getUid());
        if (!quickButton.isPresent()) {
            throw new RuntimeException("quickButton not found");
        }
        QuickButton entity = quickButton.get();
        // modelMapper.map(request, entity);
        // 
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setType(request.getType());

        entity.setOrgUid(request.getOrgUid());

        return convertToResponse(save(entity));
    }

    @Override
    public QuickButton save(QuickButton entity) {
        try {
            return quickButtonRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QuickButton> quickButton = findByUid(uid);
        if (quickButton.isPresent()) {
            quickButton.get().setDeleted(true);
            save(quickButton.get());
        }
    }

    @Override
    public void delete(QuickButton entity) {
        quickButtonRepository.delete(entity);
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QuickButton entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QuickButtonResponse convertToResponse(QuickButton entity) {
        return modelMapper.map(entity, QuickButtonResponse.class);
    }

}
