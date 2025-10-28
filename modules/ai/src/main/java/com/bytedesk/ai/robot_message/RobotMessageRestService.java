/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:05:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-24 17:54:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Description;

import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.MessageTypeConverter;
import com.bytedesk.core.message.content.RobotContent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Description("Robot Message Service - AI robot message and conversation management service")
public class RobotMessageRestService extends
        BaseRestServiceWithExport<RobotMessageEntity, RobotMessageRequest, RobotMessageResponse, RobotMessageExcel> {

    private final RobotMessageRepository robotMessageRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<RobotMessageEntity> createSpecification(RobotMessageRequest request) {
        return RobotMessageSpecification.search(request, authService);
    }

    @Override
    protected Page<RobotMessageEntity> executePageQuery(Specification<RobotMessageEntity> spec, Pageable pageable) {
        return robotMessageRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "robotMessage", key = "#uid", unless = "#result==null")
    @Override
    public Optional<RobotMessageEntity> findByUid(String uid) {
        return robotMessageRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return robotMessageRepository.existsByUid(uid);
    }

    @Override
    public RobotMessageResponse create(RobotMessageRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            // 已经存在，则更新
            return update(request);
        }
        //
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        //
        RobotMessageEntity entity = modelMapper.map(request, RobotMessageEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        //
        RobotMessageEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create robotMessage failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public RobotMessageResponse update(RobotMessageRequest request) {
        Optional<RobotMessageEntity> optional = robotMessageRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            RobotMessageEntity entity = optional.get();
            // modelMapper.map(request, entity);
            // 对于Stream消息：拼接起来，而不是覆盖
            // ROBOT_STREAM：按字段合并 JSON 内容（参考 MessagePersistService）
            try {

                String existingJson = entity.getAnswer();
                String incomingJson = request.getAnswer();

                RobotContent existing = null;
                RobotContent incoming = null;
                try {
                    if (existingJson != null && !existingJson.isEmpty()) {
                        existing = RobotContent.fromJson(existingJson, RobotContent.class);
                    }
                } catch (Exception ignore) {
                    // 旧数据或非JSON，忽略
                }
                try {
                    if (incomingJson != null && !incomingJson.isEmpty()) {
                        incoming = RobotContent.fromJson(incomingJson, RobotContent.class);
                    }
                } catch (Exception ignore) {
                }

                if (existing == null || incoming == null) {
                    // 兜底：无法解析则不丢数据，直接字符串拼接
                    entity.setAnswer((existingJson == null ? "" : existingJson)
                            + (incomingJson == null ? "" : incomingJson));
                } else {
                    String mergedAnswer = concatSafe(existing.getAnswer(), incoming.getAnswer());
                    String mergedReason = concatSafe(existing.getReasonContent(), incoming.getReasonContent());

                    RobotContent merged = RobotContent.builder()
                            .question(existing.getQuestion() != null ? existing.getQuestion()
                                    : incoming.getQuestion())
                            .answer(mergedAnswer)
                            .reasonContent(mergedReason)
                            .sources(existing.getSources() != null ? existing.getSources()
                                    : incoming.getSources())
                            .regenerationContext(existing.getRegenerationContext() != null
                                    ? existing.getRegenerationContext()
                                    : incoming.getRegenerationContext())
                            .kbUid(existing.getKbUid() != null ? existing.getKbUid() : incoming.getKbUid())
                            .robotUid(existing.getRobotUid() != null ? existing.getRobotUid()
                                    : incoming.getRobotUid())
                            .build();
                    entity.setAnswer(merged.toJson());
                }

            } catch (Exception ex) {
                log.warn("ROBOT_STREAM 合并内容失败，回退为原始拼接: {}", ex.getMessage());
                try {
                    entity.setAnswer((entity.getAnswer() == null ? "" : entity.getAnswer())
                            + (request.getAnswer() == null ? "" : request.getAnswer()));
                } catch (Exception ignore) {
                }
            }
            //
            entity.setPromptTokens(request.getPromptTokens());
            entity.setCompletionTokens(request.getCompletionTokens());
            entity.setTotalTokens(request.getTotalTokens());
            //
            entity.setPrompt(request.getPrompt());
            entity.setAiProvider(request.getAiProvider());
            entity.setAiModel(request.getAiModel());
            // log.debug("Updating robot message {}: current answer length: {}, new answer
            // length: {}",
            // request.getUid(), currentAnswer.length(), newAnswer.length());

            //
            RobotMessageEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update robotMessage failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("RobotMessage not found");
        }
    }

    @Override
    protected RobotMessageEntity doSave(RobotMessageEntity entity) {
        return robotMessageRepository.save(entity);
    }

    @Override
    public RobotMessageEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            RobotMessageEntity entity) {
        try {
            Optional<RobotMessageEntity> latest = robotMessageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RobotMessageEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 对于Stream消息：拼接answer字段，而不是覆盖
                if (entity.getAnswer() != null && !entity.getAnswer().isEmpty()) {
                    String newAnswer = latestEntity.getAnswer() != null ? latestEntity.getAnswer() + entity.getAnswer()
                            : entity.getAnswer();
                    latestEntity.setAnswer(newAnswer);
                }

                // 保留其他重要字段
                if (entity.getPromptTokens() != null) {
                    latestEntity.setPromptTokens(entity.getPromptTokens());
                }
                if (entity.getCompletionTokens() != null) {
                    latestEntity.setCompletionTokens(entity.getCompletionTokens());
                }
                if (entity.getTotalTokens() != null) {
                    latestEntity.setTotalTokens(entity.getTotalTokens());
                }
                if (entity.getStatus() != null) {
                    latestEntity.setStatus(entity.getStatus());
                }

                return robotMessageRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<RobotMessageEntity> optional = robotMessageRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // robotMessageRepository.delete(optional.get());
        } else {
            throw new RuntimeException("RobotMessage not found");
        }
    }

    @Override
    public void delete(RobotMessageRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RobotMessageResponse convertToResponse(RobotMessageEntity entity) {
        return ConvertAiUtils.convertToRobotMessageResponse(entity);
    }

    @Override
    public RobotMessageExcel convertToExcel(RobotMessageEntity entity) {
        RobotMessageExcel excel = modelMapper.map(entity, RobotMessageExcel.class);
        excel.setType(MessageTypeConverter.convertToChineseType(entity.getType()));
        excel.setContent(entity.getContent());
        excel.setUser(entity.getUserProtobuf().getNickname());
        excel.setRobot(entity.getRobotProtobuf().getNickname());
        excel.setCreatedAt(BdDateUtils.formatDatetimeToString(entity.getCreatedAt()));
        return excel;
    }

    private String concatSafe(String a, String b) {
        if (a == null || a.isEmpty())
            return (b == null ? "" : b);
        if (b == null || b.isEmpty())
            return a;
        return a + b;
    }
}
