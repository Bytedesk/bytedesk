/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 12:52:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.FaqJsonLoader.Faq;
import com.bytedesk.kbase.faq.FaqJsonLoader.FaqConfiguration;
import com.bytedesk.kbase.faq.event.FaqUpdateDocEvent;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.utils.KbaseConvertUtils;
import com.bytedesk.service.message_rating.FaqRatingRequest;
import com.bytedesk.service.message_rating.FaqRatingRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FaqRestService extends BaseRestServiceWithExcel<FaqEntity, FaqRequest, FaqResponse, FaqExcel> {

    private final FaqRepository faqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryService;

    private final FaqJsonLoader faqJsonLoader;

    private final AuthService authService;

    private final KbaseRestService kbaseRestService;

    private final ThreadRestService threadRestService;

    private final MessageRestService messageRestService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    public Page<FaqEntity> queryByOrgEntity(FaqRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FaqEntity> spec = FaqSpecification.search(request);
        return faqRepository.findAll(spec, pageable);
    }

    @Override
    public Page<FaqResponse> queryByOrg(FaqRequest request) {
        Page<FaqEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FaqResponse> queryByUser(FaqRequest request) {
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
    public FaqResponse queryByUid(FaqRequest request) {
        Optional<FaqEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            FaqEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            //
            FaqEntity savedEntity = faqRepository.save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update click count");
            }
            //
            return convertToResponse(savedEntity);
        }
        return null;
    }

    // 点击faq
    public FaqResponse clickFaq(FaqRequest request) {
        Optional<FaqEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            FaqEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            //
            FaqEntity savedEntity = faqRepository.save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update click count");
            }
            FaqResponse faqResponse = convertToResponse(savedEntity);

            // 插入问题 + 答案 两条消息记录，目前放到发送消息里面
            // 插入问题消息
            Optional<ThreadEntity> thread = threadRestService.findByUid(request.getThreadUid());
            if (thread.isPresent()) {
                ThreadEntity threadEntity = thread.get();
                // 插入问题消息
                MessageEntity questionMessage = getFaqQuestionMessage(faqResponse, threadEntity);
                MessageEntity savedQuestionMessage = messageRestService.save(questionMessage);
                if (savedQuestionMessage == null) {
                    throw new RuntimeException("Failed to insert question message");
                }
                faqResponse.setQuestionMessage(ConvertUtils.convertToMessageResponse(savedQuestionMessage));
                // 
                MessageEntity answerMessage = getFaqAnswerMessage(faqResponse, threadEntity);
                MessageEntity savedAnswerMessage = messageRestService.save(answerMessage);
                if (savedAnswerMessage == null) {
                    throw new RuntimeException("Failed to insert answer message");
                }
                faqResponse.setAnswerMessage(ConvertUtils.convertToMessageResponse(savedAnswerMessage));
            }
            //
            return faqResponse;
        }
        return null;
    }

    @Cacheable(value = "faq", key = "#uid", unless = "#result == null")
    @Override
    public Optional<FaqEntity> findByUid(String uid) {
        return faqRepository.findByUid(uid);
    }

    @Cacheable(value = "faq", key = "#kbUid", unless = "#result == null")
    public List<FaqEntity> findByKbUid(String kbUid) {
        return faqRepository.findByKbase_Uid(kbUid);
    }

    @Cacheable(value = "faq", key = "#question", unless = "#result == null")
    public List<FaqEntity> findByQuestionContains(String question) {
        return faqRepository.findByQuestionContains(question);
    }

    public Boolean existsByQuestionAndAnswerAndKbUidAndOrgUid(String question, String answer, String kbUid,
            String orgUid) {
        return faqRepository.existsByQuestionAndAnswerAndKbase_UidAndOrgUidAndDeletedFalse(question, answer, kbUid,
                orgUid);
    }

    public Boolean existsByUid(String uid) {
        return faqRepository.existsByUid(uid);
    }

    @Override
    @Transactional
    public FaqResponse create(FaqRequest request) {
        try {
            // 如果提供了uid，先尝试查找现有记录
            if (StringUtils.hasText(request.getUid())) {
                Optional<FaqEntity> existingFaq = findByUid(request.getUid());
                if (existingFaq.isPresent()) {
                    return convertToResponse(existingFaq.get());
                }
            }
            // 创建新记录
            FaqEntity entity = modelMapper.map(request, FaqEntity.class);
            if (!StringUtils.hasText(request.getUid())) {
                entity.setUid(uidUtils.getUid());
            }
            UserEntity user = authService.getUser();
            if (user != null) {
                entity.setUserUid(user.getUid());
            }
            //
            // 根据request.relatedFaqUids查找关联的FAQ
            List<FaqEntity> relatedFaqs = new ArrayList<>();
            for (String relatedFaqUid : request.getRelatedFaqUids()) {
                Optional<FaqEntity> relatedFaq = findByUid(relatedFaqUid);
                if (relatedFaq.isPresent()) {
                    relatedFaqs.add(relatedFaq.get());
                } else {
                    throw new RuntimeException("relatedFaqUid not found");
                }
            }
            entity.setRelatedFaqs(relatedFaqs);
            //
            Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
            if (kbase.isPresent()) {
                entity.setKbase(kbase.get());
            } else {
                throw new RuntimeException("kbaseUid not found");
            }

            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to create FAQ");
            }
            //
            return convertToResponse(savedEntity);

        } catch (Exception e) {
            log.error("Error creating FAQ: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating FAQ", e);
        }
    }

    @Override
    @Transactional
    public FaqResponse update(FaqRequest request) {

        Optional<FaqEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            // 
            // 判断question/answer/questionList/answerList是否有变化，如果其中一个发生变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                FaqUpdateDocEvent qaUpdateDocEvent = new FaqUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(qaUpdateDocEvent);
            }
            // 设置属性
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

            // 处理相关FAQ
            if (request.getRelatedFaqUids() != null && !request.getRelatedFaqUids().isEmpty()) {
                List<FaqEntity> relatedFaqs = new ArrayList<>();
                for (String relatedFaqUid : request.getRelatedFaqUids()) {
                    try {
                        Optional<FaqEntity> relatedFaq = findByUid(relatedFaqUid);
                        if (relatedFaq.isPresent()) {
                            relatedFaqs.add(relatedFaq.get());
                        }
                    } catch (Exception e) {
                        log.warn("无法加载相关的FAQ: {}, 原因: {}", relatedFaqUid, e.getMessage());
                        // 继续处理其他相关FAQ，不要中断
                    }
                }
                entity.setRelatedFaqs(relatedFaqs);
            }

            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update FAQ");
            }

            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    public FaqResponse enable(FaqRequest request) {
        Optional<FaqEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update FAQ");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    // rate message extra helpful
    public MessageResponse rateUp(FaqRequest request) {
        Optional<MessageEntity> messageOptional = messageRestService.findByUid(request.getMessageUid());
        if (messageOptional.isPresent()) {
            MessageEntity message = messageOptional.get();
            message.setStatus(MessageStatusEnum.RATE_UP.name());
            //
            MessageEntity savedMessage = messageRestService.save(message);
            if (savedMessage == null) {
                throw new RuntimeException("Message not saved");
            }
            // 添加faq的点赞数
            FaqMessageExtra faqMessageExtra = FaqMessageExtra.fromJson(savedMessage.getExtra());
            if (faqMessageExtra != null) {
                String faqUid = faqMessageExtra.getFaqUid();
                Optional<FaqEntity> optionalFaq = findByUid(faqUid);
                if (optionalFaq.isPresent()) {
                    FaqEntity faqEntity = optionalFaq.get();
                    faqEntity.increaseUpCount();
                    faqRepository.save(faqEntity);
                }
                // 暂不在faqRating记录rateUp记录
                // FaqRatingRequest faqRatingRequest = FaqRatingRequest.builder()
                //         .messageUid(request.getMessageUid())
                //         .threadUid(request.getThreadUid())
                //         .rateType(MessageStatusEnum.RATE_UP.name())
                //         .faqUid(faqUid)
                //         .build();
                // faqRatingRestService.create(faqRatingRequest);
            }
            return ConvertUtils.convertToMessageResponse(savedMessage);
        }
        // 
        return null;
    }

    // rate message extra unhelpful
    public MessageResponse rateDown(FaqRequest request) {
        Optional<MessageEntity> optionalMessage = messageRestService.findByUid(request.getMessageUid());
        if (optionalMessage.isPresent()) {
            MessageEntity message = optionalMessage.get();
            message.setStatus(MessageStatusEnum.RATE_DOWN.name());
            //
            MessageEntity savedMessage = messageRestService.save(message);
            if (savedMessage == null) {
                throw new RuntimeException("Message not saved");
            }
            // 添加faq的点踩数
            FaqMessageExtra faqMessageExtra = FaqMessageExtra.fromJson(savedMessage.getExtra());
            if (faqMessageExtra != null) {
                String faqUid = faqMessageExtra.getFaqUid();
                Optional<FaqEntity> optionalFaq = findByUid(faqUid);
                if (optionalFaq.isPresent()) {
                    FaqEntity faqEntity = optionalFaq.get();
                    faqEntity.increaseDownCount();
                    faqRepository.save(faqEntity);
                }
                // 暂不在faqRating记录rateDown记录
                // FaqRatingRequest faqRatingRequest = FaqRatingRequest.builder()
                //         .messageUid(request.getMessageUid())
                //         .threadUid(request.getThreadUid())
                //         .rateType(MessageStatusEnum.RATE_DOWN.name())
                //         .faqUid(faqUid)
                //         .build();
                // faqRatingRestService.create(faqRatingRequest);
            }
            return ConvertUtils.convertToMessageResponse(savedMessage);
        }
        return null;
    }

    // rate message extra feedback
    public MessageResponse rateFeedback(FaqRequest request) {
        Optional<MessageEntity> optionalMessage = messageRestService.findByUid(request.getMessageUid());
        if (optionalMessage.isPresent()) {
            MessageEntity message = optionalMessage.get();
            message.setStatus(MessageStatusEnum.RATE_FEEDBACK.name());
            //
            MessageEntity savedMessage = messageRestService.save(message);
            if (savedMessage == null) {
                throw new RuntimeException("Message not saved");
            }
            // 记录反馈
            FaqMessageExtra faqMessageExtra = FaqMessageExtra.fromJson(savedMessage.getExtra());
            if (faqMessageExtra != null) {
                String faqUid = faqMessageExtra.getFaqUid();
                // 
                FaqRatingRequest faqRatingRequest = FaqRatingRequest.builder()
                        .messageUid(request.getMessageUid())
                        .threadUid(request.getThreadUid())
                        .rateType(MessageStatusEnum.RATE_FEEDBACK.name())
                        .rateDownTagList(request.getRateDownTagList())
                        .rateDownReason(request.getRateDownReason())
                        .faqUid(faqUid)
                        .user(request.getUser())
                        .orgUid(savedMessage.getOrgUid())
                        .build();
                faqRatingRestService.create(faqRatingRequest);
            }
            // 
            return ConvertUtils.convertToMessageResponse(savedMessage);
        }
        return null;
    }

    // rate message transfer
    public MessageResponse rateTransfer(FaqRequest request) {
        Optional<MessageEntity> optionalMessage = messageRestService.findByUid(request.getMessageUid());
        if (optionalMessage.isPresent()) {
            MessageEntity message = optionalMessage.get();
            message.setStatus(MessageStatusEnum.RATE_TRANSFER.name());
            //
            MessageEntity savedMessage = messageRestService.save(message);
            if (savedMessage == null) {
                throw new RuntimeException("Message not saved");
            }
            // 记录转人工
            FaqMessageExtra faqMessageExtra = FaqMessageExtra.fromJson(savedMessage.getExtra());
            if (faqMessageExtra != null) {
                String faqUid = faqMessageExtra.getFaqUid();
                // 
                FaqRatingRequest faqRatingRequest = FaqRatingRequest.builder()
                        .messageUid(request.getMessageUid())
                        .threadUid(request.getThreadUid())
                        .rateType(MessageStatusEnum.RATE_TRANSFER.name())
                        .faqUid(faqUid)
                        .build();
                faqRatingRestService.create(faqRatingRequest);
            }
            return ConvertUtils.convertToMessageResponse(savedMessage);
        }
        return null;
    }

    @Override
    protected FaqEntity doSave(FaqEntity entity) {
        return faqRepository.save(entity);
    }

    @Override
    public FaqEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            FaqEntity entity) {
        try {
            log.warn("处理乐观锁冲突: {}", entity.getUid());
            // 重新获取最新版本的实体
            Optional<FaqEntity> latest = faqRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FaqEntity latestEntity = latest.get();
                // 保持原有实体的部分属性
                latestEntity.setQuestion(entity.getQuestion());
                latestEntity.setAnswer(entity.getAnswer());
                latestEntity.setAnswerList(entity.getAnswerList());
                latestEntity.setType(entity.getType());
                latestEntity.setEnabled(entity.isEnabled());
                latestEntity.setCategoryUid(entity.getCategoryUid());
                // latestEntity.setKbUid(entity.getKbUid());

                // 避免覆盖计数器类字段
                // latestEntity.setViewCount(entity.getViewCount());
                // latestEntity.setClickCount(entity.getClickCount());
                // latestEntity.setUpCount(entity.getUpCount());
                // latestEntity.setDownCount(entity.getDownCount());

                // 处理相关FAQ时特别小心
                if (entity.getRelatedFaqs() != null && !entity.getRelatedFaqs().isEmpty()) {
                    latestEntity.setRelatedFaqs(entity.getRelatedFaqs());
                }

                // docIdList
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setStatus(entity.getStatus());

                return faqRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
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
        deleteByUid(entity.getUid());
    }

    public void delateAll(FaqRequest request) {
        // 查询kbUid所有的问答对
        List<FaqEntity> faqEntities = findByKbUid(request.getKbUid());
        // 遍历所有的问答对，设置deleted为true
        for (FaqEntity faqEntity : faqEntities) {
            faqEntity.setDeleted(true);
            save(faqEntity);
        }
    }

    @Override
    public FaqResponse convertToResponse(FaqEntity entity) {
        return KbaseConvertUtils.convertToFaqResponse(entity);
    }

    @Override
    public FaqExcel convertToExcel(FaqEntity faq) {
        FaqExcel excel = modelMapper.map(faq, FaqExcel.class);
        if (StringUtils.hasText(faq.getCategoryUid())) {
            Optional<CategoryEntity> categoryOptional = categoryService.findByUid(faq.getCategoryUid());
            if (categoryOptional.isPresent()) {
                excel.setCategory(categoryOptional.get().getName());
            } else {
                excel.setCategory("未分类");
            }
        } else {
            excel.setCategory("未分类");
        }
        excel.setAnswerList(JSON.toJSONString(faq.getAnswerList()));
        if (faq.isEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }
        return excel;
    }

    public FaqEntity convertExcelToFaq(FaqExcel excel, String uploadType, String fileUid, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Faq.class); // String categoryUid,
        // 检索问题+答案+kbUid+orgUid是否已经存在，如果存在则不创建新的问答对
        if (existsByQuestionAndAnswerAndKbUidAndOrgUid(excel.getQuestion(), excel.getAnswer(), kbUid, orgUid)) {
            return null;
        }

        FaqEntity faq = FaqEntity.builder().build();
        faq.setUid(uidUtils.getUid());
        faq.setQuestion(excel.getQuestion());
        faq.setAnswer(excel.getAnswer());
        faq.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        //
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            faq.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .type(uploadType)
                    .kbUid(kbUid)
                    .orgUid(orgUid)
                    .build();
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            faq.setCategoryUid(categoryResponse.getUid());
        }
        faq.setFileUid(fileUid);
        faq.setOrgUid(orgUid);
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            faq.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        return faq;
    }

    public void saveFaqPairs(String qaPairs, String kbUid, String orgUid) {
        if (!StringUtils.hasText(qaPairs)) {
            return;
        }

        try {
            // Clean up the JSON string
            String cleanJson = qaPairs;
            int jsonStart = qaPairs.indexOf("```json");
            if (jsonStart != -1) {
                // Find the start of the actual JSON after ```json
                int contentStart = qaPairs.indexOf("{", jsonStart);
                int contentEnd = qaPairs.lastIndexOf("}");
                if (contentStart != -1 && contentEnd != -1) {
                    cleanJson = qaPairs.substring(contentStart, contentEnd + 1);
                }
            } else {
                // If no ```json marker, try to find JSON directly
                int contentStart = qaPairs.indexOf("{");
                int contentEnd = qaPairs.lastIndexOf("}");
                if (contentStart != -1 && contentEnd != -1) {
                    cleanJson = qaPairs.substring(contentStart, contentEnd + 1);
                }
            }

            // Parse JSON array of QA pairs
            JSONObject jsonObject = JSON.parseObject(cleanJson);
            List<JSONObject> qaList = jsonObject.getList("qaPairs", JSONObject.class);
            if (qaList == null || qaList.isEmpty()) {
                log.warn("No QA pairs found in response");
                return;
            }

            for (JSONObject qa : qaList) {
                String question = qa.getString("question");
                String answer = qa.getString("answer");
                // String tags = qa.getString("tags");

                if (StringUtils.hasText(question) && StringUtils.hasText(answer)) {
                    FaqEntity faq = FaqEntity.builder()
                            .uid(uidUtils.getUid())
                            .question(question)
                            .answer(answer)
                            .type(MessageTypeEnum.TEXT.name())
                            // .tags(tags)
                            // .kbUid(kbUid)
                            // .docId(docId)
                            .orgUid(orgUid)
                            .build();
                    //
                    Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
                    if (kbase.isPresent()) {
                        faq.setKbase(kbase.get());
                    } else {
                        throw new RuntimeException("kbaseUid not found");
                    }

                    save(faq);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing and saving QA pairs: {} \nContent: {}", e.getMessage(), qaPairs);
            throw new RuntimeException("Failed to save QA pairs", e);
        }
    }

    /**
     * 导入FAQ数据
     * 从JSON文件加载数据并存储到数据库
     * 
     * @return 导入的FAQ数量
     */
    @Transactional
    public void importFaqs(String orgUid, String kbUid) {
        if (faqRepository.count() > 0) {
            return;
        }

        try {
            // 加载JSON文件中的FAQ数据
            FaqConfiguration config = faqJsonLoader.loadFaqs();
            // String llmQaKbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID);

            // 遍历并保存每个FAQ
            for (Faq faq : config.getFaqs()) {
                String uid = Utils.formatUid(orgUid, faq.getUid());
                // 检查FAQ是否已存在
                if (!faqRepository.existsByUid(uid)) {
                    FaqRequest request = FaqRequest.builder()
                            .uid(uid)
                            .question(faq.getQuestion())
                            .answer(faq.getAnswer())
                            .type(MessageTypeEnum.TEXT.name())
                            .enabled(true)
                            // .autoSyncLlmQa(true)
                            // .llmQaKbUid(llmQaKbUid)
                            .kbUid(kbUid)
                            .orgUid(orgUid)
                            .build();
                    // 保存FAQ到数据库
                    create(request);
                    //
                    // QaRequest qaRequest = QaRequest.builder()
                    //         .question(faq.getQuestion())
                    //         .answer(faq.getAnswer())
                    //         .kbUid(llmQaKbUid)
                    //         .orgUid(orgUid)
                    //         .build();
                    // qaRestService.create(qaRequest);
                } else {
                    // log.info("FAQ already exists: {}", faq.getUid());
                }
            }
            // log.info("Successfully imported {} FAQs", count);
            // return count;
        } catch (Exception e) {
            log.error("Failed to import FAQs", e);
            throw new RuntimeException("Failed to import FAQs", e);
        }
    }

    @Transactional
    public void initRelationFaqs(String orgUid, String kbUid) {
        try {
            // 加载JSON文件中的FAQ数据
            FaqConfiguration config = faqJsonLoader.loadFaqs();

            // 创建5个示例多答案数据
            List<FaqAnswer> answerList = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                FaqAnswer answer = new FaqAnswer();
                answer.setVipLevel("" + i);
                answer.setAnswer("VIP " + i + " 专属回答：这是针对不同会员等级的答案示例");
                answerList.add(answer);
            }

            // 准备5个相关问题的UID列表
            List<String> relatedFaqUids = new ArrayList<>();
            for (int i = 5; i < 10; i++) {
                String relatedUid = Utils.formatUid(orgUid, "faq_00" + i);
                relatedFaqUids.add(relatedUid);
            }

            int count = 0;
            // 遍历并保存每个FAQ
            for (Faq faq : config.getFaqs()) {
                String uid = Utils.formatUid(orgUid, faq.getUid());
                // 构建FAQ请求
                FaqRequest request = FaqRequest.builder()
                        .uid(uid)
                        .question(faq.getQuestion())
                        .answer(faq.getAnswer())
                        .type(MessageTypeEnum.TEXT.name())
                        .enabled(true)
                        .kbUid(kbUid)
                        .orgUid(orgUid)
                        .build();
                // 为部分FAQ添加多答案和相关问题
                if (count < 5) {
                    request.setAnswerList(answerList);
                    request.setRelatedFaqUids(relatedFaqUids);
                }
                update(request);
                count++;
            }

            log.info("Successfully updated {} FAQs with related questions and multiple answers", count);
        } catch (Exception e) {
            log.error("Failed to initialize FAQ relations: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize FAQ relations", e);
        }
    }

    public static MessageEntity getFaqQuestionMessage(FaqResponse faqResponse, ThreadEntity threadEntity) {
        // 
        FaqMessageExtra questionExtra = FaqMessageExtra.builder()
                .faqUid(faqResponse.getUid())
                .build();
        //
        String content = faqResponse.getQuestion();
        String extra = questionExtra.toJson();
        String user = threadEntity.getUser();
        //
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.FAQ_QUESTION.name())
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

    public static MessageEntity getFaqAnswerMessage(FaqResponse faqResponse, ThreadEntity threadEntity) {
        // 
        FaqMessageExtra answerExtra = FaqMessageExtra.builder()
                        .faqUid(faqResponse.getUid())
                        .relatedFaqs(faqResponse.getRelatedFaqs())
                        .build();
        // 
        String content = faqResponse.getAnswer();
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
                .type(MessageTypeEnum.FAQ_ANSWER.name())
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
