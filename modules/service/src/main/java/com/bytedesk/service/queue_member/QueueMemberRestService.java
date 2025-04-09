/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 09:24:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 16:15:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueMemberRestService extends BaseRestService<QueueMemberEntity, QueueMemberRequest, QueueMemberResponse> {

    private final QueueMemberRepository queueMemberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<QueueMemberResponse> queryByOrg(QueueMemberRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QueueMemberEntity> specification = QueueMemberSpecification.search(request);
        Page<QueueMemberEntity> page = queueMemberRepository.findAll(specification, pageable);
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

    // @Cacheable(value = "queue_member", key = "#queueTopic#queueDay#threadUid#status")
    // public Optional<QueueMemberEntity> findByQueueTopicAndQueueDayAndThreadUidAndStatus(String queueTopic, String queueDay, String threadUid, String status) {
    //     return queueMemberRepository.findByQueueTopicAndQueueDayAndThreadUidAndStatus(queueTopic, queueDay, threadUid, status);
    // }

    // 添加新的查询方法
    // public Optional<QueueMemberEntity> findByThreadUidAndQueueUid(String threadUid, String queueUid) {
    //     // 实现查询逻辑
    //     return queueMemberRepository.findByThreadUidAndQueueUid(threadUid, queueUid);
    // }

    @Override
    public QueueMemberResponse create(QueueMemberRequest request) {
        QueueMemberEntity counter = modelMapper.map(request, QueueMemberEntity.class);
        counter.setUid(uidUtils.getUid());
        //
        QueueMemberEntity savedQueueMember = save(counter);
        if (savedQueueMember == null) {
            throw new RuntimeException("save counter failed");
        }
        return convertToResponse(savedQueueMember);
    }

    @Override
    public QueueMemberResponse update(QueueMemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public QueueMemberEntity save(QueueMemberEntity entity) {
        try {
            return queueMemberRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(QueueMemberRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public void deleteAll() {
        queueMemberRepository.deleteAll();
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            QueueMemberEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
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

    public QueueMemberExcel convertToExcel(QueueMemberResponse response) {
        QueueMemberExcel excel = new QueueMemberExcel();
        
        // 基本信息
        if (response.getQueue() != null) {
            excel.setQueueNickname(response.getQueue().getNickname());
        }
        
        // 访客信息
        if (response.getThread() != null) {
            excel.setVisitorNickname(response.getThread().getUser().getNickname());
            excel.setAgentNickname(response.getThread().getAgentProtobuf().getNickname());
            excel.setWorkgroupName(response.getThread().getWorkgroup().getNickname());
            excel.setClient(response.getThread().getClient());
        }
        
        // 排队信息
        excel.setQueueNumber(response.getQueueNumber());
        // excel.setBeforeNumber(0); // 需要计算或者从别处获取
        excel.setWaitTime(response.getWaitTime());
        excel.setStatus(response.getThread() != null ? response.getThread().getStatus() : null);
        
        // 时间处理
        excel.setEnqueueTime(formatLocalDateTime(response.getVisitorEnqueueTime()));
        excel.setVisitorFirstMessageTime(formatLocalDateTime(response.getVisitorFirstMessageTime()));
        excel.setVisitorLastMessageTime(formatLocalDateTime(response.getVisitorLastMessageTime()));
        // excel.setLeaveTime(formatLocalDateTime(response.getVisitorLeaveTime()));
        
        // 人工客服相关
        excel.setAgentAcceptType(response.getAgentAcceptType());
        excel.setAgentAcceptTime(formatLocalDateTime(response.getAgentAcceptTime()));
        excel.setAgentFirstResponseTime(formatLocalDateTime(response.getAgentFirstResponseTime()));
        excel.setAgentLastResponseTime(formatLocalDateTime(response.getAgentLastResponseTime()));
        // excel.setCloseTime(formatLocalDateTime(response.getAgentCloseTime()));
        // excel.setAvgResponseTime(response.getAgentAvgResponseTime());
        // excel.setMaxResponseTime(response.getAgentMaxResponseTime());
        excel.setAgentMessageCount(response.getAgentMessageCount());
        // excel.setTimeout(booleanToString(response.getAgentTimeout()));
        // excel.setAgentOffline(booleanToString(response.getAgentOffline()));
        
        // 机器人相关
        excel.setRobotAcceptType(response.getRobotAcceptType());
        excel.setRobotAcceptTime(formatLocalDateTime(response.getRobotAcceptTime()));
        excel.setRobotFirstResponseTime(formatLocalDateTime(response.getRobotFirstResponseTime()));
        excel.setRobotLastResponseTime(formatLocalDateTime(response.getRobotLastResponseTime()));
        // excel.setRobotCloseTime(formatLocalDateTime(response.getRobotCloseTime()));
        // excel.setRobotAvgResponseTime(response.getRobotAvgResponseTime());
        // excel.setRobotMaxResponseTime(response.getRobotMaxResponseTime());
        // excel.setRobotMessageCount(response.getRobotMessageCount());
        // excel.setRobotTimeout(booleanToString(response.getRobotTimeout()));
        excel.setRobotToAgent(booleanToString(response.getRobotToAgent()));
        
        // 消息统计
        excel.setVisitorMessageCount(response.getVisitorMessageCount());
        // excel.setSystemMessageCount(response.getSystemMessageCount());
        
        // 评价与服务质量
        excel.setRated(booleanToString(response.getRated()));
        // excel.setRateLevel(response.getRateLevel());
        excel.setResolved(booleanToString(response.getResolved()));
        // excel.setQualityChecked(booleanToString(response.getQualityChecked()));
        // excel.setQualityCheckResult(response.getQualityCheckResult());
        
        // 留言与小结
        excel.setLeaveMsg(booleanToString(response.getLeaveMsg()));
        excel.setLeaveMsgAt(formatLocalDateTime(response.getLeaveMsgAt()));
        excel.setSummarized(booleanToString(response.getSummarized()));
        
        // 交互状态
        // excel.setTransferStatus(response.getTransferStatus());
        // excel.setInviteStatus(response.getInviteStatus());
        // excel.setIntentionType(response.getIntentionType());
        // excel.setEmotionType(response.getEmotionType());
        
        return excel;
    }
    
    /**
     * 将LocalDateTime转换为易于阅读的字符串格式
     */
    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString().replace("T", " ").split("\\.")[0];
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
