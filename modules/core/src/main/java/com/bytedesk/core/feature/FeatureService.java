/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:17:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 10:23:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeatureService {

    @Autowired
    private FeatureRepository featureRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public FeatureEntity registerFeature(String code, String name, String moduleName) {
        if (featureRepository.existsByCode(code)) {
            log.info("Feature already exists: {}", code);
            return featureRepository.findByCode(code);
        }

        FeatureEntity feature = new FeatureEntity();
        feature.setCode(code);
        feature.setName(name);
        feature.setModuleName(moduleName);
        return featureRepository.save(feature);
    }

    
    @Transactional
    public void updateFeatureStatus(String code, boolean enabled) {
        FeatureEntity feature = featureRepository.findByCode(code);
        if (feature != null) {
            feature.setEnabled(enabled);
            featureRepository.save(feature);
        }
    }

    
    public List<FeatureEntity> getEnabledFeatures() {
        return featureRepository.findAllEnabledFeatures();
    }

    
    public List<FeatureEntity> getFeaturesByModule(String moduleName) {
        return featureRepository.findByModuleNameAndEnabledTrue(moduleName);
    }

    
    public Boolean isFeatureEnabled(String code) {
        FeatureEntity feature = featureRepository.findByCode(code);
        return feature != null && feature.getEnabled();
    }

    
    public Map<String, Object> getFeatureConfig(String code) {
        try {
            // FeatureEntity feature = featureRepository.findByCode(code);
            // if (feature != null && feature.getDefaultConfig() != null) {
            //     return objectMapper.readValue(feature.getDefaultConfig(), Map.class);
            // }
        } catch (Exception e) {
            log.error("Failed to parse feature config", e);
        }
        return new HashMap<>();
    }

    
    @Transactional
    public void updateFeatureConfig(String code, Map<String, Object> config) {
        try {
            FeatureEntity feature = featureRepository.findByCode(code);
            if (feature != null) {
                feature.setDefaultConfig(objectMapper.writeValueAsString(config));
                featureRepository.save(feature);
            }
        } catch (Exception e) {
            log.error("Failed to update feature config", e);
        }
    }

    public Map<String, Long> getModuleFeatureStats() {
        List<FeatureEntity> features = featureRepository.findByEnabledTrue();
        return features.stream()
            .collect(Collectors.groupingBy(
                FeatureEntity::getModuleName,
                Collectors.counting()
            ));
    }
} 