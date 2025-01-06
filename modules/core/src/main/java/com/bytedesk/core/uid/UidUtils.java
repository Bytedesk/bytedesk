/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-07 15:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-30 10:55:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.uid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.core.uid.impl.CachedUidGenerator;
import com.bytedesk.core.uid.impl.DefaultUidGenerator;

import jakarta.annotation.PostConstruct;

// import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/wujun234/uid-generator-spring-boot-starter/blob/master/README.md
 * UidGenerator接口提供了 UID 生成和解析的方法，提供了两种实现:
 */
// @Slf4j
@Component
public class UidUtils {

    @Autowired
    private DefaultUidGenerator defaultUidGenerator;

    @Autowired
    private CachedUidGenerator cachedUidGenerator;

    private static UidUtils instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    // 使用方法：String uid = UidUtils.getInstance().getDefaultSerialUid();
    public static UidUtils getInstance() {
        return instance;
    }

    /**
     * 实时生成
     * 性能有损耗
     * 
     * @return string
     */
    public String getDefaultSerialUid() {
        // Generate UID
        long uid = defaultUidGenerator.getUID();
        // Parse UID into [Timestamp, WorkerId, Sequence]
        // {"UID":"1165810429067392","timestamp":"2023-07-17 12:17:13","workerId":"1","sequence":"0"}
        // log.info(defaultUidGenerator.parseUID(uid));
        return String.valueOf(uid);
    }


    /**
     * 生成一次id之后，按序列号+1生成一批id，缓存，供之后请求
     * 如对UID生成性能有要求, 请使用CachedUidGenerator
     * 
     * @return string
     */
    public String getCacheSerialUid() {
        // Generate UID
        long uid = cachedUidGenerator.getUID();
        // Parse UID into [Timestamp, WorkerId, Sequence]
        // {"UID":"1165809330159873","timestamp":"2023-07-17 12:15:02","workerId":"2","sequence":"1"}
        // log.info(cachedUidGenerator.parseUID(uid));
        return String.valueOf(uid);
    }

    public String getUid() {
        return getDefaultSerialUid();
    }

}
