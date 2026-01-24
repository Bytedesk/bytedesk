/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-23 15:27:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Description;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotJsonLoader.Robot;
import com.bytedesk.ai.robot.RobotJsonLoader.RobotConfiguration;
import com.bytedesk.ai.robot_settings.RobotSettingsEntity;
import com.bytedesk.ai.robot_settings.RobotSettingsRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.llm.LlmProviderConfigDefault;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Description("Robot Management Service - AI robot and chatbot management service")
public class RobotRestService extends BaseRestServiceWithExport<RobotEntity, RobotRequest, RobotResponse, RobotExcel> {

    // Cache names (best-practice: separate caches by access pattern for
    // fine-grained eviction)
    private static final String CACHE_ENTITY = "robot:entity";
    private static final String CACHE_RESP = "robot:resp";
    private static final String CACHE_NAME_ORG = "robot:nameOrg";
    private static final String CACHE_EXISTS = "robot:exists";

    private final RobotRepository robotRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    private final RobotJsonLoader robotJsonLoader;

    private final CategoryRestService categoryRestService;

    private final LlmProviderRestService llmProviderRestService;

    private final RobotSettingsRestService robotSettingsRestService;

    // @Cacheable(value = CACHE_ENTITY, key = "'uid_' + #uid", condition = "#uid !=
    // null", unless = "T(org.springframework.util.ObjectUtils).isEmpty(#result)")
    @Override
    public Optional<RobotEntity> findByUid(String uid) {
        return robotRepository.findByUid(uid);
    }

    // 根据名称和组织id查询机器人，并且未删除
    // @Cacheable(value = CACHE_NAME_ORG, key = "'name_org_' + #name + '_' +
    // #orgUid", condition = "#name != null && #orgUid != null", unless =
    // "T(org.springframework.util.ObjectUtils).isEmpty(#result)")
    public Optional<RobotEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid) {
        return robotRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    // @Cacheable(value = CACHE_EXISTS, key = "'exists_' + #uid", condition = "#uid
    // != null", unless = "#result == null")
    public Boolean existsByUid(String uid) {
        return robotRepository.existsByUidAndDeleted(uid, false);
    }

    // @Cacheable(value = CACHE_RESP, key = "'uid_' + #request.uid", condition =
    // "#request != null && #request.uid != null", unless = "#result == null")
    @Override
    public RobotResponse queryByUid(RobotRequest request) {
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot not found by uid: " + request.getUid());
        }
        return convertToResponse(robotOptional.get());
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse create(RobotRequest request) {
        // 如果uid不为空，判断是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            throw new RuntimeException("robot " + request.getUid() + " already exists");
        }
        //
        RobotEntity robot = RobotEntity.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            robot.setUid(request.getUid());
        } else {
            robot.setUid(uidUtils.getUid());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            robot.setAvatar(request.getAvatar());
        } else {
            robot.setAvatar(AvatarConsts.getDefaultRobotAvatar());
        }
        robot.setName(request.getName());
        robot.setNickname(request.getNickname());
        robot.setType(request.getType());
        robot.setOrgUid(request.getOrgUid());
        // robot.setKbEnabled(request.getKbEnabled()); // 后台在faq对话测试时，创建机器人时会用到
        // robot.setKbUid(request.getKbUid()); // 后台在faq对话测试时，创建机器人时会用到

        // 设置配置：若传入 settingsUid 则按 uid 关联，否则使用组织默认配置
        try {
            if (StringUtils.hasText(request.getSettingsUid())) {
                Optional<RobotSettingsEntity> settingsOpt = robotSettingsRestService
                        .findByUid(request.getSettingsUid());
                if (settingsOpt.isPresent()) {
                    robot.setSettings(settingsOpt.get());
                } else {
                    log.warn("create robot: settings not found by uid={}, fallback to default",
                            request.getSettingsUid());
                    robot.setSettings(robotSettingsRestService.getOrCreateDefault(request.getOrgUid()));
                }
            } else {
                robot.setSettings(robotSettingsRestService.getOrCreateDefault(request.getOrgUid()));
            }
        } catch (Exception ex) {
            log.warn("create robot settings resolve failed, fallback to default. orgUid={}, err={}",
                    request.getOrgUid(), ex.getMessage());
            robot.setSettings(robotSettingsRestService.getOrCreateDefault(request.getOrgUid()));
        }

        // LLM 配置
        // 设置llm相关属性
        if (request.getLlm() != null) {
            RobotLlm llm = request.getLlm();
            // Get default model config if not provided
            LlmProviderConfigDefault modelConfig = llmProviderRestService.getLlmProviderConfigDefault();

            // Set default chat provider and model if not provided
            if (!StringUtils.hasText(llm.getTextProvider()) || !StringUtils.hasText(llm.getTextModel())) {
                llm.setTextProvider(
                        llm.getTextProvider() != null ? llm.getTextProvider() : modelConfig.getDefaultChatProvider());
                llm.setTextModel(llm.getTextModel() != null ? llm.getTextModel() : modelConfig.getDefaultChatModel());
            }

            // Set default vision provider and model if not provided
            // if (!StringUtils.hasText(llm.getVisionProvider()) ||
            // !StringUtils.hasText(llm.getVisionModel())) {
            // llm.setVisionProvider(llm.getVisionProvider() != null ?
            // llm.getVisionProvider() : modelConfig.getDefaultVisionProvider());
            // llm.setVisionModel(llm.getVisionModel() != null ? llm.getVisionModel() :
            // modelConfig.getDefaultVisionModel());
            // }

            // Set default voice provider and model if not provided
            if (!StringUtils.hasText(llm.getAudioProvider()) || !StringUtils.hasText(llm.getAudioModel())) {
                llm.setAudioProvider(llm.getAudioProvider() != null ? llm.getAudioProvider()
                        : modelConfig.getDefaultVoiceProvider());
                llm.setAudioModel(
                        llm.getAudioModel() != null ? llm.getAudioModel() : modelConfig.getDefaultVoiceModel());
            }

            // Set default rerank provider and model if not provided
            if (!StringUtils.hasText(llm.getRerankProvider()) || !StringUtils.hasText(llm.getRerankModel())) {
                llm.setRerankProvider(llm.getRerankProvider() != null ? llm.getRerankProvider()
                        : modelConfig.getDefaultRerankProvider());
                llm.setRerankModel(
                        llm.getRerankModel() != null ? llm.getRerankModel() : modelConfig.getDefaultRerankModel());
            }

            robot.setLlm(llm);
        } else {
            RobotLlm robotLlm = RobotLlm.builder().build();
            robot.setLlm(robotLlm);
        }

        //
        RobotEntity updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }
        return convertToResponse(updatedRobot);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse update(RobotRequest request) {

        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        //
        RobotEntity robot = robotOptional.get();
        // robot.setName(request.getName());
        robot.setNickname(request.getNickname());
        robot.setAvatar(request.getAvatar());
        robot.setDescription(request.getDescription());
        // robot.setPublished(request.getPublished());
        robot.setKbSourceEnabled(request.getKbSourceEnabled());
        robot.setKbEnabled(request.getKbEnabled());
        robot.setKbUid(request.getKbUid());

        // 如果传入新的 settingsUid，则更新关联的配置
        if (StringUtils.hasText(request.getSettingsUid())) {
            try {
                Optional<RobotSettingsEntity> settingsOpt = robotSettingsRestService
                        .findByUid(request.getSettingsUid());
                if (settingsOpt.isPresent()) {
                    if (robot.getSettings() == null || !request.getSettingsUid().equals(robot.getSettings().getUid())) {
                        robot.setSettings(settingsOpt.get());
                    }
                } else {
                    log.warn("update robot: settings not found by uid={}, keep original settings",
                            request.getSettingsUid());
                }
            } catch (Exception ex) {
                log.warn("update robot settings resolve failed, uid={}, err={}", request.getSettingsUid(),
                        ex.getMessage());
            }
        }

        // 设置llm相关属性
        if (request.getLlm() != null) {
            robot.setLlm(request.getLlm());
        }

        //
        RobotEntity updateRobot = save(robot);
        if (updateRobot == null) {
            throw new RuntimeException("update robot failed");
        }

        return convertToResponse(updateRobot);
    }

    @Transactional
    public ThreadResponse createLlmThread(ThreadRequest request) {
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw new NotLoginException("login required");
        }
        RobotProtobuf robotProtobuf = RobotProtobuf.fromJson(request.getRobot());
        if (robotProtobuf == null) {
            throw new RuntimeException("robot is required");
        }
        // 因为robotProtobuf中没有name字段，所以前端通过uid传递name
        String robotUid = robotProtobuf.getUid();
        if (!StringUtils.hasText(robotUid)) {
            throw new RuntimeException("robotUid is required");
        }
        //
        Optional<RobotEntity> robotOptional = null;
        String topic = null;
        if (RobotConsts.ROBOT_NAME_AGENT_ASSISTANT.equals(robotUid)) {
            robotOptional = findByNameAndOrgUidAndDeletedFalse(robotUid, owner.getOrgUid());
            // 客服助手 org/robot/robotUid/userUid
            topic = TopicUtils.formatOrgRobotThreadTopic(robotUid, owner.getUid());
        } else {
            robotOptional = findByUid(robotProtobuf.getUid());
            // org/robot/robotUid/userUid/randomUid
            topic = TopicUtils.formatOrgRobotLlmThreadTopic(robotUid, owner.getUid(), uidUtils.getUid());
        }
        // 如果没有强制创建新会话，则尝试获取已存在的会话并返回该会话信息
        if (!request.getForceNew()) {
            Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopicAndOwner(topic, owner);
            if (threadOptional.isPresent()) {
                return threadRestService.convertToResponse(threadOptional.get());
            }
        }
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + robotUid + " not found");
        }
        //
        RobotEntity robotEntity = robotOptional.get();

        // 从 UserProtobuf 中获取昵称和头像，如果没有则使用机器人默认值
        UserProtobuf userProtobuf = request.getUser();
        String userNickname = null;
        String userAvatar = null;
        if (userProtobuf != null) {
            userNickname = StringUtils.hasText(userProtobuf.getNickname())
                    ? userProtobuf.getNickname()
                    : robotEntity.getNickname();
            userAvatar = StringUtils.hasText(userProtobuf.getAvatar())
                    ? userProtobuf.getAvatar()
                    : AvatarConsts.getLlmThreadDefaultAvatar();
        } else {
            userNickname = robotEntity.getNickname();
            userAvatar = AvatarConsts.getLlmThreadDefaultAvatar();
        }

        // 构建 UserProtobuf
        UserProtobuf threadUser = UserProtobuf.builder()
                .uid(robotUid)
                .nickname(userNickname)
                .avatar(userAvatar)
                .build();
        String user = threadUser.toJson();
        String robot = ConvertAiUtils.convertToRobotProtobufString(robotEntity);
        // 创建新的 ThreadEntity 并手动设置属性，而不是使用 ModelMapper
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.LLM.name())
                // .unreadCount(0)
                .hide(request.getHide())
                .user(user)
                .agent(robot)
                .robot(robot)
                .userUid(owner.getUid())
                .owner(owner)
                .level(LevelEnum.ORGANIZATION.name())
                .orgUid(owner.getOrgUid())
                .build();
        //
        ThreadEntity savedThread = threadRestService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return threadRestService.convertToResponse(savedThread);
    }

    @Transactional
    public ThreadResponse updateLlmThread(ThreadRequest request) {
        //
        String topic = request.getTopic();
        RobotProtobuf robotProtobuf = RobotProtobuf.fromJson(request.getAgent());
        //
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        ThreadEntity thread = threadOptional.get();
        //
        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService
                .findByUid(robotProtobuf.getLlm().getTextProviderUid());
        if (!llmProviderOptional.isPresent()) {
            throw new RuntimeException("llm provider not found");
        }
        robotProtobuf.setAvatar(llmProviderOptional.get().getLogo());
        robotProtobuf.setNickname(llmProviderOptional.get().getNickname());
        thread.setAgent(robotProtobuf.toJson());
        thread.setRobot(robotProtobuf.toJson());
        thread.setUser(robotProtobuf.toJson());
        log.info("update thread robot: {}", robotProtobuf.toJson());
        //
        ThreadEntity savedThread = threadRestService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return ConvertUtils.convertToThreadResponse(savedThread);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse updateAvatar(RobotRequest request) {
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        RobotEntity robot = robotOptional.get();
        robot.setAvatar(request.getAvatar());

        RobotEntity updateRobot = save(robot);
        if (updateRobot == null) {
            throw new RuntimeException("update robot failed");
        }
        return convertToResponse(updateRobot);
    }

    @Override
    protected RobotEntity doSave(RobotEntity entity) {
        return robotRepository.save(entity);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, allEntries = true),
            @CacheEvict(value = CACHE_RESP, allEntries = true),
            @CacheEvict(value = CACHE_EXISTS, allEntries = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true)
    })
    public void save(List<RobotEntity> entities) {
        robotRepository.saveAll(entities);
    }

    @Override
    public RobotEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            RobotEntity entity) {
        try {
            Optional<RobotEntity> latest = robotRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RobotEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 根据业务需求进行数据合并
                return robotRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #uid", beforeInvocation = true),
            @CacheEvict(value = CACHE_RESP, key = "'uid_' + #uid", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #uid", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    })
    public void deleteByUid(String uid) {
        Optional<RobotEntity> robotOptional = findByUid(uid);
        robotOptional.ifPresent(robot -> {
            robot.setDeleted(true);
            RobotEntity savedEntity = save(robot);
            if (savedEntity == null) {
                throw new RuntimeException("delete robot failed");
            }
        });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", beforeInvocation = true),
            @CacheEvict(value = CACHE_RESP, key = "'uid_' + #request.uid", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    })
    public void delete(RobotRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected Specification<RobotEntity> createSpecification(RobotRequest request) {
        return RobotSpecification.search(request, authService);
    }

    @Override
    protected Page<RobotEntity> executePageQuery(Specification<RobotEntity> spec, Pageable pageable) {
        return robotRepository.findAll(spec, pageable);
    }

    @Override
    public RobotResponse convertToResponse(RobotEntity entity) {
        return modelMapper.map(entity, RobotResponse.class);
    }

    // 创建一个机器人
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #uid", condition = "#uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #uid", condition = "#uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse createDefaultRobot(String orgUid, String uid) {
        // 判断uid是否已经存在
        if (StringUtils.hasText(uid) && existsByUid(uid)) {
            return convertToResponse(findByUid(uid).get());
        }
        //
        RobotRequest robotRequest = RobotRequest.builder()
                .uid(uid)
                .name(I18Consts.I18N_ROBOT_NAME)
                .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                .type(RobotTypeEnum.SERVICE.name())
                .orgUid(orgUid)
                .build();
        //
        return create(robotRequest);
    }

    // 创建一个空白智能体
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #robotUid", condition = "#robotUid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #robotUid", condition = "#robotUid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse createDefaultPromptRobot(String orgUid, String robotUid) {
        // 判断uid是否已经存在
        if (StringUtils.hasText(robotUid) && existsByUid(robotUid)) {
            return convertToResponse(findByUid(robotUid).get());
        }
        // 使用组织默认 Settings（若不存在则创建）并绑定
        RobotSettingsEntity persistedSettings = robotSettingsRestService.getOrCreateDefault(orgUid);
        //
        RobotEntity robot = RobotEntity.builder()
                .uid(robotUid)
                .name(RobotConsts.ROBOT_NAME_VOID_AGENT)
                .nickname("空白智能体")
                .type(RobotTypeEnum.LLM.name())
                .orgUid(orgUid)
                .settings(persistedSettings)
                .build();
        //
        RobotEntity updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }
        return convertToResponse(updatedRobot);
    }

    public void initRobotJson(String level, String orgUid) {
        RobotConfiguration config = robotJsonLoader.loadRobots();
        List<Robot> robots = config.getRobots();
        for (Robot robotJson : robots) {
            String categoryUid = ensureRobotCategory(level, orgUid, robotJson.getCategory());
            RobotSettingsEntity persistedSettings = robotSettingsRestService.getOrCreateDefault(orgUid);

            // 默认仅初始化简体中文机器人；多语言 Prompt 由 enterprise/ai 的 Prompt 初始化负责
            createRobotIfAbsent(robotJson, level, orgUid, categoryUid, persistedSettings,
                    robotJson.getName(), resolveLocale(robotJson, LanguageEnum.ZH_CN));
        }
    }

    private String ensureRobotCategory(String level, String orgUid, String categoryName) {
        String categoryUid = null;
        Optional<CategoryEntity> categoryOptional = categoryRestService
                .findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(
                        categoryName, CategoryTypeEnum.ROBOT.name(), orgUid, level, BytedeskConsts.PLATFORM_BYTEDESK);
        if (!categoryOptional.isPresent()) {
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(categoryName)
                    .order(0)
                    .level(level)
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .type(CategoryTypeEnum.ROBOT.name())
                    .orgUid(orgUid)
                    .build();
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
            if (categoryResponse != null) {
                categoryUid = categoryResponse.getUid();
            } else {
                throw new RuntimeException("create category failed");
            }
        } else {
            categoryUid = categoryOptional.get().getUid();
        }
        return categoryUid;
    }

    private RobotJsonLoader.LocaleData resolveLocale(Robot robotJson, LanguageEnum language) {
        if (robotJson == null || robotJson.getI18n() == null) {
            return null;
        }
        RobotJsonLoader.LocaleData zhCn = robotJson.getI18n().getZh_cn();
        RobotJsonLoader.LocaleData en = robotJson.getI18n().getEn();
        RobotJsonLoader.LocaleData zhTw = robotJson.getI18n().getZh_tw();

        // 尽量按目标语言取；缺失时做兜底，避免初始化时 NPE
        switch (language) {
            case EN:
                return en != null ? en : (zhCn != null ? zhCn : zhTw);
            case ZH_TW:
                return zhTw != null ? zhTw : (zhCn != null ? zhCn : en);
            case ZH_CN:
            default:
                return zhCn != null ? zhCn : (en != null ? en : zhTw);
        }
    }

    private void createRobotIfAbsent(
            Robot robotJson,
            String level,
            String orgUid,
            String categoryUid,
            RobotSettingsEntity persistedSettings,
            String robotName,
            RobotJsonLoader.LocaleData localeData) {

        if (!StringUtils.hasText(robotName)) {
            return;
        }

        Optional<RobotEntity> robotOptional = findByNameAndOrgUidAndDeletedFalse(robotName, orgUid);
        if (robotOptional.isPresent()) {
            return;
        }

        RobotLlm robotLlm = RobotLlm.builder()
                .prompt(localeData != null ? localeData.getPrompt() : null)
                .build();

        String robotUid = uidUtils.getUid();
        if ("airline_booking_assistant".equals(robotJson.getName())) {
            // External callers rely on a deterministic UID for airline booking tests
            robotUid = BytedeskConsts.DEFAULT_AIRLINE_BOOKING_ASSISTANT_UID;
        }

        // Ensure the chosen UID is unique. If it already exists (including soft-deleted
        // records), generate a new one to avoid unique constraint violations on `uuid`.
        try {
            if (existsByUid(robotUid)) {
                log.warn("robotUid {} already exists, generating a new uid to avoid conflict", robotUid);
                do {
                    robotUid = uidUtils.getUid();
                } while (existsByUid(robotUid));
            }
        } catch (Exception ex) {
            log.warn("failed to check uid existence for {}: {}", robotUid, ex.getMessage());
            robotUid = uidUtils.getUid();
        }

        RobotEntity robot = RobotEntity.builder()
                .uid(robotUid)
                .name(robotName)
                .nickname(localeData != null ? localeData.getNickname() : null)
                .avatar(AvatarConsts.getDefaultRobotAvatar())
                .description(localeData != null ? localeData.getDescription() : null)
                .type(robotJson.getType())
                .llm(robotLlm)
                .categoryUid(categoryUid)
                .level(level)
                .orgUid(orgUid)
                .settings(persistedSettings)
                .system(true)
                .build();

        try {
            save(robot);
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            log.warn("skipping robot creation due to DataIntegrityViolation for uid {}: {}", robotUid,
                    dive.getMessage());
        } catch (Exception ex) {
            log.error("failed to save robot {}: {}", robotUid, ex.getMessage(), ex);
        }
    }

    // 创建智能体机器人（LLM 配置请通过 RobotSettings 进行）
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse createPromptRobot(RobotRequest request) {
        RobotEntity robot = modelMapper.map(request, RobotEntity.class);
        robot.setUid(uidUtils.getUid());
        robot.setType(RobotTypeEnum.LLM.name());
        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("create robot failed");
        }
        return convertToResponse(savedRobot);
    }

    // update prompt robot
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse updatePromptRobot(RobotRequest request) {
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        //
        RobotEntity robot = robotOptional.get();
        // robot = modelMapper.map(request, RobotEntity.class);
        robot.setName(request.getName());
        robot.setNickname(request.getNickname());
        robot.setAvatar(request.getAvatar());
        robot.setDescription(request.getDescription());
        robot.setCategoryUid(request.getCategoryUid());
        // LLM 配置已迁移至 RobotSettings，请更新 RobotSettings 的 draftLlm 并发布
        //
        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("update robot " + request.getUid() + " failed");
        }
        return convertToResponse(savedRobot);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse updatePromptText(RobotRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("robot uid is required");
        }
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new NotFoundException("Robot not found with UID: " + request.getUid());
        }
        String newPrompt = resolvePromptValue(request);
        if (!StringUtils.hasText(newPrompt)) {
            throw new IllegalArgumentException("prompt is required");
        }
        RobotEntity robot = robotOptional.get();
        RobotLlm robotLlm = robot.getLlm();
        if (robotLlm == null) {
            robotLlm = new RobotLlm();
        }
        robotLlm.setPrompt(newPrompt);
        robot.setLlm(robotLlm);
        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("update robot prompt " + request.getUid() + " failed");
        }
        return convertToResponse(savedRobot);
    }

    // 知识库更新请通过 RobotSettings 接口更新 kbEnabled/kbUid
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CACHE_ENTITY, key = "'uid_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_EXISTS, key = "'exists_' + #request.uid", condition = "#request != null && #request.uid != null", beforeInvocation = true),
            @CacheEvict(value = CACHE_NAME_ORG, allEntries = true, beforeInvocation = true)
    }, put = {
            @CachePut(value = CACHE_RESP, key = "'uid_' + #result.uid", unless = "#result == null")
    })
    public RobotResponse updateKbUid(RobotRequest request) {
        // Deprecated path retained for compatibility; use RobotSettings APIs instead.
        log.warn("updateKbUid is deprecated. Please use RobotSettings APIs to update kbEnabled/kbUid.");
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException("robot uid is required");
        }
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new NotFoundException("Robot not found with UID: " + request.getUid());
        }
        robotOptional.get().setKbEnabled(request.getKbEnabled());
        robotOptional.get().setKbUid(request.getKbUid());
        RobotEntity savedEntity = save(robotOptional.get());
        if (savedEntity == null) {
            throw new RuntimeException("update robot kbUid failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public RobotExcel convertToExcel(RobotEntity entity) {
        RobotExcel robotExcel = modelMapper.map(entity, RobotExcel.class);
        try {
            if (entity.getSettings() != null && entity.getLlm() != null) {
                robotExcel.setPrompt(entity.getLlm().getPrompt());
            }
        } catch (Exception ignored) {
        }
        return robotExcel;
    }

    public RobotEntity convertExcelToRobot(RobotExcel excel, String kbType, String fileUid, String kbUid,
            String orgUid) {

        RobotEntity robot = modelMapper.map(excel, RobotEntity.class);
        robot.setUid(uidUtils.getUid());
        robot.setType(RobotTypeEnum.LLM.name());
        robot.setAvatar(AvatarConsts.getDefaultRobotAvatar());
        robot.setOrgUid(orgUid);

        return robot;
    }

    private String resolvePromptValue(RobotRequest request) {
        if (StringUtils.hasText(request.getPrompt())) {
            return request.getPrompt();
        }
        if (request.getLlm() != null && StringUtils.hasText(request.getLlm().getPrompt())) {
            return request.getLlm().getPrompt();
        }
        return null;
    }

}
