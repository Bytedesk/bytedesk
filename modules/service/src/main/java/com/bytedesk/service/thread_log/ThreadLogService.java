/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 10:47:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-12 11:54:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.kbase.service_settings.ServiceSettingsResponseVisitor;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class ThreadLogService {

    private final ThreadLogRepository threadLogRepository;

    private final ModelMapper modelMapper;

    private final ThreadService threadService;

    public Page<ThreadLogResponse> queryByOrg(ThreadLogRequest threadLogRequest) {

        Pageable pageable = PageRequest.of(threadLogRequest.getPageNumber(),
                threadLogRequest.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<ThreadLog> spec = ThreadLogSpecification.search(threadLogRequest);
        Page<ThreadLog> threadLogPage = threadLogRepository.findAll(spec, pageable);
        // Page<ThreadLog> threadLogPage =
        // threadLogRepository.findByOrgUid(threadLogRequest.getOrgUid(), pageable);

        return threadLogPage.map(this::convertThreadLogResponse);
    }

    public ThreadLog create(Thread thread) {

        if (threadLogRepository.existsByUid(thread.getUid())) {
            return null;
        }

        ThreadLog threadLog = modelMapper.map(thread, ThreadLog.class);

        return save(threadLog);
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
            if (thread.getType() == ThreadTypeEnum.WORKGROUP || thread.getType() == ThreadTypeEnum.AGENT) {
                ServiceSettingsResponseVisitor settings = JSON.parseObject(thread.getExtra(),
                        ServiceSettingsResponseVisitor.class);
                Double autoCloseMinites = settings.getAutoCloseMin();
                if (diffInMinutes > autoCloseMinites) {
                    threadService.autoClose(thread);
                }
            } else if (thread.getType() == ThreadTypeEnum.ROBOT) {
                ServiceSettingsResponseVisitor settings = JSON.parseObject(thread.getExtra(),
                        ServiceSettingsResponseVisitor.class);
                Double autoCloseMinites = settings.getAutoCloseMin();
                if (diffInMinutes > autoCloseMinites) {
                    threadService.autoClose(thread);
                }
            }
        });
    }

    public ThreadLog save(ThreadLog threadLog) {
        return threadLogRepository.save(threadLog);
    }

    public ThreadLogResponse convertThreadLogResponse(ThreadLog threadLog) {
        return modelMapper.map(threadLog, ThreadLogResponse.class);
    }

}
