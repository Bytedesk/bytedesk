package com.bytedesk.ticket.classifier;

import org.springframework.stereotype.Service;
import com.bytedesk.ticket.ticket.TicketEntity;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TicketClassifierImpl implements TicketClassifier {

    // @Autowired
    // private ClassificationRuleRepository ruleRepository;

    @Override
    public void classifyTicket(TicketEntity ticket) {
        // 获取所有启用的规则
        List<ClassificationRuleEntity> rules = null; //ruleRepository.findByEnabledTrueOrderByWeightDesc();
        
        // 计算每个分类的得分
        Map<Long, Double> categoryScores = calculateCategoryScores(ticket, rules);
        
        // 选择最佳分类
        Optional<Map.Entry<Long, Double>> bestCategory = categoryScores.entrySet().stream()
            .max(Map.Entry.comparingByValue());
            
        if (bestCategory.isPresent() && bestCategory.get().getValue() >= 0.7) {
            ticket.setCategoryId(bestCategory.get().getKey());
            
            // 设置优先级
            setPriority(ticket, bestCategory.get().getKey(), rules);
        }
    }

    private Map<Long, Double> calculateCategoryScores(TicketEntity ticket, List<ClassificationRuleEntity> rules) {
        Map<Long, Double> scores = new HashMap<>();
        
        String content = (ticket.getTitle() + " " + ticket.getContent()).toLowerCase();
        
        for (ClassificationRuleEntity rule : rules) {
            double score = 0.0;
            
            // 关键词匹配
            if (rule.getKeywords() != null) {
                score += calculateKeywordScore(content, rule.getKeywords());
            }
            
            // 正则表达式匹配
            if (rule.getRegexPatterns() != null) {
                score += calculateRegexScore(content, rule.getRegexPatterns());
            }
            
            // 应用规则权重
            score *= rule.getWeight();
            
            // 更新分类得分
            scores.merge(rule.getCategoryId(), score, Double::sum);
        }
        
        // 归一化得分
        double maxScore = scores.values().stream().mapToDouble(v -> v).max().orElse(1.0);
        scores.replaceAll((k, v) -> v / maxScore);
        
        return scores;
    }

    private double calculateKeywordScore(String content, String keywords) {
        double score = 0.0;
        String[] keywordArray = keywords.toLowerCase().split(",");
        
        for (String keyword : keywordArray) {
            keyword = keyword.trim();
            if (content.contains(keyword)) {
                // 关键词匹配得分，可以根据关键词长度加权
                score += (double) keyword.length() / 10.0;
            }
        }
        
        return score;
    }

    private double calculateRegexScore(String content, String patterns) {
        double score = 0.0;
        String[] regexArray = patterns.split(",");
        
        for (String regex : regexArray) {
            regex = regex.trim();
            try {
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(content).find()) {
                    // 正则表达式匹配得分，可以设置较高权重
                    score += 2.0;
                }
            } catch (Exception e) {
                log.error("Invalid regex pattern: " + regex, e);
            }
        }
        
        return score;
    }

    private void setPriority(TicketEntity ticket, Long categoryId, List<ClassificationRuleEntity> rules) {
        // 查找匹配分类的规则中优先级最高的
        Optional<String> highestPriority = rules.stream()
            .filter(r -> r.getCategoryId().equals(categoryId) && r.getPriorityLevel() != null)
            .map(ClassificationRuleEntity::getPriorityLevel)
            .max(this::comparePriorities);
            
        highestPriority.ifPresent(ticket::setPriority);
    }

    private int comparePriorities(String p1, String p2) {
        Map<String, Integer> priorityLevels = Map.of(
            "urgent", 3,
            "high", 2,
            "normal", 1,
            "low", 0
        );
        
        return Integer.compare(
            priorityLevels.getOrDefault(p1.toLowerCase(), 0),
            priorityLevels.getOrDefault(p2.toLowerCase(), 0)
        );
    }
} 