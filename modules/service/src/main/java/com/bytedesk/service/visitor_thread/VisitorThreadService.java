/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-20 06:32:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.kbase.service_settings.ServiceSettingsResponseVisitor;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VisitorThreadService extends BaseService<VisitorThread, VisitorThreadRequest, VisitorThreadResponse> {

    private final VisitorThreadRepository visitorThreadRepository;

    private final ThreadService threadService;

    private final ModelMapper modelMapper;

    @Override
    public Page<VisitorThreadResponse> queryByOrg(VisitorThreadRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<VisitorThread> spec = VisitorThreadSpecification.search(request);
        Page<VisitorThread> threads = visitorThreadRepository.findAll(spec, pageable);

        return threads.map(this::convertToResponse);
    }

    @Override
    public Page<VisitorThreadResponse> queryByUser(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "visitor_thread", key = "#uid", unless = "#result == null")
    @Override
    public Optional<VisitorThread> findByUid(String uid) {
        return visitorThreadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return visitorThreadRepository.existsByUid(uid);
    }

    @Cacheable(value = "visitor_thread", key = "#topic", unless = "#result == null")
    public Optional<VisitorThread> findByTopic(String topic) {
        return visitorThreadRepository.findByTopic(topic);
    }

    public VisitorThread create(Thread thread) {
        //
        VisitorThread visitorThread = modelMapper.map(thread, VisitorThread.class);
        //
        String visitorString = thread.getUser();
        UserProtobuf visitor = JSON.parseObject(visitorString, UserProtobuf.class);
        visitorThread.setVisitorUid(visitor.getUid());
        //
        VisitorThread savedThread = save(visitorThread);
        if (savedThread == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedThread;
    }

    public VisitorThread update(Thread thread) {
        Optional<VisitorThread> visitorThreadOpt = findByUid(thread.getUid());
        if (visitorThreadOpt.isPresent()) {
            VisitorThread visitorThread = visitorThreadOpt.get();
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
     * 
     * TODO: 座席端25分钟不回复则自动断开，推送满意度
     * TODO: 客户端2分钟没有回复坐席则自动推送1分钟计时提醒：
     * 温馨提示：您已经有2分钟未有操作了，如再有1分钟未有操作，系统将自动结束本次对话，感谢您的支持与谅解
     * 如果1分钟之内无回复，则推送满意度：
     */
    // TODO: 频繁查库，待优化
    @Async
    public void autoCloseThread() {
        List<Thread> threads = threadService.findStatusOpen();
        // log.info("autoCloseThread size {}", threads.size());
        threads.forEach(thread -> {
            // 计算两个日期之间的毫秒差
            long diffInMilliseconds = Math.abs(new Date().getTime() - thread.getUpdatedAt().getTime());
            // 转换为分钟
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
            // log.info("1.autoCloseThread threadUid {} threadType {} threadId {}
            // diffInMinutes {}", thread.getUid(), thread.getType(),
            // thread.getUid(), diffInMinutes);
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())
                    || thread.getType().equals(ThreadTypeEnum.AGENT.name())
                    || thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
                ServiceSettingsResponseVisitor settings = JSON.parseObject(thread.getExtra(),
                        ServiceSettingsResponseVisitor.class);
                Double autoCloseMinites = settings.getAutoCloseMin();
                // log.info("2. autoCloseThread threadUid {} threadType {} autoCloseMinites {},
                // diffInMinutes {}",
                // thread.getUid(), thread.getType(), autoCloseMinites, diffInMinutes);
                if (diffInMinutes > autoCloseMinites) {
                    threadService.autoClose(thread);
                }
            }
        });
    }

    @Override
    public VisitorThread save(VisitorThread entity) {
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
    public void delete(VisitorThread entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorThread entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public VisitorThreadResponse convertToResponse(VisitorThread entity) {
        return modelMapper.map(entity, VisitorThreadResponse.class);
    }

}
