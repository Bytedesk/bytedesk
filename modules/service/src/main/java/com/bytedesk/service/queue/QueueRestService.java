/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 17:49:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Description;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.ThreadProcessStatusEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Description("Queue Management Service - Customer service queue and routing management service")
public class QueueRestService extends BaseRestServiceWithExcel<QueueEntity, QueueRequest, QueueResponse, QueueExcel> {

    private final QueueRepository queueRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final ThreadRestService threadRestService;

    private final AgentRestService agentRestService;

    private final WorkgroupRepository workgroupRepository;

    @Override
    public Page<QueueEntity> queryByOrgEntity(QueueRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QueueEntity> specification = QueueSpecification.search(request);
        return queueRepository.findAll(specification, pageable);
    }

    @Override
    public Page<QueueResponse> queryByOrg(QueueRequest request) {
        Page<QueueEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QueueResponse> queryByUser(QueueRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        // set user uid
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    public Page<ThreadResponse> queryQueuing(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }        
        // 设置查询条件：状态为排队中
        request.setStatus(ThreadProcessStatusEnum.QUEUING.name());
        
        // 通过user.uid查询相应的agent
        Optional<AgentEntity> agentOptional = agentRestService.findByUserUid(user.getUid());
        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            // 将agent.uid添加到topicList中
            request.getTopicList().add(agent.getUid());
            
            // 通过agent.uid查询相应的workgroups
            List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agent.getUid());
            for (WorkgroupEntity workgroup : workgroups) {
                // 将workgroup.uid添加到topicList中
                request.getTopicList().add(workgroup.getUid());
            }
        }
        log.debug("查询排队中会话，主题列表: {}", request.getTopicList());
        
        // 使用 threadRestService 查询排队中的会话
        return threadRestService.queryByOrg(request);
    }

    @Cacheable(value = "queue", key = "#uid", unless = "#result==null")
    @Override
    public Optional<QueueEntity> findByUid(String uid) {
        return queueRepository.findByUid(uid);
    }

    @Cacheable(value = "queue", key = "#topic+#day", unless = "#result==null")
    public Optional<QueueEntity> findByTopicAndDay(String topic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(topic, day);
    }

    public QueueEntity createQueue(QueueRequest request) {
        QueueEntity queue = modelMapper.map(request, QueueEntity.class);
        queue.setUid(uidUtils.getUid());
        try {
            return queueRepository.save(queue);
        } catch (DataIntegrityViolationException e) {
            // 如果在保存时发生唯一约束冲突，说明其他线程已创建
            // 重新查询获取已存在的记录
            return queueRepository.findByTopicAndDayAndDeletedFalse(request.getTopic(), request.getDay())
                    .orElseThrow(() -> new RuntimeException("Queue creation failed"));
        }
    }

    @Override
    public QueueResponse create(QueueRequest request) {
        QueueEntity entity = modelMapper.map(request, QueueEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        QueueEntity savedQueue = save(entity);
        if (savedQueue == null) {
            throw new RuntimeException("Create queue failed");
        }
        return convertToResponse(savedQueue);
    }

    @Override
    public QueueResponse update(QueueRequest request) {
        Optional<QueueEntity> queueOptional = findByUid(request.getUid());
        if (queueOptional.isPresent()) {
            QueueEntity entity = queueOptional.get();
            // modelMapper.map(request, entity);
            entity.setUid(request.getUid());
            //
            return convertToResponse(save(entity));
        }
        return create(request);
    }

    @CachePut(value = "queue", key = "#entity.uid")
    @Override
    protected QueueEntity doSave(QueueEntity entity) {
        return queueRepository.save(entity);
    }

    @CacheEvict(value = "queue", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<QueueEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            // delete(optional.get());
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(QueueRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public QueueEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QueueEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<QueueEntity> latest = queueRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                QueueEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return queueRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public QueueResponse convertToResponse(QueueEntity entity) {
        return ServiceConvertUtils.convertToQueueResponse(entity);
    }

    public Optional<QueueEntity> findLatestByQueueTopicAndDay(String queueTopic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(queueTopic, day);
    }
    
    @Override
    public QueueExcel convertToExcel(QueueEntity entity) {
        QueueExcel queueExcel = modelMapper.map(entity, QueueExcel.class);
        queueExcel.setUid(entity.getUid());
        
        // 使用ThreadTypeEnum的静态方法将类型转换为中文名称
        String typeStr = entity.getType();
        queueExcel.setType(ThreadTypeEnum.getChineseNameByString(typeStr));
        
        // 使用QueueStatusEnum的静态方法将状态转换为中文名称
        // String statusStr = entity.getStatus();
        // queueExcel.setStatus(QueueStatusEnum.getChineseNameByString(statusStr));
        
        return queueExcel;
    }
}
