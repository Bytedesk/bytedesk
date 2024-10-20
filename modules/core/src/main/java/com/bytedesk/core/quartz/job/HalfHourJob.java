package com.bytedesk.core.quartz.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.bytedesk.core.config.BytedeskEventPublisher;

import java.io.Serializable;

/**
 * 半小时-运行一次
 *
 * @author kefux.com on 2019/4/20
 */
@Slf4j
@AllArgsConstructor
@DisallowConcurrentExecution
public class HalfHourJob extends QuartzJobBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("HalfHourJob");
        // 
        bytedeskEventPublisher.publishQuartzHalfHourEvent();
    }

   
}
