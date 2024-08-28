/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-26 06:46:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply;

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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.Category;
import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AutoReplyService extends BaseService<AutoReply, AutoReplyRequest, AutoReplyResponse> {

    private final AutoReplyRepository autoReplyRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final CategoryService categoryService;

    @Override
    public Page<AutoReplyResponse> queryByOrg(AutoReplyRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<AutoReply> specification = AutoReplySpecification.search(request);

        Page<AutoReply> page = autoReplyRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<AutoReplyResponse> queryByUser(AutoReplyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<AutoReply> findByUid(String uid) {
        return autoReplyRepository.findByUid(uid);
    }

    @Override
    public AutoReplyResponse create(AutoReplyRequest request) {
        
        AutoReply autoReply = modelMapper.map(request, AutoReply.class);
        autoReply.setUid(uidUtils.getCacheSerialUid());

        AutoReply savedAutoReply = save(autoReply);
        if (savedAutoReply == null) {
            throw new RuntimeException("AutoReply create failed");
        }
        // 
        return convertToResponse(savedAutoReply);
    }

    @Override
    public AutoReplyResponse update(AutoReplyRequest request) {
        
        Optional<AutoReply> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            AutoReply autoReply = optional.get();
            autoReply.setContent(request.getContent());
            autoReply.setCategoryUid(request.getCategoryUid());
            autoReply.setKbUid(request.getKbUid());
            // 
            AutoReply savedAutoReply = save(autoReply);
            if (savedAutoReply == null) {
                throw new RuntimeException("AutoReply create failed");
            }
            //
            return convertToResponse(savedAutoReply);
        } else {
            throw new RuntimeException("AutoReply not found");
        }
    }

    @Override
    public AutoReply save(AutoReply entity) {
        try {
            return autoReplyRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void save(List<AutoReply> entities) {
        autoReplyRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AutoReply> autoReplyOptional = findByUid(uid);
        if (autoReplyOptional.isPresent()) {
            autoReplyOptional.get().setDeleted(true);
            save(autoReplyOptional.get());
        }
    }

    @Override
    public void delete(AutoReply entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AutoReply entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public AutoReplyResponse convertToResponse(AutoReply entity) {
        return modelMapper.map(entity, AutoReplyResponse.class);
    }

    public AutoReplyExcel convertToExcel(AutoReplyResponse autoReply) {
        return modelMapper.map(autoReply, AutoReplyExcel.class);
    }

    // String categoryUid,
    public AutoReply convertExcelToAutoReply(AutoReplyExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, AutoReply.class);
        AutoReply autoReply = AutoReply.builder().build();
        autoReply.setUid(uidUtils.getCacheSerialUid());
        autoReply.setContent(excel.getContent());
        // 
        // autoReply.setType(MessageTypeEnum.TEXT); // TODO: 根据实际类型设置
        autoReply.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        // 
        // autoReply.setCategoryUid(categoryUid);
        Optional<Category> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            autoReply.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryConsts.CATEGORY_TYPE_AUTOREPLY);
            categoryRequest.setOrgUid(orgUid);
            // 
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            autoReply.setCategoryUid(categoryResponse.getUid());
        }
        // 
        autoReply.setKbUid(kbUid);
        autoReply.setOrgUid(orgUid);

        return autoReply;
    }
    
}
