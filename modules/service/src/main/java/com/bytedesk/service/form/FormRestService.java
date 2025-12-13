/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 16:05:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FormRestService extends BaseRestService<FormEntity, FormRequest, FormResponse> {

    private final FormRepository formRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<FormEntity> createSpecification(FormRequest request) {
        return FormSpecification.search(request, authService);
    }

    @Override
    protected Page<FormEntity> executePageQuery(Specification<FormEntity> spec, Pageable pageable) {
        return formRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<FormEntity> findByUid(String uid) {
        return formRepository.findByUid(uid);
    }

    public Page<FormResponse> queryAvailableForms(FormRequest request) {
        return queryByOrg(request);
    }

    @Override
    public FormResponse create(FormRequest request) {
        FormEntity entity = modelMapper.map(request, FormEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserEntity user = authService.getCurrentUser();
        if (user != null) {
            entity.setUserUid(user.getUid());
        }
        // 
        FormEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create form failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FormResponse update(FormRequest request) {
        Optional<FormEntity> optional = formRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FormEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FormEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update form failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Form not found");
        }
    }

    @Override
    protected FormEntity doSave(FormEntity entity) {
        return formRepository.save(entity);
    }

    @Override
    public FormEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FormEntity entity) {
        try {
            Optional<FormEntity> latest = formRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FormEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return formRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FormEntity> optional = formRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("Form not found");
        }
    }

    @Override
    public void delete(FormRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FormResponse convertToResponse(FormEntity entity) {
        return modelMapper.map(entity, FormResponse.class);
    }

    @Transactional
    public void initTicketForms(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return;
        }
        ensureTicketForm(orgUid, FormTypeEnum.TICKET_INTERNAL);
        ensureTicketForm(orgUid, FormTypeEnum.TICKET_EXTERNAL);
    }

    private void ensureTicketForm(String orgUid, FormTypeEnum type) {
        Optional<FormEntity> existing = formRepository
                .findFirstByOrgUidAndTypeAndDeletedFalseOrderByCreatedAtAsc(orgUid, type.name());
        if (existing.isPresent()) {
            return;
        }

        FormEntity entity = FormEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .type(type.name())
                .status(FormStatusEnum.PUBLISHED.name())
                .enabled(true)
                .name(resolveDefaultFormName(type))
                .description(resolveDefaultFormDescription(type))
                .schema(buildDefaultTicketFormSchema(type))
                .build();
        formRepository.save(entity);
        log.info("Initialized default {} form for org {}: {}", type.name(), orgUid, entity.getUid());
    }

    private String buildDefaultTicketFormSchema(FormTypeEnum type) {
        List<Map<String, Object>> components = buildComponentsForType(type);
        return JSON.toJSONString(components);
    }

    private List<Map<String, Object>> buildComponentsForType(FormTypeEnum type) {
        List<Map<String, Object>> components = new ArrayList<>();
        if (FormTypeEnum.TICKET_INTERNAL.equals(type)) {
            components.add(formComponent(
                    "type",
                    "select",
                    "问题类型",
                    true,
                    0,
                    List.of("技术问题", "产品咨询", "功能建议", "其他"),
                    null));
            // components.add(formComponent(
            //         "email",
            //         "input",
            //         "联系邮箱",
            //         true,
            //         1,
            //         null,
            //         Map.of("type", "email")));
            // components.add(formComponent(
            //         "mobile",
            //         "input",
            //         "联系电话",
            //         false,
            //         2,
            //         null,
            //         Map.of("type", "tel")));
            components.add(formComponent(
                    "expectedTime",
                    "input",
                    "期望解决时间",
                    false,
                    3,
                    null,
                    Map.of("type", "datetime-local")));
            components.add(formComponent(
                    "remark",
                    "textarea",
                    "备注说明",
                    false,
                    4,
                    null,
                    Map.of("rows", 3)));
        } else if (FormTypeEnum.TICKET_EXTERNAL.equals(type)) {
            components.add(formComponent(
                    "nickname",
                    "input",
                    "您的姓名",
                    true,
                    0,
                    null,
                    null));
            components.add(formComponent(
                    "email",
                    "input",
                    "联系邮箱",
                    true,
                    1,
                    null,
                    Map.of("type", "email")));
            components.add(formComponent(
                    "mobile",
                    "input",
                    "联系电话",
                    true,
                    2,
                    null,
                    Map.of("type", "tel")));
            components.add(formComponent(
                    "type",
                    "select",
                    "问题类型",
                    true,
                    3,
                    List.of("产品咨询", "功能使用", "技术问题", "账户问题", "付费相关", "投诉建议", "其他"),
                    null));
            components.add(formComponent(
                    "title",
                    "input",
                    "问题标题",
                    true,
                    4,
                    null,
                    null));
            components.add(formComponent(
                    "content",
                    "textarea",
                    "详细描述",
                    true,
                    5,
                    null,
                    Map.of("rows", 4)));
        }
        return components;
    }

    private Map<String, Object> formComponent(
            String id,
            String componentType,
            String label,
            boolean required,
            int index,
            List<String> options,
            Map<String, Object> propsOverride) {
        Map<String, Object> component = new LinkedHashMap<>();
        component.put("id", id);
        component.put("type", componentType);
        component.put("label", label);
        component.put("required", required);
        component.put("index", index);
        if (options != null && !options.isEmpty()) {
            component.put("options", options);
        }
        Map<String, Object> props = componentProps(label, propsOverride);
        if (!props.isEmpty()) {
            component.put("props", props);
        }
        return component;
    }

    private Map<String, Object> componentProps(String label, Map<String, Object> override) {
        Map<String, Object> props = new LinkedHashMap<>();
        if (StringUtils.hasText(label)) {
            props.put("placeholder", "请输入" + label);
        }
        if (override != null && !override.isEmpty()) {
            props.putAll(override);
        }
        return props;
    }

    private String resolveDefaultFormName(FormTypeEnum type) {
        if (FormTypeEnum.TICKET_INTERNAL.equals(type)) {
            return "默认内部工单表单";
        }
        if (FormTypeEnum.TICKET_EXTERNAL.equals(type)) {
            return "默认外部工单表单";
        }
        return "默认表单";
    }

    private String resolveDefaultFormDescription(FormTypeEnum type) {
        if (FormTypeEnum.TICKET_INTERNAL.equals(type)) {
            return "用于企业内部协同处理工单的默认表单";
        }
        if (FormTypeEnum.TICKET_EXTERNAL.equals(type)) {
            return "用于访客/客户提交工单的默认表单";
        }
        return "系统自动初始化的默认表单";
    }

    

    
}
