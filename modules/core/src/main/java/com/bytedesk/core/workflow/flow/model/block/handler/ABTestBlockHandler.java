package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ABTestBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.SelectionMode;
import com.bytedesk.core.workflow.flow.model.block.model.options.TestVariant;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ABTestBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private final Random random;
    private final Map<String, AtomicInteger> sequentialCounters;
    private final Map<String, Map<String, TestVariant>> persistentSelections;
    
    public ABTestBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.random = new Random();
        this.sequentialCounters = new ConcurrentHashMap<>();
        this.persistentSelections = new ConcurrentHashMap<>();
    }

    @Override
    public String getType() {
        return BlockType.AB_TEST.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        ABTestBlockOptions options = objectMapper.convertValue(block.getOptions(), ABTestBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            TestVariant selectedVariant;
            String persistedVariantId = (String) context.get(block.getId() + "_variant");
            
            if (persistedVariantId != null && options.isPersistentSelection()) {
                selectedVariant = options.getVariants().stream()
                    .filter(v -> v.getId().equals(persistedVariantId))
                    .findFirst()
                    .orElse(null);
            } else {
                // selectedVariant = selectVariant(
                //     options.getVariants(), 
                //     options.getSelectionMode(), 
                //     block.getId()
                // );
                
                // if (options.isPersistentSelection()) {
                //     result.put(block.getId() + "_variant", selectedVariant.getId());
                // }
            }
            
            // if (selectedVariant != null) {
            //     if (options.getVariableName() != null) {
            //         result.put(options.getVariableName(), selectedVariant.getId());
            //     }
            //     result.put("selectedVariant", selectedVariant);
            //     result.put("success", true);
            // } else {
            //     result.put("error", "No variant selected");
            //     result.put("success", false);
            // }
            
        } catch (Exception e) {
            log.error("A/B test selection failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            ABTestBlockOptions options = objectMapper.convertValue(block.getOptions(), ABTestBlockOptions.class);
            return options.getVariants() != null && 
                   !options.getVariants().isEmpty() &&
                   options.getVariants().stream()
                       .allMatch(v -> v.getGroupId() != null && v.getName() != null);
        } catch (Exception e) {
            return false;
        }
    }

    private TestVariant selectVariant(List<TestVariant> variants, SelectionMode mode, Long seed) {
            if (variants == null || variants.isEmpty()) {
                return null;
            }
            
            switch (mode) {
                case RANDOM:
                    return selectRandomVariant(variants);
                case SEQUENTIAL:
                    return selectSequentialVariant(variants, seed);
            case WEIGHTED:
                return selectWeightedVariant(variants);
            default:
                return selectRandomVariant(variants);
        }
    }

    private TestVariant selectRandomVariant(List<TestVariant> variants) {
        return variants.get(random.nextInt(variants.size()));
    }

    private TestVariant selectWeightedVariant(List<TestVariant> variants) {
        double randomValue = random.nextDouble() * 100;
        double cumulativePercentage = 0;
        
        for (TestVariant variant : variants) {
            cumulativePercentage += variant.getPercentage();
            if (randomValue <= cumulativePercentage) {
                return variant;
            }
        }
        
        return variants.get(variants.size() - 1);
    }
    private TestVariant selectSequentialVariant(List<TestVariant> variants, Long seed) {
        AtomicInteger counter = sequentialCounters.computeIfAbsent(seed.toString(), k -> new AtomicInteger(0));
        int index = counter.getAndIncrement() % variants.size();
        return variants.get(index);
    }

    private boolean validateVariantPercentages(List<TestVariant> variants) {
        if (variants.stream().anyMatch(v -> v.getPercentage() < 0 || v.getPercentage() > 100)) {
            return false;
        }
        
        double totalPercentage = variants.stream()
            .mapToDouble(TestVariant::getPercentage)
            .sum();
            
        return Math.abs(totalPercentage - 100.0) < 0.001;  // 允许0.001的误差
    }

    private void savePersistentSelection(String sessionId, String blockId, TestVariant variant) {
        persistentSelections.computeIfAbsent(sessionId, k -> new HashMap<>())
            .put(blockId, variant);
    }

    private TestVariant getPersistentSelection(String sessionId, String blockId) {
        return persistentSelections.getOrDefault(sessionId, Collections.emptyMap())
            .get(blockId);
    }
} 
