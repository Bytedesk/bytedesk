/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-28 13:50:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettingsResponseVisitor;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorThreadService
        extends BaseRestService<VisitorThreadEntity, VisitorThreadRequest, VisitorThreadResponse> {

    private final VisitorThreadRepository visitorThreadRepository;

    private final ThreadRestService threadRestService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final QueueMemberRestService queueMemberRestService;

    @Override
    public Page<VisitorThreadResponse> queryByOrg(VisitorThreadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<VisitorThreadEntity> spec = VisitorThreadSpecification.search(request);
        Page<VisitorThreadEntity> threads = visitorThreadRepository.findAll(spec, pageable);
        return threads.map(this::convertToResponse);
    }

    @Override
    public Page<VisitorThreadResponse> queryByUser(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "visitor_thread", key = "#uid", unless = "#result == null")
    @Override
    public Optional<VisitorThreadEntity> findByUid(String uid) {
        return visitorThreadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return visitorThreadRepository.existsByUid(uid);
    }

    @Cacheable(value = "visitor_thread", key = "#topic", unless = "#result == null")
    public Optional<VisitorThreadEntity> findFirstByTopic(String topic) {
        return visitorThreadRepository.findFirstByTopic(topic);
    }

    public ThreadEntity createWorkgroupThread(VisitorRequest visitorRequest, WorkgroupEntity workgroup, String topic) {
        //
        String user = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        String workgroupString = ServiceConvertUtils.convertToUserProtobufJSONString(workgroup);
        String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(workgroup.getServiceSettings());
        if (visitorRequest.isWeChat()) {
            extra = visitorRequest.getThreadExtra();
        }
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.WORKGROUP.name())
                .user(user)
                .workgroup(workgroupString)
                .extra(extra)
                .client(visitorRequest.getClient())
                .orgUid(workgroup.getOrgUid())
                .build();
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitWorkgroupThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread, WorkgroupEntity workgroup) {
        //
        if (visitorRequest.isWeChat()) {
            thread.setExtra(visitorRequest.getThreadExtra());
        } else {
            String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(workgroup.getServiceSettings());
            thread.setExtra(extra);
        }
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity createAgentThread(VisitorRequest visitorRequest, AgentEntity agent, String topic) {
        //
        // 考虑到客服信息发生变化，更新客服信息
        // String agentString = ServiceConvertUtils.convertToUserProtobufJSONString(agent);
        UserProtobuf agentProtobuf = agent.toUserProtobuf();
        // 访客信息
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        // 考虑到配置可能变化，更新配置
        String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(agent.getServiceSettings());
        // 
        String orgUid = agent.getOrgUid();
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.AGENT.name())
                .agent(agentProtobuf.toJson())
                .userUid(agent.getUid()) // 客服uid
                .owner(agent.getMember().getUser())
                .user(visitor)
                .extra(extra)
                .client(visitorRequest.getClient())
                .orgUid(orgUid)
                .build();
        // 
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitAgentThreadExtra(ThreadEntity thread, AgentEntity agent) {
        // 考虑到配置可能变化，更新配置
        String extra = ServiceConvertUtils
                .convertToServiceSettingsResponseVisitorJSONString(agent.getServiceSettings());
        thread.setExtra(extra);
        // 考虑到客服信息发生变化，更新客服信息
        String agentString = ServiceConvertUtils.convertToUserProtobufJSONString(agent);
        thread.setAgent(agentString);
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity createRobotThread(VisitorRequest visitorRequest, RobotEntity robot, String topic) {
        //
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robot);
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        String extra = ServiceConvertUtils
                .convertToServiceSettingsResponseVisitorJSONString(robot.getServiceSettings());
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.ROBOT.name())
                // .agent(robotString) // 人工客服
                .robot(robotString) // 机器人
                .userUid(robot.getUid()) // 机器人uid
                .user(visitor)
                .client(visitorRequest.getClient())
                .orgUid(robot.getOrgUid())
                .extra(extra)
                .build();
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitRobotThreadExtra(ThreadEntity thread, RobotEntity robot) {
        //
        String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(robot.getServiceSettings());
        thread.setExtra(extra);
        // 使用agent的serviceSettings配置
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robot);
        // thread.setAgent(robotString); // 人工客服
        thread.setRobot(robotString); // 机器人
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        //
        return savedEntity;
    }

    public VisitorThreadEntity update(ThreadEntity thread) {
        Optional<VisitorThreadEntity> visitorThreadOpt = findByUid(thread.getUid());
        if (visitorThreadOpt.isPresent()) {
            VisitorThreadEntity visitorThread = visitorThreadOpt.get();
            visitorThread.setStatus(thread.getStatus());
            //
            return save(visitorThread);
        }
        return null;
    }

    @Override
    public VisitorThreadResponse create(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public VisitorThreadResponse update(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * TODO: 频繁查库，待优化
     * 1. 超时关闭会话
     * 2. 超时未回复会话，发送会话超时提醒
     */
    @Async
    public void autoRemindAgentOrCloseThread(List<ThreadEntity> threads) {
        // List<ThreadEntity> threads = threadRestService.findStateOpen();
        // log.info("autoCloseThread size {}", threads.size());
        threads.forEach(thread -> {
            // 计算两个日期之间的毫秒差
            // long diffInMilliseconds = Math.abs(new Date().getTime() -
            // thread.getUpdatedAt().getTime());
            // LocalDateTime转为时间戳需借助ZoneId和系统默认时区
            long currentTimeMillis = System.currentTimeMillis();
            long updatedAtMillis = thread.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long diffInMilliseconds = Math.abs(currentTimeMillis - updatedAtMillis);
            // 转换为分钟
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
            // log.info("before autoCloseThread threadUid {} threadType {} threadId {}
            // diffInMinutes {}", thread.getUid(), thread.getType(), thread.getUid(),
            // diffInMinutes);
            // if (thread.isCustomerService()) {
            ServiceSettingsResponseVisitor settings = JSON.parseObject(thread.getExtra(), ServiceSettingsResponseVisitor.class);
            Double autoCloseMinutes = settings.getAutoCloseMin();
            // log.info("autoCloseThread threadUid {} threadType {} autoCloseMinutes {},
            // diffInMinutes {}", thread.getUid(), thread.getType(), autoCloseMinutes,
            // diffInMinutes);
            
            // 添加空值检查，如果为null则使用默认值30分钟
            double autoCloseValue = (autoCloseMinutes != null) ? autoCloseMinutes : 30.0;
            if (diffInMinutes > autoCloseValue) {
                threadRestService.autoClose(thread);
            }

            // TODO: 查询超时未回复会话, 发送会话超时提醒
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isPresent()) {
                
            }


        });
    }

    @Override
    public VisitorThreadEntity save(VisitorThreadEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected VisitorThreadEntity doSave(VisitorThreadEntity entity) {
        return visitorThreadRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(VisitorThreadRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public VisitorThreadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorThreadEntity entity) {
        try {
            Optional<VisitorThreadEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                VisitorThreadEntity latestEntity = latest.get();
                // 合并关键信息
                latestEntity.setStatus(entity.getStatus());
                return visitorThreadRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突", ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public VisitorThreadResponse convertToResponse(VisitorThreadEntity entity) {
        return modelMapper.map(entity, VisitorThreadResponse.class);
    }

}
