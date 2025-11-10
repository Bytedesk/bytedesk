package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 欢迎消息内容类
 *
 * 使用说明:
 * 1. 服务端在路由策略(Agent/Robot/Workgroup)中构建 WelcomeContent 并序列化为 JSON 写入 MessageEntity.content
 * 2. 前端收到 type=WELCOME 的消息后优先尝试 JSON.parse(content) 解析为该结构；若解析失败则按旧版纯文本展示
 * 3. faqs 字段为精简 FAQ 数据，不依赖复杂对象，避免前端反序列化失败；kbUid 指向关联知识库
 * 4. 兼容：旧历史消息只有 content(纯字符串)，前端自动回退
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WelcomeContent extends BaseContent {

    private static final long serialVersionUID = 1L;
    
    private String content;  // 欢迎消息内容

    @Builder.Default
    private List<QA> faqs = new ArrayList<>(); // 相关常见问题列表

    private String kbUid; // 关联的知识库ID

    /**
     * 内部类：用于在不引入外部依赖的情况下承载FAQ数据
     */
    @Getter
    @Setter
    @lombok.Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QA implements Serializable {
        private static final long serialVersionUID = 1L;

        private String uid;

        private String question;

        private String answer;

        private String type;
    }
    
}
