package com.bytedesk.kbase.llm.qa;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaElasticSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // QA内容
    private QaElastic qaElastic;
   
    private float score;
    
    // 存储带高亮标记的问题文本
    private String highlightedQuestion;
}
