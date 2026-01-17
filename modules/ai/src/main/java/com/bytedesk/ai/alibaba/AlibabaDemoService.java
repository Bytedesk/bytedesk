package com.bytedesk.ai.alibaba;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.bytedesk.ai.alibaba.json_object.ContactInfo;

@Service
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaDemoService {

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatModel")
    private ChatModel bytedeskDashscopeChatModel;

    public String getContactSchema(String format) {

        ReactAgent agent = ReactAgent.builder()
                .name("contact_extractor")
                .model(bytedeskDashscopeChatModel)
                .outputSchema(format)
                .build();

        AssistantMessage result;
        try {
            result = agent.call(
                    "从以下信息提取联系方式：张三，zhangsan@example.com，(555) 123-4567");
            return result.getText();
        } catch (GraphRunnerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public String getContactType() {

        // 直接使用 outputType，框架会自动处理 schema 转换
        ReactAgent agent = ReactAgent.builder()
                .name("contact_extractor")
                .model(bytedeskDashscopeChatModel)
                .outputType(ContactInfo.class) // [!code highlight]
                .saver(new MemorySaver())
                .build();

        try {
            AssistantMessage result = agent.call(
                    "从以下信息提取联系方式：张三，zhangsan@example.com，(555) 123-4567");
            return result.getText();
        } catch (GraphRunnerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public String getProductReviewSchema(String format) {
        ReactAgent agent = ReactAgent.builder()
                .name("review_analyzer")
                .model(bytedeskDashscopeChatModel)
                .outputSchema(format)
                .build();

        AssistantMessage result;
        try {
            result = agent.call(
                    "分析评价：这个产品很棒，5星好评。配送快速，但价格稍贵。");
            return result.getText();
        } catch (GraphRunnerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public String getTextAnalysisSchema(String format) {
        ReactAgent agent = ReactAgent.builder()
                .name("text_analyzer")
                .model(bytedeskDashscopeChatModel)
                .outputSchema(format)
                .build();

        try {
            AssistantMessage result = agent.call(
                    "分析这段文字：昨天，李明在北京参加了阿里巴巴公司的技术大会，感受到了创新的力量。");
            return result.getText();
        } catch (GraphRunnerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
