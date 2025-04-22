package com.bytedesk.kbase.llm.qa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "bytedesk_kbase_llm_qa")
public class QaElastic {
    
    @Id
    private String uid;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String question;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String answer;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private List<String> questionList;
    
    @Field(type = FieldType.Keyword)
    private List<String> tagList;
    
    @Field(type = FieldType.Keyword)
    private String orgUid;
    
    @Field(type = FieldType.Keyword)
    private String kbUid;
    
    @Field(type = FieldType.Keyword)
    private String categoryUid;
    
    @Field(type = FieldType.Boolean)
    private boolean enabled;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
    
    @Field(type = FieldType.Integer)
    private int viewCount;
    
    @Field(type = FieldType.Integer)
    private int clickCount;
    
    @Field(type = FieldType.Integer)
    private int upCount;
    
    @Field(type = FieldType.Integer)
    private int downCount;
    
    // 从QaEntity创建QaElastic的静态方法
    public static QaElastic fromQaEntity(QaEntity qa) {
        String kbUid = "";
        if (qa.getKbaseEntity() != null) {
            kbUid = qa.getKbaseEntity().getUid();
        }
        
        return QaElastic.builder()
            .uid(qa.getUid())
            .question(qa.getQuestion())
            .answer(qa.getAnswer())
            .questionList(qa.getQuestionList())
            .tagList(qa.getTagList())
            .orgUid(qa.getOrgUid())
            .kbUid(kbUid)
            .categoryUid(qa.getCategoryUid())
            .enabled(qa.isEnabled())
            .createdAt(qa.getCreatedAt())
            .updatedAt(qa.getUpdatedAt())
            .viewCount(qa.getViewCount())
            .clickCount(qa.getClickCount())
            .upCount(qa.getUpCount())
            .downCount(qa.getDownCount())
            .build();
    }
}
