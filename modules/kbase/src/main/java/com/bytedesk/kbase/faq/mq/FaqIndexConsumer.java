/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 15:31:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.mq;

import java.util.Optional;
import java.util.Random;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConstants;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.faq.elastic.FaqElasticService;
import com.bytedesk.kbase.faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ索引消费者
 * 用于处理FAQ索引队列中的消息
 */
@Slf4j
@Component
@AllArgsConstructor
public class FaqIndexConsumer {

    private final FaqElasticService faqElasticService;
    private final FaqVectorService faqVectorService;
    private final FaqRestService faqRestService;
    private final Random random = new Random();

    /**
     * 处理FAQ索引队列中的消息
     * 使用客户端确认模式，只有成功处理后才确认消息
     * 增强了对乐观锁冲突的处理
     * 
     * @param jmsMessage JMS原始消息
     * @param message FAQ索引消息
     */
    @JmsListener(destination = JmsArtemisConstants.QUEUE_FAQ_INDEX, containerFactory = "jmsArtemisQueueFactory", concurrency = "3-10")
    public void processIndexMessage(jakarta.jms.Message jmsMessage, FaqIndexMessage message) {
        boolean success = false;
        int maxRetryAttempts = 3; // 最大重试次数
        int currentAttempt = 1;
        
        while (currentAttempt <= maxRetryAttempts && !success) {
            try {
                if (currentAttempt > 1) {
                    log.info("尝试第{}次处理FAQ索引消息: {}", currentAttempt, message.getFaqUid());
                } else {
                    log.debug("接收到FAQ索引请求: {}", message.getFaqUid());
                }
                
                // 引入随机延迟，避免并发冲突，重试时增加延迟
                int baseDelay = 50 + random.nextInt(200); // 基础随机延迟
                int retryDelay = baseDelay * currentAttempt; // 根据重试次数增加延迟
                Thread.sleep(retryDelay);
                
                // 获取FAQ实体
                Optional<FaqEntity> optionalFaq = faqRestService.findByUid(message.getFaqUid());
                if (!optionalFaq.isPresent()) {
                    log.warn("无法找到要索引的FAQ: {}", message.getFaqUid());
                    // 消息处理完成，但没有找到实体，也认为是成功的（避免重复处理已删除的实体）
                    success = true;
                    break;
                }
                
                FaqEntity faq = optionalFaq.get();
                
                // 根据操作类型执行相应的操作
                if ("delete".equals(message.getOperationType())) {
                    handleDeleteOperation(faq, message);
                } else {
                    handleIndexOperation(faq, message);
                }
                
                // 成功处理消息
                success = true;
                log.debug("成功处理FAQ索引消息: {}", message.getFaqUid());
                
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                // 乐观锁冲突，特殊处理
                log.warn("处理FAQ索引时发生乐观锁冲突: {}, 尝试次数: {}", message.getFaqUid(), currentAttempt);
                if (currentAttempt == maxRetryAttempts) {
                    log.error("达到最大重试次数，乐观锁冲突无法解决: {}", message.getFaqUid());
                    // 最后一次尝试失败，标记为成功以避免消息无限重试
                    // 这种情况下可能需要人工干预或在后续定时任务中重新处理
                    success = true;
                }
            } catch (Exception e) {
                log.error("处理FAQ索引消息时出错: {}, 错误: {}", message.getFaqUid(), e.getMessage(), e);
                if (currentAttempt < maxRetryAttempts) {
                    log.info("将在当前消费者内进行重试处理");
                } else {
                    log.error("达到最大重试次数，处理失败: {}", message.getFaqUid());
                    success = false;
                    break;
                }
            }
            
            currentAttempt++;
        }
        
        // 根据处理结果确认或拒绝消息
        if (success) {
            acknowledgeMessage(jmsMessage);
        } else {
            log.warn("FAQ索引消息处理失败，不确认消息，等待消息队列重新投递: {}", message.getFaqUid());
        }
    }
    
    /**
     * 处理索引操作
     * 将全文索引和向量索引的操作分开处理，避免一个操作失败影响另一个操作
     */
    private void handleIndexOperation(FaqEntity faq, FaqIndexMessage message) {
        boolean elasticSuccess = true;
        boolean vectorSuccess = true;
        
        // 执行全文索引 - 独立事务
        if (message.getUpdateElasticIndex()) {
            try {
                log.debug("为FAQ创建全文索引: {}", faq.getUid());
                // 在单独的事务中处理全文索引
                processElasticIndex(faq);
                elasticSuccess = true;
            } catch (Exception e) {
                log.error("FAQ全文索引创建失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                elasticSuccess = false;
                // 这里不抛出异常，继续处理向量索引
            }
        }
        
        // 执行向量索引 - 独立事务
        if (message.getUpdateVectorIndex()) {
            try {
                log.debug("为FAQ创建向量索引: {}", faq.getUid());
                // 在单独的事务中处理向量索引
                processVectorIndex(faq);
                vectorSuccess = true;
            } catch (Exception e) {
                log.error("FAQ向量索引创建失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                vectorSuccess = false;
                // 这里不抛出异常，已经尝试过处理向量索引
            }
        }
        
        // 根据处理结果决定是否重试消息
        if (!elasticSuccess || !vectorSuccess) {
            StringBuilder errorMessage = new StringBuilder("FAQ索引失败: ");
            if (!elasticSuccess) {
                errorMessage.append("全文索引错误");
            }
            if (!vectorSuccess) {
                if (!elasticSuccess) {
                    errorMessage.append(" 和 ");
                }
                errorMessage.append("向量索引错误");
            }
            
            // 如果两个索引都失败，抛出异常以便重试
            if (!elasticSuccess && !vectorSuccess) {
                throw new RuntimeException(errorMessage.toString());
            } else {
                // 只有一个失败，记录警告但不重试消息（避免重复处理已成功的部分）
                log.warn(errorMessage.toString() + "，但部分索引已成功，不重试消息");
            }
        }
    }
    
    /**
     * 在单独事务中处理全文索引
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void processElasticIndex(FaqEntity faq) {
        log.debug("在独立事务中处理全文索引: {}", faq.getUid());
        faqElasticService.indexFaq(faq);
    }
    
    /**
     * 在单独事务中处理向量索引
     */
    // 不使用事务注解，让内部方法管理自己的事务
    public void processVectorIndex(FaqEntity faq) {
        try {
            log.info("开始处理FAQ向量索引: {}, 当前状态: {}", faq.getUid(), faq.getVectorStatus());
            
            // 获取当前实体的最新状态
            Optional<FaqEntity> latestFaqOpt = faqRestService.findByUid(faq.getUid());
            if (!latestFaqOpt.isPresent()) {
                log.error("无法获取最新FAQ实体，处理向量索引失败: {}", faq.getUid());
                return;
            }
            FaqEntity latestFaq = latestFaqOpt.get();
            
            // 先尝试删除旧的向量索引
            boolean deleteResult = deleteFaqVector(latestFaq);
            log.info("删除旧向量索引结果: {}, FAQ: {}", deleteResult, latestFaq.getUid());
            
            // 即使删除失败，也尝试创建索引，因为可能是首次创建没有需要删除的索引
            log.info("准备创建新向量索引: {}", latestFaq.getUid());
            boolean indexResult = indexFaqVector(latestFaq);
            
            if (indexResult) {
                log.info("向量索引创建成功: {}, 状态: {}", latestFaq.getUid(), latestFaq.getVectorStatus());
                
                // 再次检查状态确保保存成功
                Optional<FaqEntity> finalFaqOpt = faqRestService.findByUid(latestFaq.getUid());
                if (finalFaqOpt.isPresent() && 
                    ChunkStatusEnum.SUCCESS.name().equals(finalFaqOpt.get().getVectorStatus())) {
                    log.info("已确认FAQ向量索引状态已更新为SUCCESS: {}", latestFaq.getUid());
                } else {
                    log.warn("FAQ向量索引可能创建成功但状态未正确更新: {}", latestFaq.getUid());
                }
            } else {
                log.warn("向量索引创建失败: {}, 状态: {}", latestFaq.getUid(), latestFaq.getVectorStatus());
                
                // 如果创建失败，再尝试一次
                log.info("重试创建向量索引: {}", latestFaq.getUid());
                indexResult = indexFaqVector(latestFaq);
                log.info("重试创建向量索引结果: {}, FAQ: {}", indexResult, latestFaq.getUid());
            }
        } catch (Exception e) {
            // 记录异常但不抛出，避免影响整个流程
            log.error("处理向量索引时出错: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 在完全独立事务中删除向量索引
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean deleteFaqVector(FaqEntity faq) {
        try {
            log.debug("在完全独立事务中删除向量索引: {}", faq.getUid());
            return faqVectorService.deleteFaqVector(faq);
        } catch (Exception e) {
            log.error("删除向量索引失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 在完全独立事务中创建向量索引
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean indexFaqVector(FaqEntity faq) {
        try {
            log.info("在完全独立事务中创建向量索引: {}", faq.getUid());
            
            // 先确保FAQ存在且状态正确
            Optional<FaqEntity> faqOpt = faqRestService.findByUid(faq.getUid());
            if (!faqOpt.isPresent()) {
                log.error("找不到FAQ实体，无法创建向量索引: {}", faq.getUid());
                return false;
            }
            
            // 使用最新的FAQ实体
            FaqEntity latestFaq = faqOpt.get();

            // 调用向量服务创建索引
            faqVectorService.indexFaqVector(latestFaq);
            
            // 清除缓存并重新获取FAQ以检查状态更新
            try {
                // 尝试清除可能的缓存
                faqRestService.getClass().getMethod("clearCache", String.class).invoke(faqRestService, latestFaq.getUid());
            } catch (Exception ex) {
                log.debug("清除缓存方法不存在，忽略: {}", ex.getMessage());
            }
            
            Optional<FaqEntity> updatedFaqOpt = faqRestService.findByUid(latestFaq.getUid());
            if (!updatedFaqOpt.isPresent()) {
                log.error("创建向量索引后无法获取FAQ实体: {}", latestFaq.getUid());
                return false;
            }
            
            FaqEntity updatedFaq = updatedFaqOpt.get();
            
            // 检查操作后的向量状态来确认索引是否创建成功
            if (updatedFaq.getVectorStatus() != null && 
                updatedFaq.getVectorStatus().equals(ChunkStatusEnum.SUCCESS.name())) {
                log.info("确认向量索引创建成功: {}, 状态: {}, 文档ID: {}", 
                        updatedFaq.getUid(), 
                        updatedFaq.getVectorStatus(),
                        updatedFaq.getDocIdList());
                return true;
            } else {
                log.warn("向量索引状态未更新为成功: {}, 当前状态: {}", updatedFaq.getUid(), updatedFaq.getVectorStatus());
                
                // 尝试手动强制更新状态
                try {
                    log.info("尝试强制更新向量索引状态: {}", updatedFaq.getUid());
                    updatedFaq.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
                    updatedFaq.setDeleted(false); // 确保不会触发删除事件
                    FaqEntity savedFaq = faqRestService.save(updatedFaq);
                    
                    log.info("手动更新向量索引状态结果: 状态={}, 文档ID数量={}", 
                            savedFaq.getVectorStatus(), 
                            savedFaq.getDocIdList() != null ? savedFaq.getDocIdList().size() : 0);
                    return true;
                } catch (Exception statusEx) {
                    log.error("强制更新向量索引状态失败: {}, 错误: {}", updatedFaq.getUid(), statusEx.getMessage());
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("创建向量索引失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 处理删除操作
     * 将全文索引和向量索引的删除操作分开处理
     */
    private void handleDeleteOperation(FaqEntity faq, FaqIndexMessage message) {
        boolean elasticSuccess = true;
        boolean vectorSuccess = true;
        
        // 从全文索引中删除 - 独立事务
        if (message.getUpdateElasticIndex()) {
            try {
                log.debug("从全文索引中删除FAQ: {}", faq.getUid());
                // 在单独的事务中处理
                elasticSuccess = processElasticDelete(faq.getUid());
                if (!elasticSuccess) {
                    log.warn("从Elasticsearch中删除FAQ全文索引失败: {}", faq.getUid());
                }
            } catch (Exception e) {
                log.error("从全文索引中删除FAQ失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                elasticSuccess = false;
                // 不抛出异常，继续处理向量索引删除
            }
        }
        
        // 从向量索引中删除 - 独立事务
        if (message.getUpdateVectorIndex()) {
            try {
                log.debug("从向量索引中删除FAQ: {}", faq.getUid());
                // 在单独的事务中处理
                vectorSuccess = processVectorDelete(faq);
                if (!vectorSuccess) {
                    log.warn("从向量存储中删除FAQ索引失败: {}", faq.getUid());
                }
            } catch (Exception e) {
                log.error("从向量索引中删除FAQ失败: {}, 错误: {}", faq.getUid(), e.getMessage(), e);
                vectorSuccess = false;
                // 不抛出异常，已经尝试过处理向量索引删除
            }
        }
        
        // 根据处理结果决定是否重试消息
        if (!elasticSuccess || !vectorSuccess) {
            StringBuilder errorMessage = new StringBuilder("FAQ删除失败: ");
            if (!elasticSuccess) {
                errorMessage.append("全文索引删除错误");
            }
            if (!vectorSuccess) {
                if (!elasticSuccess) {
                    errorMessage.append(" 和 ");
                }
                errorMessage.append("向量索引删除错误");
            }
            
            // 如果两个索引删除都失败，抛出异常以便重试
            if (!elasticSuccess && !vectorSuccess) {
                throw new RuntimeException(errorMessage.toString());
            } else {
                // 只有一个失败，记录警告但不重试消息
                log.warn(errorMessage.toString() + "，但部分删除已成功，不重试消息");
            }
        }
    }
    
    /**
     * 在单独事务中处理全文索引删除
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean processElasticDelete(String faqUid) {
        log.debug("在独立事务中处理全文索引删除: {}", faqUid);
        return faqElasticService.deleteFaq(faqUid);
    }
    
    /**
     * 在单独事务中处理向量索引删除
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean processVectorDelete(FaqEntity faq) {
        log.debug("在独立事务中处理向量索引删除: {}", faq.getUid());
        return faqVectorService.deleteFaqVector(faq);
    }
    
    /**
     * 安全地确认消息
     * 只有在消息处理成功后才调用此方法
     *
     * @param message JMS消息
     */
    private void acknowledgeMessage(jakarta.jms.Message message) {
        try {
            if (message != null) {
                message.acknowledge();
                log.debug("消息已确认");
            }
        } catch (jakarta.jms.JMSException e) {
            log.error("确认消息失败: {}", e.getMessage(), e);
        }
    }
}
