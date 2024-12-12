package com.bytedesk.service.quality.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "bytedesk.quality")
public class QualityConfig {

    // 自动质检配置
    private boolean autoInspectionEnabled = true;
    private int autoInspectionDelay = 24;  // 延迟多少小时后执行自动质检
    private int autoInspectionBatchSize = 100;  // 每批处理数量
    
    // 评分配置
    private int minScore = 0;
    private int maxScore = 100;
    private int passScore = 60;  // 及格分数
    
    // 响应时间配置(秒)
    private int fastResponseTime = 30;     // 快速响应时间
    private int normalResponseTime = 120;  // 正常响应时间
    private int slowResponseTime = 300;    // 慢速响应时间
    
    // 解决时间配置(分钟)
    private int fastSolutionTime = 5;      // 快速解决时间
    private int normalSolutionTime = 30;   // 正常解决时间
    private int slowSolutionTime = 120;    // 慢速解决时间
} 