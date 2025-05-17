package com.bytedesk.kbase.faq;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ消息队列测试类
 */
@Slf4j
@SpringBootTest
public class FaqMessageQueueTests {
    
    @Autowired
    private FaqMessageService faqMessageService;
    
    @Autowired
    private FaqRepository faqRepository;
    
    /**
     * 测试发送单个FAQ到索引队列
     */
    @Test
    public void testSendSingleFaqToQueue() {
        // 查找第一个FAQ记录
        Optional<FaqEntity> firstFaq = faqRepository.findAll().stream().findFirst();
        if (firstFaq.isPresent()) {
            String faqUid = firstFaq.get().getUid();
            log.info("发送FAQ到索引队列: {}", faqUid);
            faqMessageService.sendToIndexQueue(faqUid);
            // 等待一段时间，让消息处理完成
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("消息发送完成");
        } else {
            log.warn("没有找到FAQ记录");
        }
    }
    
    /**
     * 测试批量发送FAQ到索引队列
     */
    @Test
    public void testBatchSendFaqToQueue() {
        // 查找前10个FAQ记录
        List<FaqEntity> faqs = faqRepository.findAll().stream().limit(10).toList();
        if (!faqs.isEmpty()) {
            log.info("批量发送{}个FAQ到索引队列", faqs.size());
            faqMessageService.batchSendToIndexQueue(
                faqs.stream().map(FaqEntity::getUid).toList()
            );
            // 等待一段时间，让消息处理完成
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("批量消息发送完成");
        } else {
            log.warn("没有找到FAQ记录");
        }
    }
}
