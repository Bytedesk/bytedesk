/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.skill;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.StreamUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SkillRestService extends BaseRestServiceWithExport<SkillEntity, SkillRequest, SkillResponse, SkillExcel> {

    private static final String INTERNAL_SKILL_RESOURCE_PATTERN = "classpath*:skills/*/SKILL.md";
    private static final String INTERNAL_SKILL_UID_PREFIX = "skill_";
    private static final String EXTERNAL_SKILL_UID_PREFIX = "external_skill_";
    private static final String LEGACY_IMPORTED_SKILL_TYPE = "CUSTOMER";
    private static final Pattern SKILL_DIRECTORY_PATTERN = Pattern.compile(".*/([^/]+)/SKILL\\.md$");

    private final SkillRepository skillRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    private final ResourcePatternResolver resourcePatternResolver;

    private final SkillProperties skillProperties;
    
    @Override
    public Page<SkillEntity> queryByOrgEntity(SkillRequest request) {
        Pageable pageable = request.getPageable();
        Specification<SkillEntity> specs = SkillSpecification.search(request, authService);
        return skillRepository.findAll(specs, pageable);
    }

    @Override
    public Page<SkillResponse> queryByOrg(SkillRequest request) {
        Page<SkillEntity> skillPage = queryByOrgEntity(request);
        return skillPage.map(this::convertToResponse);
    }

    @Override
    public Page<SkillResponse> queryByUser(SkillRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "skill", key = "#uid", unless="#result==null")
    @Override
    public Optional<SkillEntity> findByUid(String uid) {
        return skillRepository.findByUid(uid);
    }

    @Cacheable(value = "skill", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<SkillEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return skillRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return skillRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public SkillResponse create(SkillRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public SkillResponse createSystemSkill(SkillRequest request) {
        return createInternal(request, true);
    }

    private SkillResponse createInternal(SkillRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<SkillEntity> skill = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (skill.isPresent()) {
                return convertToResponse(skill.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(SkillPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        SkillEntity entity = modelMapper.map(request, SkillEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        if (!skipPermissionCheck) {
            entity.setSource(null);
        }
        // 
        SkillEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create skill failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public SkillResponse update(SkillRequest request) {
        Optional<SkillEntity> optional = skillRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SkillEntity entity = optional.get();
            String originalSource = entity.getSource();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(SkillPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            entity.setSource(originalSource);
            //
            SkillEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update skill failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Skill not found");
        }
    }

    @Override
    protected SkillEntity doSave(SkillEntity entity) {
        return skillRepository.save(entity);
    }

    @Override
    public SkillEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SkillEntity entity) {
        try {
            Optional<SkillEntity> latest = skillRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SkillEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return skillRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<SkillEntity> optional = skillRepository.findByUid(uid);
        if (optional.isPresent()) {
            SkillEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(SkillPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // skillRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Skill not found");
        }
    }

    @Override
    public void delete(SkillRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SkillResponse convertToResponse(SkillEntity entity) {
        return modelMapper.map(entity, SkillResponse.class);
    }

    @Override
    public SkillExcel convertToExcel(SkillEntity entity) {
        return modelMapper.map(entity, SkillExcel.class);
    }

    @Override
    protected Specification<SkillEntity> createSpecification(SkillRequest request) {
        return SkillSpecification.search(request, authService);
    }

    @Override
    protected Page<SkillEntity> executePageQuery(Specification<SkillEntity> spec, Pageable pageable) {
        return skillRepository.findAll(spec, pageable);
    }
    
    public void initSkills(String orgUid) {
        for (Resource resource : listInternalSkillResources()) {
            syncInternalSkill(resource, orgUid);
        }
        for (Resource resource : listExternalSkillResources()) {
            syncExternalSkill(resource, orgUid);
        }
    }

    List<Resource> listInternalSkillResources() {
        return listSkillResources(INTERNAL_SKILL_RESOURCE_PATTERN, "internal");
    }

    List<Resource> listExternalSkillResources() {
        if (!StringUtils.hasText(skillProperties.getExternalDirectory())) {
            return List.of();
        }
        return listSkillResources(buildExternalSkillResourcePattern(skillProperties.getExternalDirectory()), "external");
    }

    List<Resource> listAllSkillResources() {
        return Stream.concat(listInternalSkillResources().stream(), listExternalSkillResources().stream())
                .sorted(Comparator.comparing(Resource::getDescription))
                .toList();
    }

    private List<Resource> listSkillResources(String pattern, String sourceLabel) {
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            Arrays.sort(resources, Comparator.comparing(Resource::getDescription));
            return Arrays.asList(resources);
        } catch (IOException exception) {
            log.error("Failed to load {} skills from {}", sourceLabel, pattern, exception);
            return List.of();
        }
    }

    void syncInternalSkill(SkillRequest request, String orgUid, String skillDirectory) {
        syncImportedSkill(request, orgUid, skillDirectory, SkillSourceEnum.INTERNAL);
    }

    private void syncInternalSkill(Resource resource, String orgUid) {
        String skillDirectory = resolveSkillDirectory(resource);
        syncInternalSkill(loadInternalSkillRequest(resource, skillDirectory), orgUid, skillDirectory);
    }

    private void syncExternalSkill(Resource resource, String orgUid) {
        String skillDirectory = resolveSkillDirectory(resource);
        syncImportedSkill(loadInternalSkillRequest(resource, skillDirectory), orgUid, skillDirectory, SkillSourceEnum.EXTERNAL);
    }

    private void syncImportedSkill(SkillRequest request, String orgUid, String skillDirectory, SkillSourceEnum source) {
        if (!isValidInternalSkill(request) || !StringUtils.hasText(skillDirectory)) {
            return;
        }

        String uid = Utils.formatUid(orgUid, getSkillUidPrefix(source) + skillDirectory);
        Optional<SkillEntity> existing = skillRepository.findByUid(uid);
        if (source == SkillSourceEnum.INTERNAL) {
            existing = existing.or(() -> findExistingImportedInternalSkill(request.getName(), orgUid));
        }

        SkillEntity entity = existing.orElseGet(() -> SkillEntity.builder().uid(uid).build());
        entity.setUid(uid);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setType(SkillTypeEnum.GENERAL.name());
        entity.setSource(source.name());
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);
        entity.setOrgUid(orgUid);
        entity.setDeleted(false);
        save(entity);
    }

    private Optional<SkillEntity> findExistingImportedInternalSkill(String name, String orgUid) {
        Optional<SkillEntity> existing = skillRepository.findByNameAndOrgUidAndTypeAndLevelAndDeletedFalse(
                name,
                orgUid,
                SkillTypeEnum.GENERAL.name(),
                LevelEnum.PLATFORM.name());
        if (existing.isPresent()) {
            return existing;
        }
        return skillRepository.findByNameAndOrgUidAndTypeAndLevelAndDeletedFalse(
                name,
                orgUid,
                LEGACY_IMPORTED_SKILL_TYPE,
                LevelEnum.PLATFORM.name());
    }

    private SkillRequest loadInternalSkillRequest(Resource resource, String skillDirectory) {
        try {
            if (!StringUtils.hasText(skillDirectory)) {
                return SkillRequest.builder().build();
            }

            String markdown = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return SkillMarkdownParser.parse(markdown, skillDirectory);
        } catch (IOException exception) {
            log.warn("Failed to read internal skill resource: {}", resource.getDescription(), exception);
            return SkillRequest.builder().build();
        }
    }

    private boolean isValidInternalSkill(SkillRequest request) {
        return request != null
                && StringUtils.hasText(request.getName())
                && StringUtils.hasText(request.getDescription());
    }

    private String resolveSkillDirectory(Resource resource) {
        try {
            String normalizedPath = resource.getURL().toString().replace('\\', '/');
            Matcher matcher = SKILL_DIRECTORY_PATTERN.matcher(normalizedPath);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            log.warn("Unable to resolve skill directory from resource path: {}", normalizedPath);
        } catch (IOException exception) {
            log.warn("Failed to resolve skill directory from resource: {}", resource.getDescription(), exception);
        }
        return null;
    }

    private String getSkillUidPrefix(SkillSourceEnum source) {
        return source == SkillSourceEnum.EXTERNAL ? EXTERNAL_SKILL_UID_PREFIX : INTERNAL_SKILL_UID_PREFIX;
    }

    private String buildExternalSkillResourcePattern(String externalDirectory) {
        String normalizedDirectory = externalDirectory.replace('\\', '/').replaceAll("/+$", "");
        return "file:" + normalizedDirectory + "/*/SKILL.md";
    }

    
    
}
