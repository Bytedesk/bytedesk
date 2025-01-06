/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 14:11:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-29 14:21:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.exception;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Component
@AllArgsConstructor
public class ExceptionHandlerUtils {

    // private final BytedeskEventPublisher bytedeskEventPublisher;
    // // 
    // private static final int MAX_RETRY_ATTEMPTS = 3; // 设定最大重试次数
    // private static final long RETRY_DELAY_MS = 5000; // 设定重试间隔（毫秒）
    // private final Queue<Object> retryQueue = new LinkedList<>();
    
    // // 
    // public void processRetryQueue(Object entity, JpaRepository<Object, Long> entityRepository) {
    //     retryQueue.add(entity);
    //     // 
    //     while (!retryQueue.isEmpty()) {
    //         Object message = retryQueue.poll(); // 从队列中取出一个元素
    //         if (message == null) {
    //             break; // 队列为空，跳出循环
    //         }

    //         int retryCount = 0;
    //         while (retryCount < MAX_RETRY_ATTEMPTS) {
    //             try {
    //                 // 尝试更新Topic对象
    //                 entityRepository.save(message);
    //                 // 更新成功，无需进一步处理
    //                 log.info("Optimistic locking succeeded for message: {}", entity.getUid());
    //                 break; // 跳出内部循环
    //             } catch (ObjectOptimisticLockingFailureException ex) {
    //                 // 捕获乐观锁异常
    //                 log.error("Optimistic locking failure for message: {}, retry count: {}", entity.getUid(),
    //                         retryCount + 1);
    //                 // 等待一段时间后重试
    //                 try {
    //                     Thread.sleep(RETRY_DELAY_MS);
    //                 } catch (InterruptedException ie) {
    //                     Thread.currentThread().interrupt();
    //                     log.error("Interrupted while waiting for retry", ie);
    //                     return;
    //                 }
    //                 retryCount++; // 增加重试次数

    //                 // 如果还有重试机会，则将message放回队列末尾
    //                 if (retryCount < MAX_RETRY_ATTEMPTS) {
    //                     // FIXME: 发现会一直失败，暂时不重复处理
    //                     // retryQueue.add(message);
    //                 } else {
    //                     // 所有重试都失败了
    //                     handleFailedRetries(message);
    //                 }
    //             }
    //         }
    //     }
    // }

    // private void handleFailedRetries(Object message) {
    //     String messageJSON = JSONObject.toJSONString(message);
    //     ActionRequest actionRequest = ActionRequest.builder()
    //             .title("message")
    //             .action("save")
    //             .description("All retry attempts failed for optimistic locking")
    //             .extra(messageJSON)
    //             .build();
    //     actionRequest.setType(ActionTypeEnum.FAILED.name());
    //     bytedeskEventPublisher.publishActionEvent(actionRequest);
    //     log.error("All retry attempts failed for optimistic locking of message");
    //     // 根据业务逻辑决定如何处理失败，例如通知用户稍后重试或执行其他操作
    //     // notifyUserOfFailure(message);
    // }

}
