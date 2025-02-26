/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 21:12:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionRequest;
import com.bytedesk.core.action.ActionRestService;
import com.bytedesk.core.action.ActionTypeEnum;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FaqRestService extends BaseRestService<FaqEntity, FaqRequest, FaqResponse> {

    private final FaqRepository faqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryService;

    private final ActionRestService actionRestService;

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

    @Cacheable(value = "faq", key="#question", unless = "#result == null")
    public List<FaqEntity> findByQuestionContains(String question) {
        return faqRepository.findByQuestionContains(question);
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
            entity.setQuestion(request.getQuestion());
            entity.setAnswer(request.getAnswer());
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
        deleteByUid(entity.getUid());
    }

    private static final int MAX_RETRY_ATTEMPTS = 3; // 设定最大重试次数
    private static final long RETRY_DELAY_MS = 5000; // 设定重试间隔（毫秒）
    private final Queue<FaqEntity> retryQueue = new LinkedList<>();

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FaqEntity entity) {
        retryQueue.add(entity);
        processRetryQueue();
    }

    private void processRetryQueue() {
        while (!retryQueue.isEmpty()) {
            FaqEntity entity = retryQueue.poll(); // 从队列中取出一个元素
            if (entity == null) {
                break; // 队列为空，跳出循环
            }

            int retryCount = 0;
            while (retryCount < MAX_RETRY_ATTEMPTS) {
                try {
                    // 尝试更新Topic对象
                    faqRepository.save(entity);
                    // 更新成功，无需进一步处理
                    log.info("Optimistic locking succeeded for faq: {}", entity.getUid());
                    break; // 跳出内部循环
                } catch (ObjectOptimisticLockingFailureException ex) {
                    // 捕获乐观锁异常
                    log.error("Optimistic locking failure for faq: {}, retry count: {}", entity.getUid(),
                            retryCount + 1);
                    // 等待一段时间后重试
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted while waiting for retry", ie);
                        return;
                    }
                    retryCount++; // 增加重试次数

                    // 如果还有重试机会，则将faq放回队列末尾
                    if (retryCount < MAX_RETRY_ATTEMPTS) {
                        // FIXME: 发现会一直失败，暂时不重复处理
                        // retryQueue.add(faq);
                    } else {
                        // 所有重试都失败了
                        handleFailedRetries(entity);
                    }
                }
            }
        }
    }

    private void handleFailedRetries(FaqEntity faq) {
        String faqJSON = JSONObject.toJSONString(faq);
        ActionRequest actionRequest = ActionRequest.builder()
                .title("faq")
                .action("save")
                .description("All retry attempts failed for optimistic locking")
                .extra(faqJSON)
                .build();
        actionRequest.setType(ActionTypeEnum.FAILED.name());
        actionRestService.create(actionRequest);
        // bytedeskEventPublisher.publishActionEvent(actionRequest);
        log.error("All retry attempts failed for optimistic locking of faq: {}", faq.getUid());
        // 根据业务逻辑决定如何处理失败，例如通知用户稍后重试或执行其他操作
        // notifyUserOfFailure(robot);
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
        faq.setUid(uidUtils.getUid());
        faq.setQuestion(excel.getQuestion());
        faq.setAnswer(excel.getAnswer());
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

    public void saveFaqPairs(String qaPairs, String kbUid, String orgUid, String docId) {
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
                String tags = qa.getString("tags");
                
                if (StringUtils.hasText(question) && StringUtils.hasText(answer)) {
                    FaqEntity faq = FaqEntity.builder()
                        .question(question)
                        .answer(answer)
                        .type(MessageTypeEnum.TEXT.name())
                        .tags(tags)
                        .kbUid(kbUid)
                        .docId(docId)
                        .build();
                    
                    faq.setUid(uidUtils.getUid());
                    faq.setOrgUid(orgUid);
                    save(faq);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing and saving QA pairs: {} \nContent: {}", e.getMessage(), qaPairs);
            throw new RuntimeException("Failed to save QA pairs", e);
        }
    }

}
