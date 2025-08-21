package com.bytedesk.kbase.faq;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRepository;
import com.bytedesk.kbase.llm_faq.FaqRestService;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.mq.FaqIndexConsumer;
import com.bytedesk.kbase.llm_faq.mq.FaqIndexMessage;
import com.bytedesk.kbase.llm_faq.mq.FaqMessageService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ消息队列测试类
 * 包含集成测试和单元测试
 */
@Slf4j
@SpringBootTest
public class FaqMessageQueueTests {
    
    @Autowired
    private FaqMessageService faqMessageService;
    
    @Autowired
    private FaqRepository faqRepository;
    
    /**
     * 测试客户端确认模式的消息队列处理
     */
    @ExtendWith(MockitoExtension.class)
    static class ClientAcknowledgeTests {
        
        @Mock
        private FaqElasticService faqElasticService;
        
        @Mock
        private FaqVectorService faqVectorService;
        
        @Mock
        private FaqRestService faqRestService;
        
        @InjectMocks
        private FaqIndexConsumer faqIndexConsumer;
        
        /**
         * 测试成功处理消息时确认消息
         */
        @Test
        void testMessageAcknowledgmentOnSuccess() throws Exception {
            // 准备测试数据
            String faqUid = "test-faq-uid";
            FaqIndexMessage indexMessage = new FaqIndexMessage(faqUid, "index", true, true);
            Message jmsMessage = mock(Message.class);
            
            // 模拟FAQ存在
            FaqEntity mockFaq = new FaqEntity();
            mockFaq.setUid(faqUid);
            when(faqRestService.findByUid(faqUid)).thenReturn(Optional.of(mockFaq));
            
            // 调用被测试的方法
            faqIndexConsumer.processIndexMessage(jmsMessage, indexMessage);
            
            // 验证消息被确认
            verify(jmsMessage, times(1)).acknowledge();
        }
        
        /**
         * 测试处理失败时不确认消息
         */
        @Test
        void testNoAcknowledgmentOnFailure() throws Exception {
            // 准备测试数据
            String faqUid = "test-faq-uid";
            FaqIndexMessage indexMessage = new FaqIndexMessage(faqUid, "index", true, true);
            Message jmsMessage = mock(Message.class);
            
            // 模拟FAQ存在
            FaqEntity mockFaq = new FaqEntity();
            mockFaq.setUid(faqUid);
            when(faqRestService.findByUid(faqUid)).thenReturn(Optional.of(mockFaq));
            
            // 模拟索引操作抛出异常
            doThrow(new RuntimeException("模拟索引失败")).when(faqElasticService).indexFaq(any());
            
            // 调用被测试的方法
            faqIndexConsumer.processIndexMessage(jmsMessage, indexMessage);
            
            // 验证消息没有被确认
            verify(jmsMessage, never()).acknowledge();
        }
        
        /**
         * 测试确认消息时出现异常的情况
         */
        @Test
        void testAcknowledgmentException() throws Exception {
            // 准备测试数据
            String faqUid = "test-faq-uid";
            FaqIndexMessage indexMessage = new FaqIndexMessage(faqUid, "index", true, false);
            Message jmsMessage = mock(Message.class);
            
            // 模拟FAQ存在
            FaqEntity mockFaq = new FaqEntity();
            mockFaq.setUid(faqUid);
            when(faqRestService.findByUid(faqUid)).thenReturn(Optional.of(mockFaq));
            
            // 模拟确认消息时抛出异常
            doThrow(new JMSException("模拟确认失败")).when(jmsMessage).acknowledge();
            
            // 调用被测试的方法 - 不应抛出异常
            faqIndexConsumer.processIndexMessage(jmsMessage, indexMessage);
            
            // 验证尝试确认消息
            verify(jmsMessage, times(1)).acknowledge();
        }
    }
    
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
