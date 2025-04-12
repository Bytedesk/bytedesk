/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 12:51:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

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

    // private final QaJsonLoader qaJsonLoader;

    private final AuthService authService;

    @Override
    public Page<QaEntity> queryByOrgEntity(QaRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QaEntity> spec = QaSpecification.search(request);
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

    @Override
    public QaResponse queryByUid(QaRequest request) {
        Optional<QaEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            QaEntity entity = optionalEntity.get();
            entity.increaseClickCount();
            // 
            QaEntity savedEntity = qaRepository.save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update click count");
            }
            return convertToResponse(savedEntity);
        }
        return null;
    }

    @Cacheable(value = "qa", key = "#uid", unless = "#result == null")
    @Override
    public Optional<QaEntity> findByUid(String uid) {
        return qaRepository.findByUid(uid);
    }

    @Cacheable(value = "qa", key = "#question", unless = "#result == null")
    public List<QaEntity> findByQuestionContains(String question) {
        return qaRepository.findByQuestionContains(question);
    }

    public Boolean existsByUid(String uid) {
        return qaRepository.existsByUid(uid);
    }

    @Override
    @Transactional
    public QaResponse create(QaRequest request) {
        try {
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
            // 如何将string类型startDate和endDate转换为LocalDateTime类型？
            // if (StringUtils.hasText(request.getStartDate())) {
            //     entity.setStartDate(BdDateUtils.parseLocalDateTime(request.getStartDate()));
            // }
            // if (StringUtils.hasText(request.getEndDate())) {
            //     entity.setEndDate(BdDateUtils.parseLocalDateTime(request.getEndDate()));
            // }
            // entity.setType(MessageTypeEnum.fromValue(request.getType()).name());
            // 根据request.relatedQaUids查找关联的FAQ
            List<QaEntity> relatedQas = new ArrayList<>();
            for (String relatedQaUid : request.getRelatedQaUids()) {
                Optional<QaEntity> relatedQa = findByUid(relatedQaUid);
                if (relatedQa.isPresent()) {
                    relatedQas.add(relatedQa.get());
                } else {
                    throw new RuntimeException("relatedQaUid not found");
                }
            }
            entity.setRelatedQas(relatedQas);

            try {
                QaEntity savedEntity = save(entity);
                if (savedEntity != null) {
                    return convertToResponse(savedEntity);
                }
            } catch (Exception e) {
                // 如果保存时发生唯一键冲突，再次尝试查找
                if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    Optional<QaEntity> existingQa = findByUid(entity.getUid());
                    if (existingQa.isPresent()) {
                        return convertToResponse(existingQa.get());
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
    public QaResponse update(QaRequest request) {

        Optional<QaEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            QaEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setQuestion(request.getQuestion());
            entity.setAnswer(request.getAnswer());
            entity.setAnswerList(request.getAnswerList());
            entity.setStatus(request.getStatus());
            entity.setTagList(request.getTagList());
            entity.setType(request.getType());
            entity.setEnabled(request.getEnabled());
            // 如何将string类型startDate和endDate转换为LocalDateTime类型？
            // if (StringUtils.hasText(request.getStartDate())) {
            //     entity.setStartDate(BdDateUtils.parseLocalDateTime(request.getStartDate()));
            // }
            // if (StringUtils.hasText(request.getEndDate())) {
            //     entity.setEndDate(BdDateUtils.parseLocalDateTime(request.getEndDate()));
            // }
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            //
            entity.setCategoryUid(request.getCategoryUid());
            entity.setKbUid(request.getKbUid());
            //
            // 根据request.relatedQaUids查找关联的FAQ
            List<QaEntity> relatedQas = new ArrayList<>();
            for (String relatedQaUid : request.getRelatedQaUids()) {
                Optional<QaEntity> relatedQa = findByUid(relatedQaUid);
                if (relatedQa.isPresent()) {
                    relatedQas.add(relatedQa.get());
                } else {
                    throw new RuntimeException("relatedQaUid not found");
                }
            }
            entity.setRelatedQas(relatedQas);

            return convertToResponse(save(entity));
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
            QaEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update FAQ");
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
                throw new RuntimeException("Failed to rate up FAQ");
            }
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
                throw new RuntimeException("Failed to rate down FAQ");
            }
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("qa not found");
        }
    }

    @Override
    public QaEntity save(QaEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected QaEntity doSave(QaEntity entity) {
        return qaRepository.save(entity);
    }

    @Override
    public QaEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QaEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<QaEntity> latest = qaRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                QaEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return qaRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<QaEntity> entities) {
        qaRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QaEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(QaRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public QaResponse convertToResponse(QaEntity entity) {
        // return modelMapper.map(entity, QaResponse.class);
        QaResponse response = QaResponse.builder()
                .uid(entity.getUid())
                .question(entity.getQuestion())
                .answer(entity.getAnswer())
                .answerList(entity.getAnswerList())
                .isLlmQa(entity.isLlmQa())
                .type(entity.getType())
                .status(entity.getStatus())
                .viewCount(entity.getViewCount())
                .clickCount(entity.getClickCount())
                .upCount(entity.getUpCount())
                .downCount(entity.getDownCount())
                // .downShowTransferToAgentButton(entity.isDownShowTransferToAgentButton())
                .enabled(entity.isEnabled())
                .tagList(entity.getTagList())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .categoryUid(entity.getCategoryUid())
                .kbUid(entity.getKbUid())
                .fileUid(entity.getFileUid())
                .docUid(entity.getDocId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // 处理相关问题，避免循环依赖
        if (entity.getRelatedQas() != null) {
            List<QaResponse.SimpleQaResponse> simpleQas = entity.getRelatedQas().stream()
                    .map(relatedQa -> QaResponse.SimpleQaResponse.builder()
                            .uid(relatedQa.getUid())
                            .question(relatedQa.getQuestion())
                            .answer(relatedQa.getAnswer())
                            .type(relatedQa.getType())
                            .status(relatedQa.getStatus())
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

    public QaEntity convertExcelToQa(QaExcel excel, String uploadType, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Qa.class); // String categoryUid,
        QaEntity qa = QaEntity.builder().build();
        qa.setUid(uidUtils.getUid());
        qa.setQuestion(excel.getQuestion());
        qa.setAnswer(excel.getAnswer());
        qa.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        //
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            qa.setCategoryUid(categoryOptional.get().getUid());
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
            qa.setCategoryUid(categoryResponse.getUid());
        }
        qa.setKbUid(kbUid);
        qa.setOrgUid(orgUid);
        //
        return qa;
    }

    public void saveQaPairs(String qaPairs, String kbUid, String orgUid, String docId) {
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
                    QaEntity qaEntity = QaEntity.builder()
                            .question(question)
                            .answer(answer)
                            .type(MessageTypeEnum.TEXT.name())
                            .kbUid(kbUid)
                            .docId(docId)
                            .build();
                            qaEntity.setUid(uidUtils.getUid());
                            qaEntity.setOrgUid(orgUid);
                    save(qaEntity);
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
    // @Transactional
    // public void importQas(String orgUid, String kbUid) {
    //     if (qaRepository.count() > 0) {
    //         return;
    //     }

    //     try {
    //         // 加载JSON文件中的FAQ数据
    //         QaConfiguration config = qaJsonLoader.loadQas();

    //         // 遍历并保存每个FAQ
    //         for (Qa qa : config.getQas()) {
    //             String uid = Utils.formatUid(orgUid, qa.getUid());
    //             // 检查FAQ是否已存在
    //             if (!qaRepository.existsByUid(uid)) {
    //                 QaRequest request = QaRequest.builder()
    //                         .uid(uid)
    //                         .question(qa.getQuestion())
    //                         .answer(qa.getAnswer())
    //                         .type(MessageTypeEnum.TEXT.name())
    //                         .kbUid(kbUid)
    //                         .orgUid(orgUid)
    //                         .build();
    //                 // 保存FAQ到数据库
    //                 create(request);
    //             } else {
    //                 // log.info("FAQ already exists: {}", qa.getUid());
    //             }
    //         }
    //         // log.info("Successfully imported {} FAQs", count);
    //         // return count;
    //     } catch (Exception e) {
    //         log.error("Failed to import FAQs", e);
    //         throw new RuntimeException("Failed to import FAQs", e);
    //     }
    // }

    // @Transactional
    // public void initRelationQas(String orgUid, String kbUid) {
    //     try {
    //         // 加载JSON文件中的FAQ数据
    //         QaConfiguration config = qaJsonLoader.loadQas();
            
    //         // 创建5个示例多答案数据
    //         List<QaAnswer> answerList = new ArrayList<>();
    //         for (int i = 1; i <= 5; i++) {
    //             QaAnswer answer = new QaAnswer();
    //             answer.setVipLevel("" + i);
    //             answer.setAnswer("VIP " + i + " 专属回答：这是针对不同会员等级的答案示例");
    //             answerList.add(answer);
    //         }
            
    //         // 准备5个相关问题的UID列表
    //         List<String> relatedQaUids = new ArrayList<>();
    //         for (int i = 5; i < 10; i++) {
    //             String relatedUid = Utils.formatUid(orgUid, "qa_00" + i);
    //             relatedQaUids.add(relatedUid);
    //         }

    //         int count = 0;
    //         // 遍历并保存每个FAQ
    //         for (Qa qa : config.getQas()) {
    //             String uid = Utils.formatUid(orgUid, qa.getUid());
    //             // 构建FAQ请求
    //             QaRequest request = QaRequest.builder()
    //                     .uid(uid)
    //                     .question(qa.getQuestion())
    //                     .answer(qa.getAnswer())
    //                     .type(MessageTypeEnum.TEXT.name())
    //                     .kbUid(kbUid)
    //                     .orgUid(orgUid)
    //                     .build();
                
    //             // 为部分FAQ添加多答案和相关问题
    //             if (count < 5) {
    //                 request.setAnswerList(answerList);
    //                 request.setRelatedQaUids(relatedQaUids);
    //             }
                
    //             // 更新FAQ到数据库
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
