/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-14 10:05:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:33:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@DisallowConcurrentExecution
public class QuartzJob extends QuartzJobBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("quartz job is running");

        JobDetail jobDetail = context.getJobDetail();
        JobDataMap dataMap = jobDetail.getJobDataMap();
        /**
         * 获取任务中保存的方法名字，动态调用方法
         */
        String methodName = dataMap.getString(QuartzConsts.JOB_METHOD_NAME);
        try {
            QuartzJob job = new QuartzJob();
            Method method = job.getClass().getMethod(methodName);
            method.invoke(job);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void test1() {
        log.info("quartz job test1 is running");
    }

    public void test2() {
        log.info("quartz job test2 is running");
    }
    
    
}
