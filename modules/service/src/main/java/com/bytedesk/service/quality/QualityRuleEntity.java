package com.bytedesk.service.quality;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_quality_rule")
@EqualsAndHashCode(callSuper = true)
public class QualityRuleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "category_uid")
    private String categoryUid;  // 适用的分类
    
    @Column(name = "min_response_time")
    private Integer minResponseTime;  // 最低响应时间(秒)
    
    @Column(name = "min_solution_time")
    private Integer minSolutionTime;  // 最低解决时间(秒)
    
    @Column(name = "forbidden_words")
    private String forbiddenWords;    // 禁用词,逗号分隔
    
    @Column(name = "required_words")
    private String requiredWords;     // 必用词,逗号分隔
    
    @Column(name = "score_rules", columnDefinition = "TEXT")
    private String scoreRules;        // 评分规则(JSON)
    
    private Boolean enabled = true;
} 