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
package com.bytedesk.freeswitch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bytedesk.freeswitch.model.FreeSwitchConferenceEntity;

/**
 * FreeSwitch会议室仓库接口
 */
@Repository
public interface FreeSwitchConferenceRepository extends JpaRepository<FreeSwitchConferenceEntity, Long>, 
        JpaSpecificationExecutor<FreeSwitchConferenceEntity> {

    /**
     * 根据会议室名称查找会议室
     */
    Optional<FreeSwitchConferenceEntity> findByConferenceName(String conferenceName);

    /**
     * 查找启用的会议室
     */
    List<FreeSwitchConferenceEntity> findByEnabledTrue();

    /**
     * 查找禁用的会议室
     */
    List<FreeSwitchConferenceEntity> findByEnabledFalse();

    /**
     * 根据创建者查找会议室
     */
    List<FreeSwitchConferenceEntity> findByCreator(String creator);

    /**
     * 查找启用录音的会议室
     */
    List<FreeSwitchConferenceEntity> findByRecordEnabledTrue();

    /**
     * 根据会议室名称模糊搜索
     */
    Page<FreeSwitchConferenceEntity> findByConferenceNameContainingIgnoreCase(String conferenceName, Pageable pageable);

    /**
     * 根据描述模糊搜索
     */
    Page<FreeSwitchConferenceEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * 检查会议室名称是否存在
     */
    boolean existsByConferenceName(String conferenceName);

    /**
     * 查找有密码保护的会议室
     */
    @Query("SELECT c FROM FreeSwitchConferenceEntity c WHERE c.password IS NOT NULL AND c.password != ''")
    List<FreeSwitchConferenceEntity> findPasswordProtectedConferences();

    /**
     * 查找无密码保护的会议室
     */
    @Query("SELECT c FROM FreeSwitchConferenceEntity c WHERE c.password IS NULL OR c.password = ''")
    List<FreeSwitchConferenceEntity> findPublicConferences();

    /**
     * 根据最大成员数量范围查找会议室
     */
    List<FreeSwitchConferenceEntity> findByMaxMembersBetween(Integer minMembers, Integer maxMembers);

    /**
     * 统计启用的会议室数量
     */
    long countByEnabledTrue();

    /**
     * 统计启用录音的会议室数量
     */
    long countByRecordEnabledTrue();

    /**
     * 根据创建者统计会议室数量
     */
    long countByCreator(String creator);

    /**
     * 查找指定创建者的启用会议室
     */
    List<FreeSwitchConferenceEntity> findByCreatorAndEnabledTrue(String creator);

    /**
     * 查找有最大成员限制的会议室
     */
    @Query("SELECT c FROM FreeSwitchConferenceEntity c WHERE c.maxMembers IS NOT NULL AND c.maxMembers > 0")
    List<FreeSwitchConferenceEntity> findConferencesWithMemberLimit();

    /**
     * 查找无成员限制的会议室
     */
    @Query("SELECT c FROM FreeSwitchConferenceEntity c WHERE c.maxMembers IS NULL OR c.maxMembers <= 0")
    List<FreeSwitchConferenceEntity> findConferencesWithoutMemberLimit();
}
