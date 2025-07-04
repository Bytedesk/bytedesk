/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:28:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.llm_text.event.TextUpdateDocEvent;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TextRestService extends BaseRestServiceWithExcel<TextEntity, TextRequest, TextResponse, TextExcel> {

    private final TextRepository textRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final KbaseRestService kbaseRestService;

    private final CategoryRestService categoryRestService;
    
    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    public Page<TextEntity> queryByOrgEntity(TextRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TextEntity> spec = TextSpecification.search(request);
        return textRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TextResponse> queryByOrg(TextRequest request) {
        Page<TextEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TextResponse> queryByUser(TextRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "text", key = "#uid", unless = "#result==null")
    @Override
    public Optional<TextEntity> findByUid(String uid) {
        return textRepository.findByUid(uid);
    }

    @Cacheable(value = "text", key = "#kbUid", unless = "#result==null")
    public List<TextEntity> findByKbUid(String kbUid) {
        return textRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }

    public Boolean existsByTitleAndKbUidAndDeletedFalse(String title, String kbUid) {
        return textRepository.existsByTitleAndKbase_UidAndDeletedFalse(title, kbUid);
    }

    @Override
    public TextResponse create(TextRequest request) {
        // 获取当前登录用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        //
        TextEntity entity = modelMapper.map(request, TextEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        TextEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create text failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TextResponse update(TextRequest request) {
        Optional<TextEntity> optional = textRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TextEntity entity = optional.get();
            // modelMapper.map(request, entity);
            // 判断question/answer/similarQuestions/answerList是否有变化，如果其中一个发生变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                TextUpdateDocEvent textUpdateDocEvent = new TextUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(textUpdateDocEvent);
            }
            // 
            entity.setTitle(request.getTitle());
            entity.setContent(request.getContent());
            entity.setEnabled(request.getEnabled());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setCategoryUid(request.getCategoryUid());
            // entity.setType(request.getType());
            // entity.setDocIdList(request.getDocIdList());
            entity.setElasticStatus(request.getElasticStatus());
            
            //
            TextEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update text failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Text not found");
        }
    }

    protected TextEntity doSave(TextEntity entity) {
        return textRepository.save(entity);
    }

    public void save(List<TextEntity> entities) {
        textRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TextEntity> optional = textRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("Text not found");
        }
    }

    @Override
    public void delete(TextRequest request) {
        deleteByUid(request.getUid());
    }

    public void deleteAll(TextRequest request) {
        List<TextEntity> entities = findByKbUid(request.getKbUid());
        for (TextEntity entity : entities) {
            deleteByUid(entity.getUid());
        }
    }

    // enable/disable text
    public TextResponse enable(TextRequest request) {
        Optional<TextEntity> optional = textRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TextEntity entity = optional.get();
            entity.setEnabled(entity.getEnabled());
            // 
            TextEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Enable text failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Text not found");
        }
    }

    @Override
    public TextEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TextEntity entity) {
        try {
            Optional<TextEntity> latest = textRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TextEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setTitle(entity.getTitle());
                latestEntity.setContent(entity.getContent());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setType(entity.getType());

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setElasticStatus(entity.getElasticStatus());

                return textRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    @Override
    public TextResponse convertToResponse(TextEntity entity) {
        return modelMapper.map(entity, TextResponse.class);
    }

    @Override
    public TextExcel convertToExcel(TextEntity text) {
        TextExcel excel = modelMapper.map(text, TextExcel.class);
        if (text.getCategoryUid() != null) {
            Optional<CategoryEntity> category = categoryRestService.findByUid(text.getCategoryUid());
            if (category.isPresent()) {
                excel.setCategory(category.get().getName());
            }
        }
        if (text.getEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }
        if (text.getKbase()!= null) {
            excel.setKbaseName(text.getKbase().getName());
        }
        // 将状态和向量状态转换为中文
        excel.setStatus(ChunkStatusEnum.toChineseDisplay(text.getElasticStatus()));
        excel.setVectorStatus(ChunkStatusEnum.toChineseDisplay(text.getVectorStatus()));
        return excel;
    }

    public TextEntity convertExcelToText(TextExcel excel, String kbType, String fileUid, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Text.class); // String categoryUid,
        // 检索问题+答案+kbUid是否已经存在，如果存在则不创建
        if (existsByTitleAndKbUidAndDeletedFalse(excel.getTitle(), kbUid)) {
            return null;
        }

        TextEntity text = TextEntity.builder().build();
        text.setUid(uidUtils.getUid());
        text.setTitle(excel.getTitle());
        text.setContent(excel.getContent());
        //
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            text.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .type(kbType)
                    .kbUid(kbUid)
                    .orgUid(orgUid)
                    .build();
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
            text.setCategoryUid(categoryResponse.getUid());
        }
        text.setOrgUid(orgUid);
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            text.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        return text;
    }


    public void initText(String kbUid, String orgUid) {
        if (textRepository.count() > 0) {
            return;
        }

        TextEntity text = TextEntity.builder()
                .uid(uidUtils.getUid())
                .title("初始化文本")
                .content("初始化文本内容")
                .enabled(true)
                .startDate(BdDateUtils.now())
                .endDate(BdDateUtils.now().plusDays(1))
                .build();
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            text.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        text.setOrgUid(orgUid);
        save(text);
    }

}
