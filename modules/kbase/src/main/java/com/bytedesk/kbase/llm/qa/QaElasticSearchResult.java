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
 
}
