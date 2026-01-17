/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 11:07:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form_result;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FormResultRestService extends BaseRestServiceWithExport<FormResultEntity, FormResultRequest, FormResultResponse, FormResultExcel> {

    private final FormResultRepository tagRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final MessageRestService messageRestService;

    @Override
    protected Specification<FormResultEntity> createSpecification(FormResultRequest request) {
        return FormResultSpecification.search(request, authService);
    }

    @Override
    protected Page<FormResultEntity> executePageQuery(Specification<FormResultEntity> spec, Pageable pageable) {
        return tagRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<FormResultEntity> findByUid(String uid) {
        return tagRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return tagRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public FormResultResponse create(FormResultRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        FormResultEntity entity = modelMapper.map(request, FormResultEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        FormResultEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }

        // 可选：回写表单结果到对应的表单消息 content 中
        if (StringUtils.hasText(request.getMessageUid())) {
            try {
                Optional<MessageEntity> messageOptional = messageRestService.findByUid(request.getMessageUid());
                if (messageOptional.isPresent()) {
                    MessageEntity messageEntity = messageOptional.get();
                    // 简单校验：orgUid 不一致则不回写
                    if (StringUtils.hasText(request.getOrgUid())
                            && StringUtils.hasText(messageEntity.getOrgUid())
                            && !request.getOrgUid().equals(messageEntity.getOrgUid())) {
                        log.warn("Skip form message update due to orgUid mismatch, messageUid={}, requestOrgUid={}, messageOrgUid={}",
                                request.getMessageUid(), request.getOrgUid(), messageEntity.getOrgUid());
                    } else {
                        JSONObject contentJson = null;
                        if (StringUtils.hasText(messageEntity.getContent())) {
                            contentJson = JSON.parseObject(messageEntity.getContent());
                        }
                        if (contentJson == null) {
                            contentJson = new JSONObject();
                        }
                        // formData 是 JSON 字符串，前端可自行解析；同时保存 formResultUid 便于后续关联
                        contentJson.put("formData", request.getFormData());
                        contentJson.put("formResultUid", savedEntity.getUid());
                        messageEntity.setContent(contentJson.toJSONString());
                        messageRestService.save(messageEntity);
                    }
                } else {
                    log.warn("Form message not found, messageUid={}", request.getMessageUid());
                }
            } catch (Exception e) {
                // 不影响表单提交主流程
                log.warn("Failed to update form message content, messageUid={}, error={}", request.getMessageUid(), e.getMessage());
            }
        }

        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public FormResultResponse update(FormResultRequest request) {
        Optional<FormResultEntity> optional = tagRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FormResultEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FormResultEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("FormResult not found");
        }
    }

    @Override
    protected FormResultEntity doSave(FormResultEntity entity) {
        return tagRepository.save(entity);
    }

    @Override
    public FormResultEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FormResultEntity entity) {
        try {
            Optional<FormResultEntity> latest = tagRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FormResultEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setFormUid(entity.getFormUid());
                latestEntity.setType(entity.getType());
                latestEntity.setUser(entity.getUser());
                latestEntity.setFormData(entity.getFormData());
                latestEntity.setFormVersion(entity.getFormVersion());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tagRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<FormResultEntity> optional = tagRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("FormResult not found");
        }
    }

    @Override
    public void delete(FormResultRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FormResultResponse convertToResponse(FormResultEntity entity) {
        return modelMapper.map(entity, FormResultResponse.class);
    }

    @Override
    public FormResultExcel convertToExcel(FormResultEntity entity) {
        return modelMapper.map(entity, FormResultExcel.class);
    }
    
}
