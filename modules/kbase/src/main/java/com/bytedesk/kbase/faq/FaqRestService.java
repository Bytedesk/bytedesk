/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-20 00:03:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.ArrayList;
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
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.FaqJsonLoader.Faq;
import com.bytedesk.kbase.faq.FaqJsonLoader.FaqConfiguration;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;

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
            return convertToResponse(savedEntity);
        }
        return null;
    }

    @Cacheable(value = "faq", key = "#uid", unless = "#result == null")
    @Override
    public Optional<FaqEntity> findByUid(String uid) {
        return faqRepository.findByUid(uid);
    }

    @Cacheable(value = "faq", key = "#question", unless = "#result == null")
    public List<FaqEntity> findByQuestionContains(String question) {
        return faqRepository.findByQuestionContains(question);
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
            // 如何将string类型startDate和endDate转换为LocalDateTime类型？
            // if (StringUtils.hasText(request.getStartDate())) {
            //     entity.setStartDate(BdDateUtils.parseLocalDateTime(request.getStartDate()));
            // }
            // if (StringUtils.hasText(request.getEndDate())) {
            //     entity.setEndDate(BdDateUtils.parseLocalDateTime(request.getEndDate()));
            // }
            // entity.setType(MessageTypeEnum.fromValue(request.getType()).name());
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
                entity.setKbaseEntity(kbase.get());
            } else {
                throw new RuntimeException("kbaseUid not found");
            }

            try {
                FaqEntity savedEntity = save(entity);
                if (savedEntity != null) {
                    return convertToResponse(savedEntity);
                }
            } catch (Exception e) {
                // 如果保存时发生唯一键冲突，再次尝试查找
                if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    Optional<FaqEntity> existingFaq = findByUid(entity.getUid());
                    if (existingFaq.isPresent()) {
                        return convertToResponse(existingFaq.get());
                    }
                }
                throw e;
            }

            throw new RuntimeException("Failed to create FAQ");
        } catch (Exception e) {
            log.error("Error creating FAQ: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating FAQ", e);
        }
    }

    @Override
    @Transactional
    public FaqResponse update(FaqRequest request) {
        try {
            Optional<FaqEntity> optional = findByUid(request.getUid());
            if (optional.isPresent()) {
                FaqEntity entity = optional.get();
                // 设置属性
                entity.setQuestion(request.getQuestion());
                entity.setQuestionList(request.getQuestionList());
                entity.setAnswer(request.getAnswer());
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

                try {
                    return convertToResponse(save(entity));
                } catch (ObjectOptimisticLockingFailureException e) {
                    // 乐观锁异常，交给调用者处理重试
                    throw e;
                }
            } else {
                throw new RuntimeException("faq not found");
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            // 原样传递乐观锁异常，以便上层方法处理重试
            throw e;
        } catch (Exception e) {
            log.error("更新FAQ失败: {}, 原因: {}", request.getUid(), e.getMessage());
            throw new RuntimeException("更新FAQ失败", e);
        }
    }

    // update enable
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

    public FaqResponse rateUp(String uid) {
        Optional<FaqEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            entity.increaseUpCount();
            // 
            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to rate up FAQ");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    public FaqResponse rateDown(String uid) {
        Optional<FaqEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            FaqEntity entity = optional.get();
            entity.increaseDownCount();
            // 
            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to rate down FAQ");
            }
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    @Override
    public FaqEntity save(FaqEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("发生乐观锁冲突，尝试处理: {}", entity.getUid());
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected FaqEntity doSave(FaqEntity entity) {
        return faqRepository.save(entity);
    }

    @Override
    public FaqEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FaqEntity entity) {
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

    @Override
    public FaqResponse convertToResponse(FaqEntity entity) {
        FaqResponse response = modelMapper.map(entity, FaqResponse.class);
        // FaqResponse response = FaqResponse.builder()
        //         .uid(entity.getUid())
        //         .question(entity.getQuestion())
        //         .answer(entity.getAnswer())
        //         .answerList(entity.getAnswerList())
        //         .isLlmQa(entity.isLlmQa())
        //         .type(entity.getType())
        //         .status(entity.getStatus())
        //         .viewCount(entity.getViewCount())
        //         .clickCount(entity.getClickCount())
        //         .upCount(entity.getUpCount())
        //         .downCount(entity.getDownCount())
        //         // .downShowTransferToAgentButton(entity.isDownShowTransferToAgentButton())
        //         .enabled(entity.isEnabled())
        //         .tagList(entity.getTagList())
        //         .startDate(entity.getStartDate())
        //         .endDate(entity.getEndDate())
        //         .categoryUid(entity.getCategoryUid())
        //         .kbUid(entity.getKbaseEntity().getUid())
        //         .fileUid(entity.getFileUid())
        //         // .docUid(entity.getDocId())
        //         .createdAt(entity.getCreatedAt())
        //         .updatedAt(entity.getUpdatedAt())
        //         .build();

        // 处理相关问题，避免循环依赖
        if (entity.getRelatedFaqs() != null) {
            List<FaqResponse.SimpleFaqResponse> simpleFaqs = entity.getRelatedFaqs().stream()
                    .map(relatedFaq -> FaqResponse.SimpleFaqResponse.builder()
                            .uid(relatedFaq.getUid())
                            .question(relatedFaq.getQuestion())
                            .answer(relatedFaq.getAnswer())
                            .type(relatedFaq.getType())
                            .status(relatedFaq.getStatus())
                            .build())
                    .collect(Collectors.toList());
            response.setRelatedFaqs(simpleFaqs);
        }

        return response;
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
            //
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            faq.setCategoryUid(categoryResponse.getUid());
        }
        faq.setFileUid(fileUid);
        // faq.setKbUid(kbUid);
        faq.setOrgUid(orgUid);
        // 
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            faq.setKbaseEntity(kbase.get());
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
                        faq.setKbaseEntity(kbase.get());
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
                            .kbUid(kbUid)
                            .orgUid(orgUid)
                            .build();
                    // 保存FAQ到数据库
                    create(request);
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

}
