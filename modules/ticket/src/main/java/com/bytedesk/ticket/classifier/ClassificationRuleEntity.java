package com.bytedesk.ticket.classifier;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_classification_rule")
@EqualsAndHashCode(callSuper = true)
public class ClassificationRuleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Column(name = "priority_level")
    private String priorityLevel;  // 优先级
    
    @Column(name = "keywords", length = 1000)
    private String keywords;  // 关键词，逗号分隔
    
    @Column(name = "regex_patterns", length = 1000)
    private String regexPatterns;  // 正则表达式，逗号分隔
    
    @Column(name = "min_confidence")
    private Double minConfidence = 0.7;  // 最小置信度
    
    @Column(name = "weight")
    private Double weight = 1.0;  // 规则权重
    
    private Boolean enabled = true;
} 