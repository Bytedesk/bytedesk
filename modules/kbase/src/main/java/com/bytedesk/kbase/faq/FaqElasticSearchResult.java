package com.bytedesk.kbase.faq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqElasticSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // QA内容
    private FaqElastic faqElastic;
   
    private float score;
    
    // 存储带高亮标记的问题文本
    private String highlightedQuestion;
}
