/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 09:38:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.llm_faq.FaqJsonLoader.Faq;
import com.bytedesk.kbase.llm_faq.FaqJsonLoader.FaqConfiguration;
import com.bytedesk.kbase.llm_faq.event.FaqUpdateDocEvent;
import com.bytedesk.kbase.utils.KbaseConvertUtils;
import com.bytedesk.kbase.utils.KbaseMessageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FaqRestService extends BaseRestServiceWithExport<FaqEntity, FaqRequest, FaqResponse, FaqExcel> {

    private final FaqRepository faqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryRestService;

    private final FaqJsonLoader faqJsonLoader;

    private final KbaseRestService kbaseRestService;

    private final ThreadRestService threadRestService;

    private final MessageRestService messageRestService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected Specification<FaqEntity> createSpecification(FaqRequest request) {
        return FaqSpecification.search(request, authService);
    }

    @Override
    protected Page<FaqEntity> executePageQuery(Specification<FaqEntity> spec, Pageable pageable) {
        return faqRepository.findAll(spec, pageable);
    }
    @Transactional
    @Override
    public FaqResponse queryByUid(FaqRequest request) {
        Optional<FaqEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            FaqEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            //
            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update click count");
            }
            //
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    // 点击faq
    public FaqResponse clickFaq(FaqRequest request) {
        Optional<FaqEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            FaqEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            //
            FaqEntity savedEntity = save(entity);
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
                MessageEntity questionMessage = KbaseMessageUtils.getFaqQuestionMessage(faqResponse, threadEntity);
                MessageEntity savedQuestionMessage = messageRestService.save(questionMessage);
                if (savedQuestionMessage == null) {
                    throw new RuntimeException("Failed to insert question message");
                }
                faqResponse.setQuestionMessage(ConvertUtils.convertToMessageResponse(savedQuestionMessage));
                // 
                MessageEntity answerMessage = KbaseMessageUtils.getFaqAnswerMessage(faqResponse, threadEntity);
                MessageEntity savedAnswerMessage = messageRestService.save(answerMessage);
                if (savedAnswerMessage == null) {
                    throw new RuntimeException("Failed to insert answer message");
                }
                faqResponse.setAnswerMessage(ConvertUtils.convertToMessageResponse(savedAnswerMessage));
            }
            //
            return faqResponse;

        } else {
            throw new RuntimeException("faq not found");
        }
    }

    @Cacheable(value = "faq", key = "#uid", unless = "#result == null")
    @Override
    public Optional<FaqEntity> findByUid(String uid) {
        return faqRepository.findByUid(uid);
    }

    @Cacheable(value = "faq", key = "#kbUid", unless = "#result == null")
    public List<FaqEntity> findByKbUid(String kbUid) {
        return faqRepository.findByKbase_UidAndDeletedFalse(kbUid);
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
            // 判断question/answer/similarQuestions/answerList是否有变化，如果其中一个发生变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                FaqUpdateDocEvent qaUpdateDocEvent = new FaqUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(qaUpdateDocEvent);
            }
            // 设置属性
            entity.setQuestion(request.getQuestion());
            entity.setSimilarQuestions(request.getSimilarQuestions());
            entity.setAnswer(request.getAnswer());
            entity.setAnswerHtml(request.getAnswerHtml());
            entity.setAnswerMarkdown(request.getAnswerMarkdown());
            entity.setImages(request.getImages());
            entity.setAttachments(request.getAttachments());
            entity.setAnswerList(request.getAnswerList());
            entity.setElasticStatus(request.getElasticStatus());
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
            // 
            FaqEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update FAQ");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("faq not found");
        }
    }

    
    
    @CachePut(value = "faq", key = "#entity.uid")
    @Override
    protected FaqEntity doSave(FaqEntity entity) {
        return faqRepository.save(entity);
    }

    @Override
    public FaqEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            FaqEntity entity) {
        try {
            log.warn("处理乐观锁冲突: {}", entity.getUid());
            
            // 引入随机延迟，避免并发冲突
            try {
                Thread.sleep(100 + (long)(Math.random() * 400)); // 100-500ms随机延迟
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // 重新获取最新版本的实体
            Optional<FaqEntity> latest = faqRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FaqEntity latestEntity = latest.get();
                // 保持原有实体的部分属性
                latestEntity.setQuestion(entity.getQuestion());
                latestEntity.setAnswer(entity.getAnswer());
                latestEntity.setAnswerList(entity.getAnswerList());
                latestEntity.setType(entity.getType());
                latestEntity.setEnabled(entity.getEnabled());
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
                latestEntity.setElasticStatus(entity.getElasticStatus());

                // 尝试保存，可能会再次失败
                try {
                    return faqRepository.save(latestEntity);
                } catch (ObjectOptimisticLockingFailureException retryException) {
                    // 如果再次失败，记录日志并抛出异常
                    log.error("重试处理乐观锁冲突仍然失败: {}", entity.getUid());
                    throw retryException;
                }
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    public void save(List<FaqEntity> entities) {
        faqRepository.saveAll(entities);
    }

    @CacheEvict(value = "faq", key = "#uid")
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
            Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(faq.getCategoryUid());
            if (categoryOptional.isPresent()) {
                excel.setCategory(categoryOptional.get().getName());
            } else {
                excel.setCategory("未分类");
            }
        } else {
            excel.setCategory("未分类");
        }
        excel.setAnswerList(JSON.toJSONString(faq.getAnswerList()));
        if (faq.getEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }
        // 将状态和向量状态转换为中文
        excel.setStatus(ChunkStatusEnum.toChineseDisplay(faq.getElasticStatus()));
        excel.setVectorStatus(ChunkStatusEnum.toChineseDisplay(faq.getVectorStatus()));
        return excel;
    }

    public FaqEntity convertExcelToFaq(FaqExcel excel, String kbType, String fileUid, String kbUid, String orgUid) {
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
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            faq.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .type(kbType)
                    .kbUid(kbUid)
                    .orgUid(orgUid)
                    .build();
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
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
     * @param orgUid 组织ID
     * @param kbUid 知识库ID
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

    
    /**
     * 获取一个随机FAQ，用于测试
     * 
     * @return 随机FAQ的Optional包装
     */
    public Optional<FaqEntity> findRandomFaq() {
        try {
            // 获取系统中任意一个FAQ
            List<FaqEntity> randomFaqs = faqRepository.findRandomFaq(1);
            if (randomFaqs != null && !randomFaqs.isEmpty()) {
                return Optional.of(randomFaqs.get(0));
            }
        } catch (Exception e) {
            log.error("获取随机FAQ时出错: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }


    // generateSimilarQuestions
    // public List<String> generateSimilarQuestions(FaqRequest request) {
    //     List<String> similarQuestions = new ArrayList<>();
    //     // 使用OpenAI API生成相似问题
    // }

        // @Transactional
    // public void initRelationFaqs(String orgUid, String kbUid) {
    //     try {
    //         // 加载JSON文件中的FAQ数据
    //         FaqConfiguration config = faqJsonLoader.loadFaqs();
    //         // 创建5个示例多答案数据
    //         List<FaqAnswer> answerList = new ArrayList<>();
    //         for (int i = 1; i <= 5; i++) {
    //             FaqAnswer answer = new FaqAnswer();
    //             answer.setVipLevel("" + i);
    //             answer.setAnswer("VIP " + i + " 专属回答：这是针对不同会员等级的答案示例");
    //             answerList.add(answer);
    //         }
    //         // 准备5个相关问题的UID列表
    //         List<String> relatedFaqUids = new ArrayList<>();
    //         for (int i = 5; i < 10; i++) {
    //             String relatedUid = Utils.formatUid(orgUid, "faq_00" + i);
    //             relatedFaqUids.add(relatedUid);
    //         }
    //         int count = 0;
    //         // 遍历并保存每个FAQ
    //         for (Faq faq : config.getFaqs()) {
    //             String uid = Utils.formatUid(orgUid, faq.getUid());
    //             // 构建FAQ请求
    //             FaqRequest request = FaqRequest.builder()
    //                     .uid(uid)
    //                     .question(faq.getQuestion())
    //                     .answer(faq.getAnswer())
    //                     .type(MessageTypeEnum.TEXT.name())
    //                     .enabled(true)
    //                     .kbUid(kbUid)
    //                     .orgUid(orgUid)
    //                     .build();
    //             // 为部分FAQ添加多答案和相关问题
    //             if (count < 5) {
    //                 request.setAnswerList(answerList);
    //                 request.setRelatedFaqUids(relatedFaqUids);
    //             }
    //             update(request);
    //             count++;
    //         }
    //         log.info("Successfully updated {} FAQs with related questions and multiple answers", count);
    //     } catch (Exception e) {
    //         log.error("Failed to initialize FAQ relations: {}", e.getMessage(), e);
    //         throw new RuntimeException("Failed to initialize FAQ relations", e);
    //     }
    // }

}
