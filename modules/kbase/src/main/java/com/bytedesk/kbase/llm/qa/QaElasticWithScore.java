package com.bytedesk.kbase.llm.qa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaElasticWithScore {
    // QA内容
    private QaElastic qaElastic;
    // 搜索得分
    private float score;
    
    // 构造函数
    public QaElasticWithScore(QaElastic qaElastic, float score) {
        this.qaElastic = qaElastic;
        this.score = score;
    }
}
