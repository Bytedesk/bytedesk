/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-09 08:18:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.knowledgebase;

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
import com.bytedesk.core.category.Category;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KownledgebaseService extends BaseService<Kownledgebase, KownledgebaseRequest, KownledgebaseResponse> {

    private final KownledgebaseRepository FaqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryService categoryService;

    @Override
    public Page<KownledgebaseResponse> queryByOrg(KownledgebaseRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<Kownledgebase> spec = KownledgebaseSpecification.search(request);

        Page<Kownledgebase> page = FaqRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<KownledgebaseResponse> queryByUser(KownledgebaseRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Kownledgebase> findByUid(String uid) {
        return FaqRepository.findByUid(uid);
    }

    @Override
    public KownledgebaseResponse create(KownledgebaseRequest request) {

        Kownledgebase entity = modelMapper.map(request, Kownledgebase.class);
        entity.setUid(uidUtils.getCacheSerialUid());
        //
        // category
        Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
        if (categoryOptional.isPresent()) {
            entity.setCategory(categoryOptional.get());
        }

        return convertToResponse(save(entity));
    }

    @Override
    public KownledgebaseResponse update(KownledgebaseRequest request) {

        Optional<Kownledgebase> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            Kownledgebase entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setContent(request.getContent());
            entity.setType(MessageTypeEnum.fromValue(request.getType()));

            // category
            Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
            if (categoryOptional.isPresent()) {
                entity.setCategory(categoryOptional.get());
            }

            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("quick_reply not found");
        }
    }

    @Override
    public Kownledgebase save(Kownledgebase entity) {
        try {
            return FaqRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Kownledgebase> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(Kownledgebase entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            Kownledgebase entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public KownledgebaseResponse convertToResponse(Kownledgebase entity) {
        return modelMapper.map(entity, KownledgebaseResponse.class);
    }

}
