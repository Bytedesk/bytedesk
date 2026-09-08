package com.bytedesk.ai.robot.settings;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotRoutingSettingsService {

    public record WorkgroupAutoReplyConfig(Boolean autoReplyEnabled, String autoReplyKbUid) {
    }

    private final JdbcTemplate jdbcTemplate;

    private final RobotRoutingSettingsRepository robotRoutingSettingsRepository;

    @Cacheable(cacheNames = "workgroupAutoReplyConfig", key = "#workgroupUid", unless = "#result == null")
    public WorkgroupAutoReplyConfig findByWorkgroupUid(String workgroupUid) {
        if (!StringUtils.hasText(workgroupUid)) {
            return null;
        }

        String sql = """
                    SELECT rrs.auto_reply_enabled AS autoReplyEnabled,
                             NULLIF(rrs.auto_reply_kb_uid, '') AS autoReplyKbUid
                FROM bytedesk_service_workgroup wg
                LEFT JOIN bytedesk_service_workgroup_settings ws ON ws.id = wg.settings_id
                LEFT JOIN bytedesk_ai_robot_routing_settings rrs ON rrs.id = ws.robot_settings_id
                WHERE wg.uuid = ? AND wg.is_deleted = 0
                """;

        try {
            return jdbcTemplate.query(sql, rs -> {
                if (!rs.next()) {
                    return null;
                }
                return new WorkgroupAutoReplyConfig(
                        rs.getBoolean("autoReplyEnabled"),
                        rs.getString("autoReplyKbUid"));
            }, workgroupUid);
        } catch (Exception ex) {
            log.warn("failed to query pre-match config for workgroupUid={}", workgroupUid, ex);
            return null;
        }
    }

    @CacheEvict(cacheNames = "workgroupAutoReplyConfig", key = "#workgroupUid")
    public void evictByWorkgroupUid(String workgroupUid) {
        // cache eviction is handled by annotation
    }

    /**
     * 全量关闭机器人路由开关（defaultRobot/offlineRobot/nonWorktimeRobot 全部置为 false）。
     * 用于 "/ai" 菜单隐藏后，停用所有工作组/技能组的机器人接待策略。
     *
     * @return 实际更新的行数
     */
    @Transactional
    public int disableAllRobotRouting() {
        int updated = robotRoutingSettingsRepository.disableAllRobotRouting();
        if (updated > 0) {
            log.info("disableAllRobotRouting: {} robot routing settings reset to false", updated);
        }
        return updated;
    }
}
