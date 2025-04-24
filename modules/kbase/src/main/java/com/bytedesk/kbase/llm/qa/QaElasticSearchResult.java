package com.bytedesk.kbase.llm.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaElasticSearchResult {
    
    // QA内容
    private QaElastic qaElastic;
   
    private float score;
    
    // 存储带高亮标记的问题文本
    private String highlightedQuestion;
    
    // 添加一个构造函数，同时设置qaElastic和score
    public QaElasticSearchResult(QaElastic qaElastic, float score) {
        this.qaElastic = qaElastic;
        this.score = score;
    }
}
