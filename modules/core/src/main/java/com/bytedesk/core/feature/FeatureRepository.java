package com.bytedesk.core.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface FeatureRepository extends JpaRepository<FeatureEntity, Long> {
    
    FeatureEntity findByCode(String code);
    
    List<FeatureEntity> findByModuleName(String moduleName);
    
    List<FeatureEntity> findByEnabledTrue();
    
    List<FeatureEntity> findByModuleNameAndEnabledTrue(String moduleName);
    
    @Query("SELECT f FROM FeatureEntity f WHERE f.enabled = true ORDER BY f.moduleName, f.sortOrder")
    List<FeatureEntity> findAllEnabledFeatures();
    
    boolean existsByCode(String code);
} 