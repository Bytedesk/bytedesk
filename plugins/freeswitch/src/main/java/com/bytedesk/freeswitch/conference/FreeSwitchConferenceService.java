/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.conference;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public FreeSwitchConferenceEntity createConference(String conferenceName, String description, 
                                                     String password, 
                                                     Integer maxMembers) {
        FreeSwitchConferenceEntity conference = new FreeSwitchConferenceEntity();
        conference.setConferenceName(conferenceName);
        conference.setDescription(description);
        conference.setPassword(password);
        conference.setMaxMembers(maxMembers);
        conference.setEnabled(true);
        conference.setRecordEnabled(false);
        
        FreeSwitchConferenceEntity saved = conferenceRepository.save(conference);
        log.info("创建会议室: {} (ID: {})", conferenceName, saved.getId());
        return saved;
    }

    /**
     * 根据ID查找会议室
     */
    public Optional<FreeSwitchConferenceEntity> findById(Long id) {
        return conferenceRepository.findById(id);
    }

    /**
     * 根据会议室名称查找会议室
     */
    public Optional<FreeSwitchConferenceEntity> findByConferenceName(String conferenceName) {
        return conferenceRepository.findByConferenceName(conferenceName);
    }

    /**
     * 验证会议室访问权限
     */
    public boolean validateAccess(String conferenceName, String password) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findByConferenceName(conferenceName);
        if (conferenceOpt.isEmpty()) {
            log.warn("会议室不存在: {}", conferenceName);
            return false;
        }

        FreeSwitchConferenceEntity conference = conferenceOpt.get();
        if (!conference.getEnabled()) {
            log.warn("会议室未启用: {}", conferenceName);
            return false;
        }

        // 检查是否需要密码
        if (conference.isPasswordProtected()) {
            boolean accessGranted = password != null && password.equals(conference.getPassword());
            if (accessGranted) {
                log.info("会议室密码验证成功: {}", conferenceName);
            } else {
                log.warn("会议室密码验证失败: {}", conferenceName);
            }
            return accessGranted;
        } else {
            // 无密码保护的会议室
            log.info("访问公开会议室: {}", conferenceName);
            return true;
        }
    }

    /**
     * 更新会议室信息
     */
    @Transactional
    public FreeSwitchConferenceEntity updateConference(Long id, String conferenceName, String description, 
                                                      String password, Integer maxMembers, Boolean recordEnabled) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
        if (conferenceOpt.isEmpty()) {
            throw new RuntimeException("会议室不存在: " + id);
        }

        FreeSwitchConferenceEntity conference = conferenceOpt.get();
        if (conferenceName != null) conference.setConferenceName(conferenceName);
        if (description != null) conference.setDescription(description);
        if (password != null) conference.setPassword(password);
        if (maxMembers != null) conference.setMaxMembers(maxMembers);
        if (recordEnabled != null) conference.setRecordEnabled(recordEnabled);

        FreeSwitchConferenceEntity saved = conferenceRepository.save(conference);
        log.info("更新会议室: {} (ID: {})", conference.getConferenceName(), id);
        return saved;
    }

    /**
     * 启用/禁用会议室
     */
    @Transactional
    public boolean toggleConferenceStatus(Long id) {
        Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
        if (conferenceOpt.isPresent()) {
            FreeSwitchConferenceEntity conference = conferenceOpt.get();
            conference.setEnabled(!conference.getEnabled());
            conferenceRepository.save(conference);
            log.info("切换会议室状态: {} -> {}", conference.getConferenceName(), conference.getEnabled() ? "启用" : "禁用");
            return true;
        }
        return false;
    }

    /**
     * 删除会议室
     */
    @Transactional
    public boolean deleteConference(Long id) {
        if (conferenceRepository.existsById(id)) {
            Optional<FreeSwitchConferenceEntity> conferenceOpt = conferenceRepository.findById(id);
            conferenceRepository.deleteById(id);
            if (conferenceOpt.isPresent()) {
                log.info("删除会议室: {} (ID: {})", conferenceOpt.get().getConferenceName(), id);
            }
            return true;
        }
        return false;
    }

    /**
     * 获取所有会议室（分页）
     */
    public Page<FreeSwitchConferenceEntity> getAllConferences(Pageable pageable) {
        return conferenceRepository.findAll(pageable);
    }

    /**
     * 搜索会议室
     */
    public Page<FreeSwitchConferenceEntity> searchConferences(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return conferenceRepository.findAll(pageable);
        } else {
            return conferenceRepository.findByConferenceNameContainingIgnoreCase(keyword.trim(), pageable);
        }
    }

    /**
     * 获取启用的会议室列表
     */
    public List<FreeSwitchConferenceEntity> getEnabledConferences() {
        return conferenceRepository.findByEnabledTrue();
    }

    /**
     * 获取会议室统计信息
     */
    public ConferenceStatistics getStatistics() {
        long totalConferences = conferenceRepository.count();
        long enabledConferences = conferenceRepository.countByEnabledTrue();
        long recordEnabledConferences = conferenceRepository.countByRecordEnabledTrue();

        return ConferenceStatistics.builder()
                .totalConferences(totalConferences)
                .enabledConferences(enabledConferences)
                .disabledConferences(totalConferences - enabledConferences)
                .recordEnabledConferences(recordEnabledConferences)
                .build();
    }

    /**
     * 检查会议室名称是否存在
     */
    public boolean existsByConferenceName(String conferenceName) {
        return conferenceRepository.existsByConferenceName(conferenceName);
    }

    /**
     * 获取公开会议室（无密码保护）
     */
    public List<FreeSwitchConferenceEntity> getPublicConferences() {
        return conferenceRepository.findPublicConferences();
    }

    /**
     * 获取密码保护的会议室
     */
    public List<FreeSwitchConferenceEntity> getPasswordProtectedConferences() {
        return conferenceRepository.findPasswordProtectedConferences();
    }

    /**
     * 会议室统计信息类
     */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ConferenceStatistics {
        private long totalConferences;
        private long enabledConferences;
        private long disabledConferences;
        private long recordEnabledConferences;
    }
}
