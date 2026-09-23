/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu.mq;

import java.util.Optional;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.mq.jms.JmsArtemisConsts;
import com.bytedesk.kbase.llm_feishu.FeishuDocEntity;
import com.bytedesk.kbase.llm_feishu.FeishuDocRestService;
import com.bytedesk.kbase.llm_feishu.FeishuDocStatusEnum;
import com.bytedesk.kbase.llm_feishu.elastic.FeishuDocElasticService;
import com.bytedesk.kbase.llm_feishu.vector.FeishuDocVectorService;

import lombok.extern.slf4j.Slf4j;

/**
 * 飞书文档索引消费者：全文与向量独立处理、独立状态回写，一侧失败不影响另一侧
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "artemis", matchIfMissing = true)
public class FeishuDocIndexConsumer {

    private final FeishuDocElasticService feishuDocElasticService;
    private final FeishuDocRestService feishuDocRestService;
    private final FeishuDocVectorService feishuDocVectorService;

    public FeishuDocIndexConsumer(FeishuDocElasticService feishuDocElasticService,
            FeishuDocRestService feishuDocRestService,
            ObjectProvider<FeishuDocVectorService> feishuDocVectorServiceProvider) {
        this.feishuDocElasticService = feishuDocElasticService;
        this.feishuDocRestService = feishuDocRestService;
        this.feishuDocVectorService = feishuDocVectorServiceProvider.getIfAvailable();
        if (this.feishuDocVectorService == null) {
            log.info("飞书文档向量服务未启用（spring.ai.vectorstore.elasticsearch.enabled 未开启），仅处理全文索引");
        }
    }

    @JmsListener(destination = JmsArtemisConsts.QUEUE_FEISHU_DOC_INDEX, containerFactory = "jmsArtemisQueueFactory", concurrency = "3-10")
    public void processIndexMessage(jakarta.jms.Message jmsMessage, FeishuDocIndexMessage message) {
        // 直接对 message 判空，使后续 message.getOperationType() 等调用可被静态证明非空
        if (message == null || message.getDocUid() == null || message.getDocUid().isEmpty()) {
            log.warn("飞书文档索引消息缺少 docUid，忽略");
            acknowledgeMessage(jmsMessage);
            return;
        }
        String docUid = message.getDocUid();

        boolean success = false;
        int maxRetry = 3;
        for (int attempt = 1; attempt <= maxRetry && !success; attempt++) {
            try {
                if (attempt > 1) {
                    log.info("尝试第{}次处理飞书文档索引消息: {}", attempt, docUid);
                    Thread.sleep(100L * attempt);
                }
                Optional<FeishuDocEntity> optional = feishuDocRestService.findByUid(docUid);
                if (optional.isEmpty()) {
                    log.warn("无法找到要索引的飞书文档: {}", docUid);
                    success = true;
                    break;
                }
                FeishuDocEntity entity = optional.get();

                if ("delete".equals(message.getOperationType()) || entity.isDeleted()) {
                    handleDelete(entity);
                } else {
                    handleIndex(entity, message);
                }
                success = true;
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                log.warn("处理飞书文档索引时发生乐观锁冲突: {}, 尝试次数: {}", docUid, attempt);
                if (attempt == maxRetry) {
                    log.error("达到最大重试次数，乐观锁冲突无法解决: {}", docUid);
                    success = true;
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("处理飞书文档索引消息出错: {}, 错误: {}", docUid, e.getMessage(), e);
                if (attempt == maxRetry) {
                    feishuDocRestService.updateSyncStatusOnly(docUid, FeishuDocStatusEnum.ERROR.name());
                }
            }
        }

        if (success) {
            acknowledgeMessage(jmsMessage);
        } else {
            log.warn("飞书文档索引消息处理失败，等待重新投递: {}", docUid);
        }
    }

    private void handleIndex(FeishuDocEntity entity, FeishuDocIndexMessage message) {
        boolean elasticOk = true;
        boolean vectorOk = true;

        if (Boolean.TRUE.equals(message.getUpdateElasticIndex())) {
            try {
                feishuDocElasticService.indexDoc(entity);
            } catch (Exception e) {
                elasticOk = false;
            }
        }

        if (Boolean.TRUE.equals(message.getUpdateVectorIndex())) {
            if (feishuDocVectorService == null) {
                log.debug("向量服务不可用，跳过飞书文档向量索引: {}", entity.getUid());
            } else {
                try {
                    feishuDocVectorService.indexDocVector(entity);
                } catch (Exception e) {
                    vectorOk = false;
                }
            }
        }

        if (!elasticOk || !vectorOk) {
            // 单侧失败已在 service 内回写 ERROR 状态，此处仅决定是否重试：双侧失败才抛出
            if (!elasticOk && !vectorOk) {
                throw new RuntimeException("飞书文档全文与向量索引均失败: " + entity.getUid());
            }
            log.warn("飞书文档索引部分失败（不重试，状态已回写ERROR）: uid={}, elasticOk={}, vectorOk={}",
                    entity.getUid(), elasticOk, vectorOk);
        }
    }

    private void handleDelete(FeishuDocEntity entity) {
        feishuDocElasticService.deleteDoc(entity.getUid());
        if (feishuDocVectorService != null) {
            try {
                feishuDocVectorService.deleteDocVector(entity);
            } catch (Exception e) {
                log.warn("飞书文档向量索引删除失败: uid={}, error={}", entity.getUid(), e.getMessage());
            }
        }
        // 对齐 FAQ 删除语义：状态置 NEW + 清空 docIdList（经由 @Transactional 包装，勿直调 repository）
        feishuDocRestService.updateElasticStatusOnly(entity.getUid(), FeishuDocStatusEnum.NEW.name());
        feishuDocRestService.updateVectorStatusOnly(entity.getUid(), FeishuDocStatusEnum.NEW.name());
        feishuDocRestService.updateDocIdList(entity.getUid(), java.util.Collections.emptyList());
    }

    private void acknowledgeMessage(jakarta.jms.Message jmsMessage) {
        try {
            if (jmsMessage != null) {
                jmsMessage.acknowledge();
            }
        } catch (Exception e) {
            log.warn("确认飞书文档索引消息失败: {}", e.getMessage());
        }
    }
}
