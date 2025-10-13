/*
 * QuartzEventPublisher: 专职发布 Quartz 定时事件
 */
package com.bytedesk.core.quartz.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.quartz.event.QuartzDay8Event;
import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzHalfHourEvent;
import com.bytedesk.core.quartz.event.QuartzHourlyEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuartzEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishQuartzDay8Event() {
        applicationEventPublisher.publishEvent(new QuartzDay8Event(this));
    }

    public void publishQuartzDay0Event() {
        applicationEventPublisher.publishEvent(new QuartzDay0Event(this));
    }

    public void publishQuartzHourlyEvent() {
        applicationEventPublisher.publishEvent(new QuartzHourlyEvent(this));
    }

    public void publishQuartzHalfHourEvent() {
        applicationEventPublisher.publishEvent(new QuartzHalfHourEvent(this));
    }

    public void publishQuartzFiveSecondEvent() {
        applicationEventPublisher.publishEvent(new QuartzFiveSecondEvent(this));
    }

    public void publishQuartzFiveMinEvent() {
        applicationEventPublisher.publishEvent(new QuartzFiveMinEvent(this));
    }

    public void publishQuartzOneMinEvent() {
        applicationEventPublisher.publishEvent(new QuartzOneMinEvent(this));
    }
}
