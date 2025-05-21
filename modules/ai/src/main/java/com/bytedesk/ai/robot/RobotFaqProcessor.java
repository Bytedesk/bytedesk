/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-27 15:55:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 17:04:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.dao.OptimisticLockingFailureException;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.core.constant.BytedeskConsts;
// import com.bytedesk.kbase.faq.FaqEntity;

// import jakarta.annotation.PostConstruct;
// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// @AllArgsConstructor
// public class RobotFaqProcessor {
    
//     private final RobotRestService robotRestService;
//     private final RedisTemplate<String, FaqEntity> redisTemplateFaqEntity;
    
//     // 批量处理的大小
//     private static final int BATCH_SIZE = 10;
//     // 处理间隔（毫秒）
//     private static final long PROCESS_INTERVAL = 5000;
//     // 最大重试次数
//     private static final int MAX_RETRIES = 3;

//     @PostConstruct
//     public void init() {
//         startFaqProcessor();
//     }

//     /**
//      * 添加FAQ到处理队列
//      */
//     public void addFaqToQueue(FaqEntity faq) {
//         try {
//             log.info("Adding FAQ to queue: {}", faq.getQuestion());
//             redisTemplateFaqEntity.opsForList().rightPush(RobotConsts.ROBOT_FAQ_QUEUE_KEY, faq);
//         } catch (Exception e) {
//             log.error("Failed to add FAQ to queue: {}", e.getMessage(), e);
//         }
//     }

//     private void startFaqProcessor() {
//         Thread processorThread = new Thread(() -> {
//             while (true) {
//                 try {
//                     processFaqBatch();
//                     Thread.sleep(PROCESS_INTERVAL);
//                 } catch (InterruptedException e) {
//                     log.error("FAQ processor interrupted: {}", e.getMessage());
//                     Thread.currentThread().interrupt();
//                     break;
//                 } catch (Exception e) {
//                     log.error("Error processing FAQ batch: {}", e.getMessage(), e);
//                 }
//             }
//         }, "FAQ-Processor");
        
//         processorThread.setDaemon(true);
//         processorThread.start();
//     }

//     @Transactional
//     private void processFaqBatch() {
//         Optional<RobotEntity> robotOptional = robotRestService.findByUid(BytedeskConsts.DEFAULT_ROBOT_UID);
//         if (robotOptional.isEmpty()) {
//             return;
//         }

//         RobotEntity robot = robotOptional.get();
//         boolean updated = false;
//         int retryCount = 0;

//         while (retryCount < MAX_RETRIES) {
//             List<FaqEntity> faqs = fetchFaqBatch();
//             if (faqs.isEmpty()) {
//                 break;
//             }

//             try {
//                 updated = updateRobotWithFaqs(robot, faqs);
//                 if (updated) {
//                     saveRobotChanges(robot);
//                 }
//                 break; // 成功后跳出重试循环

//             } catch (OptimisticLockingFailureException e) {
//                 handleRetry(retryCount++, faqs);
//             }
//         }
//     }

//     private List<FaqEntity> fetchFaqBatch() {
//         List<FaqEntity> faqs = new ArrayList<>();
//         try {
//             for (int i = 0; i < BATCH_SIZE; i++) {
//                 FaqEntity faq = redisTemplateFaqEntity.opsForList().leftPop(RobotConsts.ROBOT_FAQ_QUEUE_KEY);
//                 if (faq == null) {
//                     break;
//                 }
//                 faqs.add(faq);
//             }
//         } catch (Exception e) {
//             log.error("Error fetching FAQ batch: {}", e.getMessage(), e);
//         }
//         return faqs;
//     }

//     private Boolean updateRobotWithFaqs(RobotEntity robot, List<FaqEntity> faqs) {
//         boolean updated = false;
//         for (FaqEntity faq : faqs) {
//             if (robot.getServiceSettings().getHotFaqs().size() < 5) {
//                 robot.getServiceSettings().getHotFaqs().add(faq);
//                 updated = true;
//             }
//             if (robot.getServiceSettings().getFaqs().size() < 5) {
//                 robot.getServiceSettings().getFaqs().add(faq);
//                 updated = true;
//             }
//         }
//         return updated;
//     }

//     private void saveRobotChanges(RobotEntity robot) {
//         robot.getServiceSettings().setShowHotFaqs(true);
//         robot.getServiceSettings().setShowFaqs(true);
//         robotRestService.save(robot);
//     }

//     private void handleRetry(int retryCount, List<FaqEntity> faqs) {
//         if (retryCount >= MAX_RETRIES) {
//             log.error("Failed to update robot after {} retries", MAX_RETRIES);
//             // 可以选择将失败的 FAQ 重新放回队列
//             // redisTemplateFaqEntity.opsForList().rightPushAll(RobotConsts.ROBOT_FAQ_QUEUE_KEY, faqs);
//             return;
//         }

//         log.warn("Optimistic locking failure, retry attempt {}", retryCount);
//         try {
//             // 指数退避
//             Thread.sleep((long) (Math.pow(2, retryCount) * 100));
//         } catch (InterruptedException ie) {
//             Thread.currentThread().interrupt();
//         }
//     }
// }