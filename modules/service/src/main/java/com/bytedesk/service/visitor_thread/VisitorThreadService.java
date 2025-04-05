/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 11:49:59
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
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettingsResponseVisitor;
import com.bytedesk.service.agent.AgentEntity;
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
        String user = ServiceConvertUtils.convertToUserProtobufJSONString(visitorRequest);
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
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitWorkgroupThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
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
        String agentString = ServiceConvertUtils.convertToUserProtobufJSONString(agent);
        // 访客信息
        String visitor = ServiceConvertUtils.convertToUserProtobufJSONString(visitorRequest);
        // 考虑到配置可能变化，更新配置
        String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(agent.getServiceSettings());
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.AGENT.name())
                .agent(agentString)
                .userUid(agent.getUid()) // 客服uid
                .owner(agent.getMember().getUser())
                .user(visitor)
                .extra(extra)
                .client(visitorRequest.getClient())
                .orgUid(agent.getOrgUid())
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
        String visitor = ServiceConvertUtils.convertToUserProtobufJSONString(visitorRequest);
        String extra = ServiceConvertUtils
                .convertToServiceSettingsResponseVisitorJSONString(robot.getServiceSettings());
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.ROBOT.name())
                .agent(robotString)
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
        thread.setAgent(robotString);
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
     * TODO: 座席端25分钟不回复则自动断开，推送满意度
     * TODO: 客户端2分钟没有回复坐席则自动推送1分钟计时提醒：
     * TODO: 频繁查库，待优化
     * 温馨提示：您已经有2分钟未有操作了，如再有1分钟未有操作，系统将自动结束本次对话，感谢您的支持与谅解
     * 如果1分钟之内无回复，则推送满意度：
     */
    @Async
    public void autoCloseThread(List<ThreadEntity> threads) {
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
            ServiceSettingsResponseVisitor settings = JSON.parseObject(thread.getExtra(),
                    ServiceSettingsResponseVisitor.class);
            Double autoCloseMinutes = settings.getAutoCloseMin();
            // log.info("autoCloseThread threadUid {} threadType {} autoCloseMinutes {},
            // diffInMinutes {}", thread.getUid(), thread.getType(), autoCloseMinutes,
            // diffInMinutes);
            
            // 添加空值检查，如果为null则使用默认值30分钟
            double autoCloseValue = (autoCloseMinutes != null) ? autoCloseMinutes : 30.0;
            
            if (diffInMinutes > autoCloseValue) {
                threadRestService.autoClose(thread);
            }
            // }
        });
    }

    @Override
    public VisitorThreadEntity save(VisitorThreadEntity entity) {
        try {
            return visitorThreadRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
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
    public void delete(VisitorThreadRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorThreadEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public VisitorThreadResponse convertToResponse(VisitorThreadEntity entity) {
        return modelMapper.map(entity, VisitorThreadResponse.class);
    }

}
