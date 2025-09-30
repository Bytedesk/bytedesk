package com.bytedesk.core.message.content;

import java.util.List;

import com.bytedesk.core.base.BaseContent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Robot流式回答内容
 * 包含问题、答案以及引用来源信息
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StreamContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;

    private String question;
    
    // 用户提问消息的唯一标识，用于前端进行问题与答案的配对
    private String questionUid;
    
    private String answer;
    
    // 推理内容（例如部分模型返回的 reasoningContent）
    private String reasonContent;
    
    // 答案来源信息列表
    private List<SourceReference> sources;
    
    // 用于重新生成答案的上下文信息
    private String regenerationContext;
    
    // 知识库UID，用于重新生成
    private String kbUid;
    
    // 机器人UID，用于重新生成
    private String robotUid;
    
    /**
     * 源引用类型枚举
     * 用于标识AI回答中引用内容的来源类型
     */
    public enum SourceTypeEnum {
        
        FAQ("faq", "常见问题"),
        TEXT("text", "文本内容"),
        CHUNK("chunk", "文档片段"),
        WEBPAGE("webpage", "网页内容"),
        FILE("file", "文件内容"),
        DOCUMENT("document", "文档"),
        ARTICLE("article", "文章"),
        IMAGE("image", "图片"),
        VIDEO("video", "视频"),
        AUDIO("audio", "音频");
        
        private final String code;
        private final String description;
        
        SourceTypeEnum(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        /**
         * 用于 JSON 序列化
         */
        @JsonValue
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * 根据代码获取枚举值，用于 JSON 反序列化
         * @param code 代码
         * @return 对应的枚举值，如果没有找到则返回null
         */
        @JsonCreator
        public static SourceTypeEnum fromCode(String code) {
            if (code == null) {
                return null;
            }
            for (SourceTypeEnum type : SourceTypeEnum.values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            return null;
        }
        
        /**
         * 检查代码是否有效
         * @param code 代码
         * @return 是否有效
         */
        public static boolean isValidCode(String code) {
            return fromCode(code) != null;
        }
        
        @Override
        public String toString() {
            return code;
        }
    }
    /**
     * 来源引用信息
     */
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SourceReference {
        
        // 来源类型
        private SourceTypeEnum sourceType;
        
        // 来源UID
        private String sourceUid;
        
        // 来源标题/名称
        private String sourceName;
        
        // 文件相关信息（当来源为文件时）
        private String fileName;
        private String fileUrl;
        private String fileUid;
        
        // 内容摘要
        private String contentSummary;
        
        // 相似度评分
        private Double score;
        
        // 是否高亮显示
        private Boolean highlighted;
    }
}
