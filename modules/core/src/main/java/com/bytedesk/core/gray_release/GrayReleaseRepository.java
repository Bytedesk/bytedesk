package com.bytedesk.core.gray_release;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GrayReleaseRepository extends JpaRepository<GrayReleaseMetrics, Long> {
    
    // 根据功能和时间范围查询指标
    List<GrayReleaseMetrics> findByFeatureAndTimestampBetween(
            String feature, ZonedDateTime start, ZonedDateTime end);

    // 根据用户和功能查询最近的使用记录
    GrayReleaseMetrics findFirstByUserUidAndFeatureOrderByTimestampDesc(
            String userUid, String feature);

    // 统计功能的使用次数
    @Query("SELECT COUNT(m) FROM GrayReleaseMetrics m WHERE m.feature = ?1 AND m.timestamp BETWEEN ?2 AND ?3")
    long countByFeatureAndTimestampBetween(
            String feature, ZonedDateTime start, ZonedDateTime end);

    // 统计功能的成功次数
    @Query("SELECT COUNT(m) FROM GrayReleaseMetrics m WHERE m.feature = ?1 AND m.success = true AND m.timestamp BETWEEN ?2 AND ?3")
    long countSuccessByFeatureAndTimestampBetween(
            String feature, ZonedDateTime start, ZonedDateTime end);

    // 统计功能的独立用户数
    @Query("SELECT COUNT(DISTINCT m.userUid) FROM GrayReleaseMetrics m WHERE m.feature = ?1 AND m.timestamp BETWEEN ?2 AND ?3")
    long countUniqueUsersByFeatureAndTimestampBetween(
            String feature, ZonedDateTime start, ZonedDateTime end);
} 