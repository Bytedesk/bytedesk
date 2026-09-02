/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-02 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-02 14:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights to use the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 *
 */
package com.bytedesk.ai.springai.observability;

import org.springframework.ai.chat.client.observation.ChatClientObservationContext;

import com.bytedesk.core.config.metrics.BytedeskMetrics;

import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;

/**
 * 将 ChatClient 观测事件桥接到 {@code bytedesk.ai.*} 业务指标。
 *
 * <p>背景：{@code BytedeskMetrics} 中的 {@code bytedesk.ai.requests} /
 * {@code bytedesk.ai.errors} / {@code bytedesk.ai.response.time} 此前没有任何调用方，
 * 导致 Grafana「AI 错误率」面板（{@code bytedesk_ai_errors_total / bytedesk_ai_requests_total}）
 * 恒为 No Data。</p>
 *
 * <p>本 Handler 注册在统一 {@code ObservationRegistry} 上，在 ChatClient 观测的
 * 生命周期钩子中驱动业务计数器与计时器，覆盖全部走 ChatClient 的 AI 问答（含
 * zhipuai / deepseek / dashscope 等 provider），无需侵入各业务流程。</p>
 */
public class BytedeskAiMetricsObservationHandler implements ObservationHandler<ChatClientObservationContext> {

    /** Timer.Sample 存放在 Observation.Context 中的 key。 */
    private static final String RESPONSE_SAMPLE_KEY = "bytedesk.ai.metrics.response.sample";

    private final BytedeskMetrics bytedeskMetrics;

    public BytedeskAiMetricsObservationHandler(BytedeskMetrics bytedeskMetrics) {
        this.bytedeskMetrics = bytedeskMetrics;
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof ChatClientObservationContext;
    }

    @Override
    public void onStart(ChatClientObservationContext context) {
        context.put(RESPONSE_SAMPLE_KEY, bytedeskMetrics.startAiResponseTimer());
    }

    @Override
    public void onStop(ChatClientObservationContext context) {
        Object sample = context.remove(RESPONSE_SAMPLE_KEY);
        if (sample instanceof Timer.Sample timerSample) {
            bytedeskMetrics.stopAiResponseTimer(timerSample);
        }
        bytedeskMetrics.aiRequestMade();
    }

    @Override
    public void onError(ChatClientObservationContext context) {
        bytedeskMetrics.aiError();
    }
}
