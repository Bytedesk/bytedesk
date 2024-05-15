/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-14 09:39:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-14 15:33:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.quartz.Job;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.QuartzConsts;
import com.bytedesk.core.constant.UserConsts;
// import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://cloud.tencent.com/developer/article/1923722
 */
@Slf4j
@Service
@AllArgsConstructor
public class QuartzService extends BaseService<QuartzEntity, QuartzRequest, QuartzResponse> {

    private Scheduler scheduler;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    private QuartzRepository quartzRepository;

    // private AuthService authService;

    @Override
    public Page<QuartzResponse> queryByOrg(QuartzRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC, "updatedAt");

        Page<QuartzEntity> page = quartzRepository.findByOrgUid(request.getOrgUid(), pageable);

        return page.map(this::convertToQuartzResponse);
    }

    @Override
    public Page<QuartzResponse> queryByUser(QuartzRequest request) {

        // Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC, "updatedAt");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public Optional<QuartzEntity> findByUid(String uid) {
        return quartzRepository.findByUid(uid);
    }

    public Boolean existsByJobName(String jobName) {
        return quartzRepository.existsByJobName(jobName);
    }

    public QuartzEntity save(QuartzEntity quartzEntity) {
        return quartzRepository.save(quartzEntity);
    }


    @Override
    public void deleteByUid(QuartzRequest quartzRequest) {
        log.info("deleteByUid {}", quartzRequest.getUid());
        quartzRepository.deleteByUid(quartzRequest.getUid());
    }
    
    @Override
    public void delete(QuartzEntity object) {
        quartzRepository.delete(object);
    }

    public QuartzResponse create(QuartzRequest quartzRequest) {

        if (existsByJobName(quartzRequest.getJobName())) {
            return null;
        }

        QuartzEntity quartzEntity = modelMapper.map(quartzRequest, QuartzEntity.class);
        quartzEntity.setUid(uidUtils.getCacheSerialUid());

        quartzEntity = save(quartzEntity);

        return convertToQuartzResponse(quartzEntity);
    }

    public QuartzResponse update(QuartzRequest quartzRequest) {

        Optional<QuartzEntity> quartzOptional = findByUid(quartzRequest.getUid());
        if (!quartzOptional.isPresent()) {
            return null;
        }
        // 
        quartzOptional.get().setJobName(quartzRequest.getJobName());
        quartzOptional.get().setJobGroup(quartzRequest.getJobGroup());
        quartzOptional.get().setDescription(quartzRequest.getDescription());
        quartzOptional.get().setJobClassName(quartzRequest.getJobClassName());
        quartzOptional.get().setJobMethodName(quartzRequest.getJobMethodName());
        quartzOptional.get().setCronExpression(quartzRequest.getCronExpression());
        // 
        quartzOptional.get().setTriggerName(quartzRequest.getTriggerName());
        quartzOptional.get().setTriggerGroup(quartzRequest.getTriggerGroup());
        quartzOptional.get().setTriggerType(quartzRequest.getTriggerType());
        quartzOptional.get().setTriggerState(quartzRequest.getTriggerState());
        //
        return convertToQuartzResponse(save(quartzOptional.get()));
    }

    public QuartzResponse convertToQuartzResponse(QuartzEntity quartzEntity) {
        return modelMapper.map(quartzEntity, QuartzResponse.class);
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            QuartzEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    public void startJob(QuartzRequest quartzRequest) {
        log.info("startJob: {}", quartzRequest);
        update(quartzRequest);
        
        try {

            JobKey jobKey = new JobKey(quartzRequest.getJobName(), quartzRequest.getJobGroup());
            // 如果存在这个任务，则删除
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            @SuppressWarnings("unchecked")
            Class<? extends Job> cls = (Class<? extends Job>) Class.forName(quartzRequest.getJobClassName());
            // cls.getDeclaredConstructor().newInstance();
            // 
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(QuartzConsts.JOB_METHORD_NAME, quartzRequest.getJobMethodName());
            JobDetail jobDetail = JobBuilder
                    // .newJob(QuartzJob.class)
                    .newJob(cls)
                    .withIdentity(jobKey)
                    .withDescription(quartzRequest.getDescription())
                    .setJobData(jobDataMap)
                    .build();

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
                    .cronSchedule(quartzRequest.getCronExpression());

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(quartzRequest.getTriggerName(), quartzRequest.getTriggerGroup())
                    .withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException | ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void pauseJob(QuartzRequest quartzRequest) {
        log.info("pauseJob: {}", quartzRequest);
        update(quartzRequest);

        try {
            JobKey jobKey = JobKey.jobKey(quartzRequest.getJobName(), quartzRequest.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                return;
            }
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void resumeJob(QuartzRequest quartzRequest) {
        log.info("resumeJob: {}", quartzRequest);
        update(quartzRequest);

        try {
            JobKey jobKey = JobKey.jobKey(quartzRequest.getJobName(), quartzRequest.getJobGroup());
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                return;
            }
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void deleteJob(QuartzRequest quartzRequest) {
        log.info("deleteJob: {}", quartzRequest);
       
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(quartzRequest.getTriggerName(),
                    quartzRequest.getTriggerGroup());
            JobKey jobKey = JobKey.jobKey(quartzRequest.getJobName(), quartzRequest.getJobGroup());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                log.info("trigger is null");
                deleteByUid(quartzRequest);
                return;
            }
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // 
        deleteByUid(quartzRequest);
    }


    //
    public void initData() {
        if (quartzRepository.count() > 0) {
            return;
        }

        //
        String jobname = "test1name";
        String group = "testgroup";
        QuartzRequest quartzRequest = QuartzRequest.builder()
                .jobName(jobname)
                .jobGroup(group)
                .jobClassName("com.bytedesk.core.quartz.QuartzJob")
                .jobMethodName("test1")
                .description("quartz test")
                .cronExpression("*/5 * * * * ?")
                .triggerName(jobname + "trigger")
                .triggerGroup(group)
                .triggerType("cron")
                .triggerState("started")
                .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .build();
        //
        create(quartzRequest);
        //
        startJob(quartzRequest);
    }

}
