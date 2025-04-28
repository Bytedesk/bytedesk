/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-28 17:41:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm.qa.event.QaUpdateDocEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QaRestService extends BaseRestServiceWithExcel<QaEntity, QaRequest, QaResponse, QaExcel> {

    private final QaRepository qaRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryService;

    private final AuthService authService;

    private final KbaseRestService kbaseRestService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final ThreadRestService threadRestService;

    private final MessageRestService messageRestService;

    @Override
    public Page<QaEntity> queryByOrgEntity(QaRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QaEntity> spec = QaSpecification.search(request);
        // 是否导出全部
        if (request.isExportAll()) {
            pageable = Pageable.unpaged();
        }
        return qaRepository.findAll(spec, pageable);
    }

    @Override
    public Page<QaResponse> queryByOrg(QaRequest request) {
        Page<QaEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QaResponse> queryByUser(QaRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Transactional
    @Override
    public QaResponse queryByUid(QaRequest request) {
        Optional<QaEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            QaEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            //
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update click count");
            }
            // TODO: 插入问题 + 答案 两条消息记录，目前放到发送消息里面
            // 插入问题消息

            return convertToResponse(savedEntity);
        }
        return null;
    }

    // 点击qa
    public QaResponse clickQa(QaRequest request) {
        Optional<QaEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            QaEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            //
            QaEntity savedEntity = qaRepository.save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update click count");
            }
            QaResponse qaResponse = convertToResponse(savedEntity);

            // 插入问题 + 答案 两条消息记录，目前放到发送消息里面
            // 插入问题消息
            Optional<ThreadEntity> thread = threadRestService.findByUid(request.getThreadUid());
            if (thread.isPresent()) {
                ThreadEntity threadEntity = thread.get();
                // 插入问题消息
                MessageEntity questionMessage = getQaQuestionMessage(qaResponse, threadEntity);
                MessageEntity savedQuestionMessage = messageRestService.save(questionMessage);
                if (savedQuestionMessage == null) {
                    throw new RuntimeException("Failed to insert question message");
                }
                qaResponse.setQuestionMessage(ConvertUtils.convertToMessageResponse(savedQuestionMessage));
                // 
                MessageEntity answerMessage = getQaAnswerMessage(qaResponse, threadEntity);
                MessageEntity savedAnswerMessage = messageRestService.save(answerMessage);
                if (savedAnswerMessage == null) {
                    throw new RuntimeException("Failed to insert answer message");
                }
                qaResponse.setAnswerMessage(ConvertUtils.convertToMessageResponse(savedAnswerMessage));
            }
            //
            return qaResponse;
        }
        return null;
    }


    @Cacheable(value = "qa", key = "#uid", unless = "#result == null")
    @Override
    public Optional<QaEntity> findByUid(String uid) {
        return qaRepository.findByUid(uid);
    }

    @Cacheable(value = "qa", key = "#kbUid", unless = "#result == null")
    public List<QaEntity> findByKbUid(String kbUid) {
        return qaRepository.findByKbase_Uid(kbUid);
    }

    @Cacheable(value = "qa", key = "#question", unless = "#result == null")
    public List<QaEntity> findByQuestionContains(String question) {
        return qaRepository.findByQuestionContains(question);
    }

    public Boolean existsByQuestionAndAnswerAndKbUidAndOrgUid(String question, String answer, String kbUid,
            String orgUid) {
        return qaRepository.existsByQuestionAndAnswerAndKbase_UidAndOrgUidAndDeletedFalse(question, answer, kbUid,
                orgUid);
    }

    public Boolean existsByUid(String uid) {
        return qaRepository.existsByUid(uid);
    }

    @Override
    @Transactional
    public QaResponse create(QaRequest request) {

        // 如果提供了uid，先尝试查找现有记录
        if (StringUtils.hasText(request.getUid())) {
            Optional<QaEntity> existingQa = findByUid(request.getUid());
            if (existingQa.isPresent()) {
                return convertToResponse(existingQa.get());
            }
        }
        // 创建新记录
        QaEntity entity = modelMapper.map(request, QaEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        UserEntity user = authService.getUser();
        if (user != null) {
            entity.setUserUid(user.getUid());
        }
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        QaEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Failed to create QA");
        }
        //
        return convertToResponse(savedEntity);

    }

    @Override
    @Transactional
    public QaResponse update(QaRequest request) {

        Optional<QaEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            //
            // 判断question/answer/questionList/answerList是否有变化，如果其中一个发生变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                QaUpdateDocEvent qaUpdateDocEvent = new QaUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(qaUpdateDocEvent);
            }
            //
            // modelMapper.map(request, entity);
            entity.setQuestion(request.getQuestion());
            entity.setQuestionList(request.getQuestionList());
            entity.setAnswer(request.getAnswer());
            entity.setAnswerHtml(request.getAnswerHtml());
            entity.setAnswerMarkdown(request.getAnswerMarkdown());
            entity.setImages(request.getImages());
            entity.setAttachments(request.getAttachments());
            entity.setAnswerList(request.getAnswerList());
            entity.setStatus(request.getStatus());
            entity.setTagList(request.getTagList());
            entity.setType(request.getType());
            entity.setEnabled(request.getEnabled());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setCategoryUid(request.getCategoryUid());
            //
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to create QA");
            }
            //
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("qa not found");
        }
    }

    // update enable
    public QaResponse enable(QaRequest request) {
        Optional<QaEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            //
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update QA");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("qa not found");
        }
    }

    public QaResponse rateUp(String uid) {
        Optional<QaEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            entity.increaseUpCount();
            //
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to rate up QA");
            }
            // TODO: 更新消息状态

            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("qa not found");
        }
    }

    public QaResponse rateDown(String uid) {
        Optional<QaEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            entity.increaseDownCount();
            //
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to rate down QA");
            }
            // TODO: 更新消息状态

            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("qa not found");
        }
    }

    @Override
    protected QaEntity doSave(QaEntity entity) {
        return qaRepository.save(entity);
    }

    @Override
    public QaEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            QaEntity entity) {
        try {
            log.warn("处理乐观锁冲突: {}", entity.getUid());
            Optional<QaEntity> latest = qaRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                QaEntity latestEntity = latest.get();
                // 保持原有实体的部分属性
                latestEntity.setQuestion(entity.getQuestion());
                latestEntity.setAnswer(entity.getAnswer());
                latestEntity.setAnswerList(entity.getAnswerList());
                latestEntity.setType(entity.getType());
                latestEntity.setEnabled(entity.isEnabled());
                latestEntity.setCategoryUid(entity.getCategoryUid());
                // latestEntity.setKbUid(entity.getKbUid());

                // 处理相关问答时特别小心
                if (entity.getRelatedQas() != null && !entity.getRelatedQas().isEmpty()) {
                    latestEntity.setRelatedQas(entity.getRelatedQas());
                }

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setStatus(entity.getStatus());
                latestEntity.setTagList(entity.getTagList());

                return qaRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }

        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    public void saveAll(List<QaEntity> entities) {
        qaRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QaEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
        }
    }

    @Override
    public void delete(QaRequest entity) {
        deleteByUid(entity.getUid());
    }

    public void delateAll(QaRequest request) {
        // 查询kbUid所有的问答对
        List<QaEntity> qaEntities = findByKbUid(request.getKbUid());
        // 遍历所有的问答对，设置deleted为true
        for (QaEntity qaEntity : qaEntities) {
            qaEntity.setDeleted(true);
            save(qaEntity);
        }
    }

    @Override
    public QaResponse convertToResponse(QaEntity entity) {
        QaResponse response = modelMapper.map(entity, QaResponse.class);

        // 处理相关问题，避免循环依赖
        if (entity.getRelatedQas() != null) {
            List<QaResponseSimple> simpleQas = entity.getRelatedQas().stream()
                    .map(relatedQa -> QaResponseSimple.builder()
                            .uid(relatedQa.getUid())
                            .question(relatedQa.getQuestion())
                            .answer(relatedQa.getAnswer())
                            .build())
                    .collect(Collectors.toList());
            response.setRelatedQas(simpleQas);
        }

        return response;
    }

    @Override
    public QaExcel convertToExcel(QaEntity qa) {
        QaExcel excel = modelMapper.map(qa, QaExcel.class);
        if (StringUtils.hasText(qa.getCategoryUid())) {
            Optional<CategoryEntity> categoryOptional = categoryService.findByUid(qa.getCategoryUid());
            if (categoryOptional.isPresent()) {
                excel.setCategory(categoryOptional.get().getName());
            } else {
                excel.setCategory("未分类");
            }
        } else {
            excel.setCategory("未分类");
        }
        excel.setAnswerList(JSON.toJSONString(qa.getAnswerList()));
        if (qa.isEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }

        return excel;
    }

    public QaEntity convertExcelToQa(QaExcel excel, String uploadType, String fileUid, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Qa.class); // String categoryUid,
        // 检索问题+答案+kbUid+orgUid是否已经存在，如果存在则不创建新的问答对
        if (existsByQuestionAndAnswerAndKbUidAndOrgUid(excel.getQuestion(), excel.getAnswer(), kbUid, orgUid)) {
            return null;
        }

        QaEntity qaEntity = QaEntity.builder().build();
        qaEntity.setUid(uidUtils.getUid());
        qaEntity.setQuestion(excel.getQuestion());
        qaEntity.setAnswer(excel.getAnswer());
        qaEntity.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        //
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            qaEntity.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .type(uploadType)
                    .kbUid(kbUid)
                    .orgUid(orgUid)
                    .build();
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            qaEntity.setCategoryUid(categoryResponse.getUid());
        }
        qaEntity.setFileUid(fileUid);
        // qa.setKbUid(kbUid);
        qaEntity.setOrgUid(orgUid);
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            qaEntity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        return qaEntity;
    }

    public static MessageEntity getQaQuestionMessage(QaResponse qaResponse, ThreadEntity threadEntity) {
        // 
        QaMessageExtra questionExtra = QaMessageExtra.builder()
                .qaUid(qaResponse.getUid())
                .build();
        //
        String content = qaResponse.getQuestion();
        String extra = questionExtra.toJson();
        String user = threadEntity.getUser();
        //
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.QA_QUESTION.name())
                .status(MessageStatusEnum.SUCCESS.name())
                .client(threadEntity.getClient())
                .user(user)
                .orgUid(threadEntity.getOrgUid())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .thread(threadEntity)
                .extra(extra)
                .build();

        return message;
    }

    public static MessageEntity getQaAnswerMessage(QaResponse qaResponse, ThreadEntity threadEntity) {
        // 
        QaMessageExtra answerExtra = QaMessageExtra.builder()
                        .qaUid(qaResponse.getUid())
                        .relatedQas(qaResponse.getRelatedQas())
                        .build();
        // 
        String content = qaResponse.getAnswer();
        String extra = answerExtra.toJson();
        // 插入答案消息
        String answerUser = threadEntity.getRobot();
        if (threadEntity.isAgentType()) {
            answerUser = threadEntity.getAgent();
        } else if (answerUser == null) {
            answerUser = threadEntity.getWorkgroup();
        }
        // 
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.QA_ANSWER.name())
                .status(MessageStatusEnum.READ.name())
                .client(ClientEnum.SYSTEM.name())
                .user(answerUser)
                .orgUid(threadEntity.getOrgUid())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .thread(threadEntity)
                .extra(extra)
                .build();

        return message;
    }

    
}
