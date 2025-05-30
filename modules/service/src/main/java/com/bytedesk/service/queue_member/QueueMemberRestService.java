/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 09:24:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 10:52:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

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

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueMemberRestService extends BaseRestServiceWithExcel<QueueMemberEntity, QueueMemberRequest, QueueMemberResponse, QueueMemberExcel> {

    private final QueueMemberRepository queueMemberRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final AuthService authService;

    @Override
    public Page<QueueMemberEntity> queryByOrgEntity(QueueMemberRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QueueMemberEntity> specification = QueueMemberSpecification.search(request);
        return queueMemberRepository.findAll(specification, pageable);
    }

    @Override
    public Page<QueueMemberResponse> queryByOrg(QueueMemberRequest request) {
        Page<QueueMemberEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QueueMemberResponse> queryByUser(QueueMemberRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user is null");
        }
        // set user uid
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "counter", key = "#uid")
    @Override
    public Optional<QueueMemberEntity> findByUid(String uid) {
        return queueMemberRepository.findByUid(uid);
    }

    @Cacheable(value = "queue_member", key = "#threadUid", unless = "#result == null")
    public Optional<QueueMemberEntity> findByThreadUid(String threadUid) {
        return queueMemberRepository.findByThreadUid(threadUid);
    }

    @CachePut(value = "queue_member", key = "#entity.uid")
    @Override
    protected QueueMemberEntity doSave(QueueMemberEntity entity) {
        return queueMemberRepository.save(entity);
    }

    @Override
    public QueueMemberEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            QueueMemberEntity entity) {
        log.error("处理乐观锁异常: {}", e.getMessage());
        
        try {
            // 获取最新的实体
            Optional<QueueMemberEntity> latestEntityOpt = queueMemberRepository.findByUid(entity.getUid());
            if (latestEntityOpt.isPresent()) {
                QueueMemberEntity latestEntity = latestEntityOpt.get();
                
                // 根据业务需求合并变更
                // 保留当前实体中的重要计数值
                if (entity.getVisitorMessageCount() > latestEntity.getVisitorMessageCount()) {
                    latestEntity.setVisitorMessageCount(entity.getVisitorMessageCount());
                }
                
                if (entity.getAgentMessageCount() > latestEntity.getAgentMessageCount()) {
                    latestEntity.setAgentMessageCount(entity.getAgentMessageCount());
                }
                
                if (entity.getRobotMessageCount() > latestEntity.getRobotMessageCount()) {
                    latestEntity.setRobotMessageCount(entity.getRobotMessageCount());
                }
                
                if (entity.getSystemMessageCount() > latestEntity.getSystemMessageCount()) {
                    latestEntity.setSystemMessageCount(entity.getSystemMessageCount());
                }
                
                // 保持最新的时间戳
                if (entity.getVisitorLastMessageAt() != null && 
                    (latestEntity.getVisitorLastMessageAt() == null || 
                     entity.getVisitorLastMessageAt().isAfter(latestEntity.getVisitorLastMessageAt()))) {
                    latestEntity.setVisitorLastMessageAt(entity.getVisitorLastMessageAt());
                }
                
                if (entity.getAgentLastResponseAt() != null && 
                    (latestEntity.getAgentLastResponseAt() == null || 
                     entity.getAgentLastResponseAt().isAfter(latestEntity.getAgentLastResponseAt()))) {
                    latestEntity.setAgentLastResponseAt(entity.getAgentLastResponseAt());
                }
                
                // 保存合并后的实体
                return queueMemberRepository.save(latestEntity);
            } else {
                log.warn("无法找到UID为{}的QueueMember实体，将使用原始实体副本", entity.getUid());
                return null;
            }
        } catch (Exception ex) {
            log.error("在处理乐观锁异常时发生错误: {}", ex.getMessage(), ex);
            // 抛出运行时异常以确保事务一致性，避免静默回滚
            throw new RuntimeException("处理乐观锁异常失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public QueueMemberResponse create(QueueMemberRequest request) {
        QueueMemberEntity counter = modelMapper.map(request, QueueMemberEntity.class);
        counter.setUid(uidUtils.getUid());
        
        QueueMemberEntity savedQueueMember = save(counter);
        if (savedQueueMember == null) {
            throw new RuntimeException("save counter failed");
        }
        return convertToResponse(savedQueueMember);
    }

    @Override
    public QueueMemberResponse update(QueueMemberRequest request) {
        Optional<QueueMemberEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("queue member not found");
        }
        QueueMemberEntity entity = optional.get();
        // 更新实体属性
        // entity.setQueueNumber(request.getQueueNumber());

        QueueMemberEntity savedCounter = save(entity);
        if (savedCounter == null) {
            throw new RuntimeException("save counter failed");
        }
        return convertToResponse(savedCounter);
    }

    @CacheEvict(value = "queue_member", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<QueueMemberEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("queue member not found");
        }
        QueueMemberEntity entity = optional.get();
        entity.setDeleted(true);
        save(entity);
    }

    @Override
    public void delete(QueueMemberRequest entity) {
        deleteByUid(entity.getUid());
    }

    public void deleteAll() {
        queueMemberRepository.deleteAll();
    }

    @Override
    public QueueMemberResponse convertToResponse(QueueMemberEntity entity) {
        QueueMemberResponse response = modelMapper.map(entity, QueueMemberResponse.class);
        response.setThread(ConvertUtils.convertToThreadResponse(entity.getThread()));
        if (entity.getThread() != null && entity.getThread().getType() != null) {
            // 处理不同类型的队列
            if (entity.getThread().getType().equals(ThreadTypeEnum.AGENT.name())) {
                response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getAgentQueue()));
            } else if (entity.getThread().getType().equals(ThreadTypeEnum.ROBOT.name())) {
                response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getRobotQueue()));
            } else if (entity.getThread().getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getWorkgroupQueue()));
            }
        }
        return response;
    }

    @Override
    public QueueMemberExcel convertToExcel(QueueMemberEntity entity) {
        QueueMemberResponse response = convertToResponse(entity);
        // QueueMemberExcel excel = new QueueMemberExcel();
        QueueMemberExcel excel = modelMapper.map(response, QueueMemberExcel.class);
        // 基本信息
        if (response.getQueue() != null) {
            excel.setQueueNickname(response.getQueue().getNickname());
        }
        
        // 访客信息
        if (response.getThread() != null) {
            excel.setVisitorNickname(response.getThread().getUser().getNickname());
            excel.setAgentNickname(response.getThread().getAgentProtobuf().getNickname());
            excel.setRobotNickname(response.getThread().getRobotProtobuf().getNickname());
            // excel.setWorkgroupName(response.getThread().getWorkgroup().getNickname());
            excel.setStatus(ThreadProcessStatusEnum.fromValue(response.getThread().getStatus()).toChineseDisplay());
            excel.setClient(ClientEnum.fromValue(response.getThread().getClient()).toChineseDisplay());
        }
        
        // 人工客服相关
        if (response.getAgentAcceptType() != null) {
            excel.setAgentAcceptType(QueueMemberAcceptTypeEnum.fromValue(response.getAgentAcceptType()).toChineseDisplay());
        } else {
            excel.setAgentAcceptType(null);
        }
        excel.setAgentOffline(booleanToString(response.getAgentOffline()));
        
        // 机器人相关
        if (response.getRobotAcceptType() != null) {
            excel.setRobotAcceptType(QueueMemberAcceptTypeEnum.fromValue(response.getRobotAcceptType()).toChineseDisplay());
        } else {
            excel.setRobotAcceptType(null);
        }
        excel.setRobotToAgent(booleanToString(response.getRobotToAgent()));
        
        // 消息统计
        // excel.setVisitorMessageCount(response.getVisitorMessageCount());
        // excel.setSystemMessageCount(response.getSystemMessageCount());
        
        // 评价与服务质量
        excel.setRated(booleanToString(response.getRated()));
        // excel.setRateLevel(response.getRateLevel());
        excel.setResolved(booleanToString(response.getResolved()));
        // excel.setQualityChecked(booleanToString(response.getQualityChecked()));
        // excel.setQualityCheckResult(response.getQualityCheckResult());
        
        // 留言与小结
        excel.setMessageLeave(booleanToString(response.getMessageLeave()));
        // excel.setLeaveMsg(booleanToString(response.getLeaveMsg()));
        // excel.setLeaveMsgAt(formatLocalDateTime(response.getLeaveMsgAt()));
        excel.setSummarized(booleanToString(response.getSummarized()));
        
        // 交互状态
        // excel.setTransferStatus(response.getTransferStatus());
        // excel.setInviteStatus(response.getInviteStatus());
        // excel.setIntentionType(response.getIntentionType());
        // excel.setEmotionType(response.getEmotionType());
        
        return excel;
    }

    /**
     * 将Boolean转换为"是"或"否"
     */
    private String booleanToString(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? "是" : "否";
    }
}
