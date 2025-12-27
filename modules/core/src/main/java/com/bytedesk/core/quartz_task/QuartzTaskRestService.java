/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz_task;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.quartz.QuartzConsts;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QuartzTaskRestService extends BaseRestServiceWithExport<QuartzTaskEntity, QuartzTaskRequest, QuartzTaskResponse, QuartzTaskExcel> {

    private final QuartzTaskRepository quartz_taskRepository;

    private final Scheduler scheduler;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<QuartzTaskEntity> createSpecification(QuartzTaskRequest request) {
        return QuartzTaskSpecification.search(request, authService);
    }

    @Override
    protected Page<QuartzTaskEntity> executePageQuery(Specification<QuartzTaskEntity> spec, Pageable pageable) {
        return quartz_taskRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "quartz_task", key = "#uid", unless="#result==null")
    @Override
    public Optional<QuartzTaskEntity> findByUid(String uid) {
        return quartz_taskRepository.findByUid(uid);
    }

    @Cacheable(value = "quartz_task", key = "#jobName", unless="#result==null")
    public Optional<QuartzTaskEntity> findByJobName(String jobName) {
        return quartz_taskRepository.findByJobName(jobName);
    }

    public Boolean existsByJobName(String jobName) {
        return quartz_taskRepository.existsByJobName(jobName);
    }

    @Transactional
    @Override
    public QuartzTaskResponse create(QuartzTaskRequest request) {
        if (StringUtils.hasText(request.getJobName()) && existsByJobName(request.getJobName())) {
            // jobName 唯一
            return convertToResponse(findByJobName(request.getJobName()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        QuartzTaskEntity entity = modelMapper.map(request, QuartzTaskEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getCacheSerialUid());
        }
        // 
        QuartzTaskEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create quartz_task failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public QuartzTaskResponse update(QuartzTaskRequest request) {
        Optional<QuartzTaskEntity> optional = quartz_taskRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            QuartzTaskEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            QuartzTaskEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update quartz_task failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("QuartzTask not found");
        }
    }

    @Override
    protected QuartzTaskEntity doSave(QuartzTaskEntity entity) {
        return quartz_taskRepository.save(entity);
    }

    @Override
    public QuartzTaskEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QuartzTaskEntity entity) {
        try {
            Optional<QuartzTaskEntity> latest = quartz_taskRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                QuartzTaskEntity latestEntity = latest.get();
                // 合并需要保留的数据（调度任务以请求体为准）
                modelMapper.map(entity, latestEntity);
                return quartz_taskRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<QuartzTaskEntity> optional = quartz_taskRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // quartz_taskRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("QuartzTask not found");
        }
    }

    @Override
    public void delete(QuartzTaskRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public QuartzTaskResponse convertToResponse(QuartzTaskEntity entity) {
        return modelMapper.map(entity, QuartzTaskResponse.class);
    }

    @Override
    public QuartzTaskExcel convertToExcel(QuartzTaskEntity entity) {
        return modelMapper.map(entity, QuartzTaskExcel.class);
    }

    public void startJob(QuartzTaskRequest request) {
        log.info("startJob: {}", request);
        update(request);

        try {
            JobKey jobKey = new JobKey(request.getJobName(), request.getJobGroup());
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            @SuppressWarnings("unchecked")
            Class<? extends Job> cls = (Class<? extends Job>) Class.forName(request.getJobClassName());

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(QuartzConsts.JOB_METHOD_NAME, request.getJobMethodName());

            JobDetail jobDetail = JobBuilder.newJob(cls)
                    .withIdentity(jobKey)
                    .withDescription(request.getDescription())
                    .setJobData(jobDataMap)
                    .build();

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(request.getCronExpression());

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(request.getTriggerName(), request.getTriggerGroup())
                    .withSchedule(cronScheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | ClassNotFoundException e) {
            log.error("startJob failed", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void pauseJob(QuartzTaskRequest request) {
        log.info("pauseJob: {}", request);
        update(request);
        try {
            JobKey jobKey = JobKey.jobKey(request.getJobName(), request.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                return;
            }
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            log.error("pauseJob failed", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void resumeJob(QuartzTaskRequest request) {
        log.info("resumeJob: {}", request);
        update(request);
        try {
            JobKey jobKey = JobKey.jobKey(request.getJobName(), request.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                return;
            }
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            log.error("resumeJob failed", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteJob(QuartzTaskRequest request) {
        log.info("deleteJob: {}", request);
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(request.getTriggerName(), request.getTriggerGroup());
            JobKey jobKey = JobKey.jobKey(request.getJobName(), request.getJobGroup());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            log.error("deleteJob failed", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        deleteByUid(request.getUid());
    }
    
    public void initQuartzTasks(String orgUid) {
        if (quartz_taskRepository.count() > 0) {
            return;
        }

        String jobName = "test1name";
        String group = "testGroup";
        QuartzTaskRequest request = QuartzTaskRequest.builder()
                .jobName(jobName)
                .jobGroup(group)
                .jobClassName("com.bytedesk.core.quartz.QuartzJob")
                .jobMethodName("test1")
                .description("quartz test")
                .cronExpression("*/5 * * * * ?")
                .triggerName(jobName + "trigger")
                .triggerGroup(group)
                .triggerType("cron")
                .triggerState("started")
                .orgUid(StringUtils.hasText(orgUid) ? orgUid : BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                .build();

        create(request);
    }

    
    
}
