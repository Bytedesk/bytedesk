package com.bytedesk.freeswitch.service;

import com.bytedesk.freeswitch.model.FreeSwitchConferenceEntity;
import com.bytedesk.freeswitch.repository.FreeSwitchConferenceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * FreeSwitch会议服务类
 * 处理会议室创建、管理、成员控制等业务逻辑
 */
@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchConferenceService {

    private final FreeSwitchConferenceRepository conferenceRepository;

    /**
     * 创建新会议室
     */
    @Transactional
    public FreeSwitchConferenceEntity createConference(String name, String description, 
                                                     String moderatorPin, String memberPin, 
                                                     Integer maxMembers) {
        FreeSwitchConferenceEntity conference = new FreeSwitchConferenceEntity();
        conference.setName(name);
        conference.setDescription(description);
        conference.setModeratorPin(moderatorPin);
        conference.setMemberPin(memberPin);
        conference.setMaxMembers(maxMembers);
        conference.setActive(true);
        conference.setCurrentMembers(0);
        
        FreeSwitchConferenceEntity saved = conferenceRepository.save(conference);
        log.info("创建会议室: {} (ID: {})", name, saved.getId());
        return saved;
    }

    /**
     * 根据ID查找会议室
     */
    public Optional<FreeSwitchConferenceEntity> findById(Long id) {
        return conferenceRepository.findById(id);
    }

    /**
     * 根据名称查找会议室
     */
    public Optional<FreeSwitchConferenceEntity> findByName(String name) {
        return conferenceRepository.findByName(name);
    }

    /**
     * 验证会议室访问权限
     */
    public boolean validateAccess(String conferenceName, String pin, boolean isModerator) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findByName(conferenceName);
        if (conferenceOpt.isEmpty()) {
            log.warn("会议室不存在: {}", conferenceName);
            return false;
        }

        FreeSwitchConferenceEntity conference = conferenceOpt.get();
        if (!conference.getActive()) {
            log.warn("会议室未激活: {}", conferenceName);
            return false;
        }

        String expectedPin = isModerator ? conference.getModeratorPin() : conference.getMemberPin();
        boolean accessGranted = pin != null && pin.equals(expectedPin);
        
        if (accessGranted) {
            log.info("会议室访问验证成功: {} (角色: {})", conferenceName, isModerator ? "主持人" : "成员");
        } else {
            log.warn("会议室访问验证失败: {} (角色: {})", conferenceName, isModerator ? "主持人" : "成员");
        }
        
        return accessGranted;
    }

    /**
     * 成员加入会议室
     */
    @Transactional
    public boolean joinConference(String conferenceName, String memberUuid) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findByName(conferenceName);
        if (conferenceOpt.isEmpty()) {
            return false;
        }

        FreeSwitchConferenceEntity conference = conferenceOpt.get();
        
        // 检查是否达到最大成员数
        if (conference.getMaxMembers() != null && 
            conference.getCurrentMembers() >= conference.getMaxMembers()) {
            log.warn("会议室已满: {} (当前: {}/{})", conferenceName, 
                    conference.getCurrentMembers(), conference.getMaxMembers());
            return false;
        }

        conference.setCurrentMembers(conference.getCurrentMembers() + 1);
        conference.setLastActivityTime(LocalDateTime.now());
        conferenceRepository.save(conference);
        
        log.info("成员加入会议室: {} (UUID: {}, 当前成员数: {})", 
                conferenceName, memberUuid, conference.getCurrentMembers());
        return true;
    }

    /**
     * 成员离开会议室
     */
    @Transactional
    public void leaveConference(String conferenceName, String memberUuid) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findByName(conferenceName);
        if (conferenceOpt.isEmpty()) {
            return;
        }

        FreeSwitchConferenceEntity conference = conferenceOpt.get();
        if (conference.getCurrentMembers() > 0) {
            conference.setCurrentMembers(conference.getCurrentMembers() - 1);
            conference.setLastActivityTime(LocalDateTime.now());
            conferenceRepository.save(conference);
            
            log.info("成员离开会议室: {} (UUID: {}, 当前成员数: {})", 
                    conferenceName, memberUuid, conference.getCurrentMembers());
        }
    }

    /**
     * 获取活跃会议室列表
     */
    public List<FreeSwitchConferenceEntity> getActiveConferences() {
        return conferenceRepository.findByActiveTrue();
    }

    /**
     * 搜索会议室
     */
    public Page<FreeSwitchConferenceEntity> searchConferences(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return conferenceRepository.findAll(pageable);
        }
        return conferenceRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable);
    }

    /**
     * 根据最大成员数过滤会议室
     */
    public List<FreeSwitchConferenceEntity> findByMaxMembersGreaterThanEqual(Integer minMaxMembers) {
        return conferenceRepository.findByMaxMembersGreaterThanEqual(minMaxMembers);
    }

    /**
     * 获取当前有成员的会议室
     */
    public List<FreeSwitchConferenceEntity> getConferencesWithMembers() {
        return conferenceRepository.findByCurrentMembersGreaterThan(0);
    }

    /**
     * 更新会议室信息
     */
    @Transactional
    public FreeSwitchConferenceEntity updateConference(Long id, String name, String description,
                                                     String moderatorPin, String memberPin,
                                                     Integer maxMembers, Boolean active) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
        if (conferenceOpt.isEmpty()) {
            throw new RuntimeException("会议室不存在: " + id);
        }

        FreeSwitchConferenceEntity conference = conferenceOpt.get();
        if (name != null) conference.setName(name);
        if (description != null) conference.setDescription(description);
        if (moderatorPin != null) conference.setModeratorPin(moderatorPin);
        if (memberPin != null) conference.setMemberPin(memberPin);
        if (maxMembers != null) conference.setMaxMembers(maxMembers);
        if (active != null) conference.setActive(active);

        FreeSwitchConferenceEntity updated = conferenceRepository.save(conference);
        log.info("更新会议室: {} (ID: {})", conference.getName(), id);
        return updated;
    }

    /**
     * 删除会议室
     */
    @Transactional
    public void deleteConference(Long id) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
        if (conferenceOpt.isPresent()) {
            FreeSwitchConferenceEntity conference = conferenceOpt.get();
            log.info("删除会议室: {} (ID: {})", conference.getName(), id);
            conferenceRepository.deleteById(id);
        }
    }

    /**
     * 激活/停用会议室
     */
    @Transactional
    public void toggleConferenceStatus(Long id) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
        if (conferenceOpt.isPresent()) {
            FreeSwitchConferenceEntity conference = conferenceOpt.get();
            conference.setActive(!conference.getActive());
            conferenceRepository.save(conference);
            log.info("切换会议室状态: {} -> {}", conference.getName(), 
                    conference.getActive() ? "激活" : "停用");
        }
    }

    /**
     * 重置会议室成员数（用于故障恢复）
     */
    @Transactional
    public void resetMemberCount(Long id) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
        if (conferenceOpt.isPresent()) {
            FreeSwitchConferenceEntity conference = conferenceOpt.get();
            conference.setCurrentMembers(0);
            conferenceRepository.save(conference);
            log.info("重置会议室成员数: {}", conference.getName());
        }
    }

    /**
     * 获取会议室统计信息
     */
    public ConferenceStatistics getStatistics() {
        long totalConferences = conferenceRepository.count();
        long activeConferences = conferenceRepository.countByActiveTrue();
        long conferencesWithMembers = conferenceRepository.countByCurrentMembersGreaterThan(0);
        
        return new ConferenceStatistics(totalConferences, activeConferences, conferencesWithMembers);
    }

    /**
     * 会议室统计信息数据类
     */
    public static class ConferenceStatistics {
        private final long totalConferences;
        private final long activeConferences;
        private final long conferencesWithMembers;

        public ConferenceStatistics(long totalConferences, long activeConferences, long conferencesWithMembers) {
            this.totalConferences = totalConferences;
            this.activeConferences = activeConferences;
            this.conferencesWithMembers = conferencesWithMembers;
        }

        public long getTotalConferences() { return totalConferences; }
        public long getActiveConferences() { return activeConferences; }
        public long getConferencesWithMembers() { return conferencesWithMembers; }
    }
}
