/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 14:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 14:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk.mq;

import java.util.Optional;
import java.util.Random;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.jms.JmsArtemisConsts;
import com.bytedesk.kbase.llm_chunk.ChunkEntity;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * Chunk索引消费者
 * 用于处理Chunk索引队列中的消息
 * 解决大文件切分时的并发问题和重复内容问题
 */
@Slf4j
@Component
public class ChunkIndexConsumer {

    private final ChunkElasticService chunkElasticService;
    private final ChunkRestService chunkRestService;
    private final Random random = new Random();
    
    @Autowired(required = false)
    private ChunkVectorService chunkVectorService;
    
    @Autowired
    private org.springframework.jms.core.JmsTemplate jmsTemplate;

    public ChunkIndexConsumer(ChunkElasticService chunkElasticService, ChunkRestService chunkRestService) {
        this.chunkElasticService = chunkElasticService;
        this.chunkRestService = chunkRestService;
        
        // 在构造函数中检查并记录向量服务状态
        if (chunkVectorService == null) {
            log.warn("Chunk向量存储服务未启用。如需启用，请检查配置: spring.ai.vectorstore.elasticsearch.enabled=true 并确保 Elasticsearch 正常运行");
        } else {
            log.info("Chunk向量存储服务已启用并可用");
        }
    }

    /**
     * 处理Chunk索引队列中的消息
     * 使用客户端确认模式，增强错误处理和重试机制
     * 
     * @param jmsMessage JMS原始消息
     * @param message Chunk索引消息
     */
    @JmsListener(destination = JmsArtemisConsts.QUEUE_CHUNK_INDEX, containerFactory = "jmsArtemisQueueFactory", concurrency = "2-5")
    public void processIndexMessage(jakarta.jms.Message jmsMessage, ChunkIndexMessage message) {
        boolean success = false;
        int maxRetryAttempts = 3;
        int currentAttempt = 1;
        
        // 从JMS消息中获取批处理信息（如果有的话）
        String batchId = getBatchInfo(jmsMessage);
        
        while (currentAttempt <= maxRetryAttempts && !success) {
            try {
                if (currentAttempt > 1) {
                    log.info("尝试第{}次处理Chunk索引消息: {}, 批次: {}", currentAttempt, message.getChunkUid(), batchId);
                } else {
                    log.debug("接收到Chunk索引请求: {}, 操作: {}, 批次: {}", 
                            message.getChunkUid(), message.getOperationType(), batchId);
                }
                
                // 引入随机延迟，避免并发冲突
                int baseDelay = 30 + random.nextInt(100); // 基础随机延迟30-130ms
                int retryDelay = baseDelay * currentAttempt; // 根据重试次数增加延迟
                Thread.sleep(retryDelay);
                
                // 获取Chunk实体
                Optional<ChunkEntity> optionalChunk = chunkRestService.findByUid(message.getChunkUid());
                if (!optionalChunk.isPresent()) {
                    log.warn("无法找到要索引的Chunk: {}, 批次: {}", message.getChunkUid(), batchId);
                    // 消息处理完成，但没有找到实体，也认为是成功的（避免重复处理已删除的实体）
                    success = true;
                    break;
                }
                
                ChunkEntity chunk = optionalChunk.get();
                
                // 检查内容是否过短（避免处理无意义的短内容）
                if (chunk.getContent() != null && chunk.getContent().trim().length() < 20) {
                    log.warn("Chunk内容过短，跳过索引: {}, 内容长度: {}, 批次: {}", 
                            chunk.getUid(), chunk.getContent().length(), batchId);
                    success = true;
                    break;
                }
                
                // 根据操作类型执行相应的操作
                if ("delete".equals(message.getOperationType())) {
                    handleDeleteOperation(chunk, message, batchId);
                } else {
                    handleIndexOperation(chunk, message, batchId);
                }
                
                // 成功处理消息
                success = true;
                log.debug("成功处理Chunk索引消息: {}, 批次: {}", message.getChunkUid(), batchId);
                
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                // 乐观锁冲突，特殊处理
                log.warn("处理Chunk索引时发生乐观锁冲突: {}, 尝试次数: {}, 批次: {}", 
                        message.getChunkUid(), currentAttempt, batchId);
                if (currentAttempt == maxRetryAttempts) {
                    log.error("达到最大重试次数，乐观锁冲突无法解决: {}, 批次: {}", message.getChunkUid(), batchId);
                    // 乐观锁冲突在大文件处理时比较常见，标记为成功避免无限重试
                    success = true;
                }
            } catch (Exception e) {
                log.error("处理Chunk索引消息时出错: {}, 错误: {}, 批次: {}", 
                        message.getChunkUid(), e.getMessage(), batchId, e);
                if (currentAttempt < maxRetryAttempts) {
                    log.info("将在当前消费者内进行重试处理");
                } else {
                    log.error("达到最大重试次数，处理失败: {}, 批次: {}", message.getChunkUid(), batchId);
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
            log.warn("Chunk索引消息处理失败，不确认消息，等待消息队列重新投递: {}, 批次: {}", 
                    message.getChunkUid(), batchId);
        }
    }
    
    /**
     * 从JMS消息中获取批处理信息
     */
    private String getBatchInfo(jakarta.jms.Message jmsMessage) {
        try {
            String batchId = jmsMessage.getStringProperty("batchId");
            if (batchId != null) {
                int batchIndex = jmsMessage.getIntProperty("batchIndex");
                int batchSize = jmsMessage.getIntProperty("batchSize");
                return String.format("%s[%d/%d]", batchId, batchIndex + 1, batchSize);
            }
        } catch (Exception e) {
            // 忽略获取批处理信息的异常
        }
        return "single";
    }
    
    /**
     * 处理索引操作
     * 将全文索引和向量索引的操作分开处理，避免一个操作失败影响另一个操作
     */
    private void handleIndexOperation(ChunkEntity chunk, ChunkIndexMessage message, String batchInfo) {
        boolean elasticSuccess = true;
        boolean vectorSuccess = true;
        
        // 执行全文索引 - 独立事务
        if (message.getUpdateElasticIndex()) {
            try {
                log.debug("为Chunk创建全文索引: {}, 批次: {}", chunk.getUid(), batchInfo);
                processElasticIndex(chunk);
                elasticSuccess = true;
            } catch (Exception e) {
                log.error("Chunk全文索引创建失败: {}, 错误: {}, 批次: {}", 
                        chunk.getUid(), e.getMessage(), batchInfo, e);
                elasticSuccess = false;
            }
        }
        
        // 执行向量索引 - 独立事务
        if (message.getUpdateVectorIndex()) {
            try {
                // 检查向量服务是否可用
                if (chunkVectorService == null) {
                    log.debug("向量服务不可用，跳过向量索引处理: {}, 批次: {}", chunk.getUid(), batchInfo);
                } else {
                    log.debug("为Chunk创建向量索引: {}, 批次: {}", chunk.getUid(), batchInfo);
                    processVectorIndex(chunk);
                }
                vectorSuccess = true;
            } catch (Exception e) {
                log.error("Chunk向量索引创建失败: {}, 错误: {}, 批次: {}", 
                        chunk.getUid(), e.getMessage(), batchInfo, e);
                vectorSuccess = false;
            }
        }
        
        // 根据处理结果决定是否重试消息
        if (!elasticSuccess || !vectorSuccess) {
            StringBuilder errorMessage = new StringBuilder("Chunk索引失败: ");
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
                log.warn(errorMessage.toString() + "，但部分索引已成功，不重试消息，批次: {}", batchInfo);
            }
        }
    }
    
    /**
     * 在单独事务中处理全文索引
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void processElasticIndex(ChunkEntity chunk) {
        log.debug("在独立事务中处理全文索引: {}", chunk.getUid());
        
        try {
            chunkElasticService.indexChunk(chunk);
            
            // 更新chunk状态
            chunk.setElasticSuccess();
            chunkRestService.save(chunk);
            
            // 发送处理完成通知
            sendChunkCompleteNotification(chunk, "success", null, "elastic");
            
        } catch (Exception e) {
            log.error("全文索引处理失败: {}, 错误: {}", chunk.getUid(), e.getMessage(), e);
            
            // 更新错误状态
            try {
                chunk.setElasticError();
                chunkRestService.save(chunk);
            } catch (Exception ex) {
                log.error("更新chunk错误状态失败: {}", chunk.getUid(), ex);
            }
            
            // 发送处理失败通知
            sendChunkCompleteNotification(chunk, "error", e.getMessage(), "elastic");
            
            throw e; // 重新抛出异常
        }
    }
    
    /**
     * 处理向量索引（使用独立的事务管理策略）
     */
    public void processVectorIndex(ChunkEntity chunk) {
        try {
            // 检查向量服务是否可用
            if (chunkVectorService == null) {
                log.warn("向量服务不可用，跳过向量索引处理: {}", chunk.getUid());
                return;
            }
            
            log.debug("开始处理Chunk向量索引: {}, 当前状态: {}", chunk.getUid(), chunk.getVectorStatus());

            // 添加重复检查机制，避免重复的chunk被重复索引
            if (isDuplicateContent(chunk)) {
                log.warn("发现重复内容的Chunk，跳过向量索引: {}", chunk.getUid());
                markChunkAsProcessed(chunk);
                return;
            }
            
            // 添加乐观锁重试机制
            int maxRetries = 3;
            int retryCount = 0;
            boolean indexSuccess = false;
            
            while (!indexSuccess && retryCount < maxRetries) {
                try {
                    if (retryCount > 0) {
                        // 重试前等待一段随机时间，避免并发冲突
                        int delay = 100 + random.nextInt(retryCount * 200);
                        Thread.sleep(delay);
                        
                        // 重新获取最新的Chunk实体
                        Optional<ChunkEntity> refreshedChunkOpt = chunkRestService.findByUid(chunk.getUid());
                        if (refreshedChunkOpt.isPresent()) {
                            chunk = refreshedChunkOpt.get();
                            log.debug("重试前刷新Chunk实体: {}, 尝试次数: {}", chunk.getUid(), retryCount + 1);
                        } else {
                            log.warn("重试时无法找到Chunk实体: {}", chunk.getUid());
                            break;
                        }
                    }
                    
                    chunkVectorService.indexChunkVector(chunk);
                    indexSuccess = true;
                    log.debug("成功创建Chunk向量索引: {}, 尝试次数: {}", chunk.getUid(), retryCount + 1);
                    
                    // 更新状态
                    markChunkAsProcessed(chunk);
                    
                } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                    retryCount++;
                    log.warn("创建向量索引时发生乐观锁冲突: {}, 尝试次数: {}/{}",
                            chunk.getUid(), retryCount, maxRetries);
                    
                    if (retryCount >= maxRetries) {
                        log.error("达到最大重试次数，创建向量索引失败: {}", chunk.getUid());
                    }
                } catch (Exception e) {
                    retryCount++;
                    log.warn("创建向量索引时出错: {}, 尝试次数: {}/{}, 错误: {}", 
                            chunk.getUid(), retryCount, maxRetries, e.getMessage());
                    
                    if (retryCount >= maxRetries) {
                        log.error("达到最大重试次数，创建向量索引失败: {}", chunk.getUid());
                        throw e; // 重新抛出异常
                    }
                }
            }
            
        } catch (Exception e) {
            // 记录异常并重新抛出，让上层处理
            log.error("处理向量索引时出错: {}, 错误: {}", chunk.getUid(), e.getMessage(), e);
            throw new RuntimeException("向量索引处理失败", e);
        }
    }
    
    /**
     * 检查是否为重复内容
     * 简单的重复检查策略：基于内容哈希值
     */
    private boolean isDuplicateContent(ChunkEntity chunk) {
        if (chunk.getContent() == null || chunk.getContent().trim().isEmpty()) {
            return true; // 空内容视为重复
        }
        
        String content = chunk.getContent().trim();
        
        // 检查内容是否过短或过于重复
        if (content.length() < 50) {
            return true; // 内容过短
        }
        
        // 检查是否包含重复的短语（简单的重复检测）
        String[] words = content.split("\\s+");
        if (words.length < 5) {
            return true; // 词汇数量过少
        }
        
        // 可以在这里添加更复杂的重复检测逻辑
        // 例如：检查数据库中是否已存在相似内容的chunk
        
        return false;
    }
    
    /**
     * 标记chunk为已处理
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void markChunkAsProcessed(ChunkEntity chunk) {
        try {
            chunk.setVectorSuccess();
            chunkRestService.save(chunk);
            log.debug("已标记Chunk为向量索引成功: {}", chunk.getUid());
            
            // 发送处理完成通知
            sendChunkCompleteNotification(chunk, "success", null, "vector");
            
        } catch (Exception e) {
            log.warn("更新Chunk向量索引状态失败: {}, 错误: {}", chunk.getUid(), e.getMessage());
            // 发送处理失败通知
            sendChunkCompleteNotification(chunk, "error", e.getMessage(), "vector");
        }
    }
    
    /**
     * 发送chunk处理完成通知
     */
    private void sendChunkCompleteNotification(ChunkEntity chunk, String status, String errorMessage, String processType) {
        try {
            String fileUid = chunk.getFile() != null ? chunk.getFile().getUid() : null;
            
            if (fileUid != null) {
                ChunkCompleteMessage message = ChunkCompleteMessage.builder()
                        .chunkUid(chunk.getUid())
                        .fileUid(fileUid)
                        .status(status)
                        .errorMessage(errorMessage)
                        .processType(processType)
                        .build();
                
                jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_CHUNK_COMPLETE, message);
                log.debug("已发送chunk处理完成通知: {}, 状态: {}", chunk.getUid(), status);
            }
        } catch (Exception e) {
            log.error("发送chunk完成通知失败: {}, 错误: {}", chunk.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 处理删除操作
     * 将全文索引和向量索引的删除操作分开处理
     */
    private void handleDeleteOperation(ChunkEntity chunk, ChunkIndexMessage message, String batchInfo) {
        boolean elasticSuccess = true;
        boolean vectorSuccess = true;
        
        // 从全文索引中删除 - 独立事务
        if (message.getUpdateElasticIndex()) {
            try {
                log.debug("从全文索引中删除Chunk: {}, 批次: {}", chunk.getUid(), batchInfo);
                elasticSuccess = processElasticDelete(chunk.getUid());
                if (!elasticSuccess) {
                    log.warn("从Elasticsearch中删除Chunk全文索引失败: {}, 批次: {}", chunk.getUid(), batchInfo);
                }
            } catch (Exception e) {
                log.error("从全文索引中删除Chunk失败: {}, 错误: {}, 批次: {}", 
                        chunk.getUid(), e.getMessage(), batchInfo, e);
                elasticSuccess = false;
            }
        }
        
        // 从向量索引中删除 - 独立事务
        if (message.getUpdateVectorIndex()) {
            try {
                // 检查向量服务是否可用
                if (chunkVectorService == null) {
                    log.debug("向量服务不可用，跳过向量索引删除: {}, 批次: {}", chunk.getUid(), batchInfo);
                    vectorSuccess = true;
                } else {
                    log.debug("从向量索引中删除Chunk: {}, 批次: {}", chunk.getUid(), batchInfo);
                    vectorSuccess = processVectorDelete(chunk);
                    if (!vectorSuccess) {
                        log.warn("从向量存储中删除Chunk索引失败: {}, 批次: {}", chunk.getUid(), batchInfo);
                    }
                }
            } catch (Exception e) {
                log.error("从向量索引中删除Chunk失败: {}, 错误: {}, 批次: {}", 
                        chunk.getUid(), e.getMessage(), batchInfo, e);
                vectorSuccess = false;
            }
        }
        
        // 根据处理结果决定是否重试消息
        if (!elasticSuccess || !vectorSuccess) {
            StringBuilder errorMessage = new StringBuilder("Chunk删除失败: ");
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
                log.warn(errorMessage.toString() + "，但部分删除已成功，不重试消息，批次: {}", batchInfo);
            }
        }
    }
    
    /**
     * 在单独事务中处理全文索引删除
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean processElasticDelete(String chunkUid) {
        log.debug("在独立事务中处理全文索引删除: {}", chunkUid);
        return chunkElasticService.deleteChunk(chunkUid);
    }
    
    /**
     * 在单独事务中处理向量索引删除
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public Boolean processVectorDelete(ChunkEntity chunk) {
        // 检查向量服务是否可用
        if (chunkVectorService == null) {
            log.warn("向量服务不可用，跳过向量索引删除: {}", chunk.getUid());
            return true;
        }
        
        try {
            log.debug("在独立事务中处理向量索引删除: {}", chunk.getUid());
            chunkVectorService.deleteChunkVector(chunk);
            return true;
        } catch (Exception e) {
            log.error("向量索引删除失败: {}, 错误: {}", chunk.getUid(), e.getMessage(), e);
            return false;
        }
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
