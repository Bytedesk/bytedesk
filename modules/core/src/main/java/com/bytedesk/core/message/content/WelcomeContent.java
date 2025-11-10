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
