package com.bytedesk.core.message.content;

import java.util.List;

import com.bytedesk.core.base.BaseContent;

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
public class RobotStreamContent extends BaseContent {

    private String question;
    
    private String answer;
    
    // 答案来源信息列表
    private List<SourceReference> sources;
    
    // 用于重新生成答案的上下文信息
    private String regenerationContext;
    
    // 知识库UID，用于重新生成
    private String kbUid;
    
    // 机器人UID，用于重新生成
    private String robotUid;
    
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
