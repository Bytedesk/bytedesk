/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2021-02-24 15:52:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-07 15:40:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.uid.impl;

import lombok.Data;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * UID 的配置
 *
 * @author wujun
 * @date 2019.02.20 10:31
 */
@Data
@ConfigurationProperties(prefix = "bytedesk.uid")
public class UidProperties {

    /**
     * 时间增量值占用位数。当前时间相对于时间基点的增量值，单位为秒
     */
    private Integer timeBits = 30;

    /**
     * 工作机器ID占用的位数
     */
    private Integer workerBits = 16;

    /**
     * 序列号占用的位数
     */
    private Integer seqBits = 7;

    /**
     * 时间基点. 例如 2019-02-20 (毫秒: 1550592000000)
     */
    private String epochStr = "2019-02-20";

    /**
     * 时间基点对应的毫秒数
     */
    private long epochSeconds = TimeUnit.MILLISECONDS.toSeconds(1550592000000L);

    /**
     * 是否容忍时钟回拨, 默认:true
     */
    private boolean enableBackward = true;

    /**
     * 时钟回拨最长容忍时间（秒）
     */
    private long maxBackwardSeconds = 1L;

    // public void setEpochStr(String epochStr) {
    //     if (StringUtils.hasText(epochStr)) {
    //         this.epochStr = epochStr;
    //         this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(DateUtils.parseByDayPattern(epochStr).getTime());
    //     }
    // }

}
