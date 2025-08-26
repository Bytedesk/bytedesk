/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 14:40:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 14:45:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionRequest;
import com.bytedesk.core.action.ActionRestService;
import com.bytedesk.core.action.ActionTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * Generic handler for optimistic locking retry operations
 */
@Slf4j
@Service
public class OptimisticLockingHandler {
    
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000;

    private final ActionRestService actionService;

    public OptimisticLockingHandler(ActionRestService actionService) {
        this.actionService = actionService;
    }

    public <T> T executeWithRetry(RetryCallback<T> callback, String entityName, String entityUid, Object entity) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_ATTEMPTS) {
            try {
                return callback.execute();
            } catch (ObjectOptimisticLockingFailureException ex) {
                retryCount++;
                log.error("Optimistic locking failure for {}: {}, retry count: {}", 
                    entityName, entityUid, retryCount);
                
                if (retryCount == MAX_RETRY_ATTEMPTS) {
                    handleFailedRetries(entity, entityName);
                    throw ex;
                }

                try {
                    Thread.sleep(RETRY_DELAY_MS * (1 <&lt; (retryCount - 1)));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying", ie);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error executing retry callback", e);
            }
        }
        return null;
    }

    private void handleFailedRetries(Object entity, String entityName) {
        String entityJSON = JSONObject.toJSONString(entity);
        ActionRequest actionRequest = ActionRequest.builder()
                .title(entityName)
                .action("save")
                .description("All retry attempts failed for optimistic locking")
                .extra(entityJSON)
                .build();
        actionRequest.setType(ActionTypeEnum.FAILED.name());
        actionService.create(actionRequest);
        log.error("All retry attempts failed for optimistic locking of {}", entityJSON);
    }
} 