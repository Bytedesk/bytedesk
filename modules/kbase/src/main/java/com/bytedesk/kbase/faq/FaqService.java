/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 22:38:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.List;
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
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FaqService extends BaseRestService<FaqEntity, FaqRequest, FaqResponse> {

    private final FaqRepository faqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryService;

    @Override
    public Page<FaqResponse> queryByOrg(FaqRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<FaqEntity> spec = FaqSpecification.search(request);

        Page<FaqEntity> page = faqRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FaqResponse> queryByUser(FaqRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "faq", key="#uid", unless = "#result == null")
    @Override
    public Optional<FaqEntity> findByUid(String uid) {
        return faqRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return faqRepository.existsByUid(uid);
    }

    @Override
    public FaqResponse create(FaqRequest request) {

        FaqEntity entity = modelMapper.map(request, FaqEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        entity.setType(MessageTypeEnum.fromValue(request.getType()).name());
        //
        // category
        // Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
        // if (categoryOptional.isPresent()) {
        //     entity.setCategory(categoryOptional.get());
        // }
        return convertToResponse(save(entity));
    }

    @Override
    public FaqResponse update(FaqRequest request) {

        Optional<FaqEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setContent(request.getContent());
            entity.setType(MessageTypeEnum.fromValue(request.getType()).name());

            // category
            // Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
            // if (categoryOptional.isPresent()) {
            //     entity.setCategory(categoryOptional.get());
            // }

            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    public FaqResponse upVote(String uid) {
        Optional<FaqEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            entity.up();
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    public FaqResponse downVote(String uid) {
        Optional<FaqEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            entity.down();
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    @Override
    public FaqEntity save(FaqEntity entity) {
        try {
            return faqRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    public void save(List<FaqEntity> entities) {
        faqRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FaqEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(FaqRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FaqEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FaqResponse convertToResponse(FaqEntity entity) {
        return modelMapper.map(entity, FaqResponse.class);
    }

    public FaqExcel convertToExcel(FaqResponse faq) {
        return modelMapper.map(faq, FaqExcel.class);
    }

    public FaqEntity convertExcelToFaq(FaqExcel excel,String kbUid, String orgUid) {
        // return modelMapper.map(excel, Faq.class); // String categoryUid,
        FaqEntity faq = FaqEntity.builder().build();
        faq.setUid(uidUtils.getCacheSerialUid());
        faq.setTitle(excel.getTitle());
        faq.setContent(excel.getContent());
        // 
        // faq.setType(MessageTypeEnum.TEXT);
        faq.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        // 
        // faq.setCategoryUid(categoryUid);
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            faq.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.FAQ.name());
            categoryRequest.setOrgUid(orgUid);
            // 
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            faq.setCategoryUid(categoryResponse.getUid());
        }
        faq.setKbUid(kbUid);
        faq.setOrgUid(orgUid);
        // 
        return faq;
    }

}
