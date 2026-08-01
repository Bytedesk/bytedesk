package com.bytedesk.call.esl_event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.call.freeswitch.esl-event")
public class EslEventIngestProperties {

    /**
     * 是否启用ESL事件入库
     */
    private boolean enabled = true;

    /**
     * 采样率，范围 [0, 1]
     */
    private double sampleRate = 1.0d;

    /**
     * 需要忽略入库的 Event-Subclass（大小写不敏感）
     */
    private List<String> ignoreSubclasses = new ArrayList<>(List.of("sofia::register"));

    /**
     * 需要忽略入库的 API 命令（大小写不敏感）
     */
    private List<String> ignoreApiCommands = new ArrayList<>(List.of("status"));

    /**
     * headersJson/bodyJson 最大保留长度，防止超大事件压垮存储
     */
    private int maxPayloadLength = 20000;

    /**
     * 历史数据保留天数（<=0 表示不清理）
     */
    private int retentionDays = 30;

    /**
     * 清理任务 cron
     */
    private String cleanupCron = "0 30 3 * * ?";
}
