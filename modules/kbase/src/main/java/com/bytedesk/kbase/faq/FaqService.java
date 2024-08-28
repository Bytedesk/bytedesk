/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-26 06:46:10
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.Category;
import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FaqService extends BaseService<Faq, FaqRequest, FaqResponse> {

    private final FaqRepository faqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryService categoryService;

    @Override
    public Page<FaqResponse> queryByOrg(FaqRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<Faq> spec = FaqSpecification.search(request);

        Page<Faq> page = faqRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FaqResponse> queryByUser(FaqRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Faq> findByUid(String uid) {
        return faqRepository.findByUid(uid);
    }

    @Override
    public FaqResponse create(FaqRequest request) {

        Faq entity = modelMapper.map(request, Faq.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getDefaultSerialUid());
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

        Optional<Faq> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            Faq entity = optional.get();
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
        Optional<Faq> optional = findByUid(uid);
        if (optional.isPresent()) {
            Faq entity = optional.get();
            entity.up();
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    public FaqResponse downVote(String uid) {
        Optional<Faq> optional = findByUid(uid);
        if (optional.isPresent()) {
            Faq entity = optional.get();
            entity.down();
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    @Override
    public Faq save(Faq entity) {
        try {
            return faqRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    public void save(List<Faq> entities) {
        faqRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Faq> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(Faq entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Faq entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FaqResponse convertToResponse(Faq entity) {
        return modelMapper.map(entity, FaqResponse.class);
    }

    public FaqExcel convertToExcel(FaqResponse faq) {
        return modelMapper.map(faq, FaqExcel.class);
    }

    public Faq convertExcelToFaq(FaqExcel excel,String kbUid, String orgUid) {
        // return modelMapper.map(excel, Faq.class); // String categoryUid,
        Faq faq = Faq.builder().build();
        faq.setUid(uidUtils.getCacheSerialUid());
        faq.setTitle(excel.getTitle());
        faq.setContent(excel.getContent());
        // 
        // faq.setType(MessageTypeEnum.TEXT);
        faq.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        // 
        // faq.setCategoryUid(categoryUid);
        Optional<Category> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            faq.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
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

    public void initData() {

        if (faqRepository.count() > 0) {
            return;
        }

        //
        String orgUid = BdConstants.DEFAULT_ORGANIZATION_UID;
        FaqRequest faqDemo1 = FaqRequest.builder()
                .title(I18Consts.I18N_FAQ_DEMO_TITLE_1)
                .content(I18Consts.I18N_FAQ_DEMO_CONTENT_1)
                .type(MessageTypeEnum.TEXT.name())
                .categoryUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .build();
        faqDemo1.setUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1);
        faqDemo1.setOrgUid(orgUid);
        create(faqDemo1);
        //
        FaqRequest faqDemo2 = FaqRequest.builder()
                .title(I18Consts.I18N_FAQ_DEMO_TITLE_2)
                .content(I18Consts.I18N_FAQ_DEMO_CONTENT_2)
                .type(MessageTypeEnum.IMAGE.name())
                .categoryUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .build();
        faqDemo2.setUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        faqDemo2.setOrgUid(orgUid);
        create(faqDemo2);
    }

}
