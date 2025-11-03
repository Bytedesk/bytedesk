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
import org.springframework.cache.annotation.Cacheable;
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
import com.bytedesk.ai.robot_settings.RobotLlmEntity;
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
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.user.UserEntity;
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

    private final RobotRepository robotRepository;

    // private final FaqRestService faqRestService; // No longer needed after
    // settings migration

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    private final RobotJsonLoader robotJsonLoader;

    private final CategoryRestService categoryRestService;

    private final LlmProviderRestService llmProviderRestService;

    private final RobotSettingsRestService robotSettingsRestService;

    @Override
    protected Specification<RobotEntity> createSpecification(RobotRequest request) {
        return RobotSpecification.search(request, authService);
    }

    @Override
    protected Page<RobotEntity> executePageQuery(Specification<RobotEntity> spec, Pageable pageable) {
        return robotRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "robot", key = "#uid", unless = "#result == null")
    @Override
    public Optional<RobotEntity> findByUid(String uid) {
        return robotRepository.findByUid(uid);
    }

    // 根据名称和组织id查询机器人，并且未删除
    @Cacheable(value = "robot", key = "#name + '_' + #orgUid", unless = "#result == null")
    public Optional<RobotEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid) {
        return robotRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    @Cacheable(value = "robot", key = "#uid", unless = "#result == null")
    public Boolean existsByUid(String uid) {
        return robotRepository.existsByUidAndDeleted(uid, false);
    }

    @Cacheable(value = "robot", key = "#name + '_' + #uid", unless = "#result == null")
    @Override
    public RobotResponse queryByUid(RobotRequest request) {
        Optional<RobotEntity> robotOptional = robotRepository.findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot not found by uid: " + request.getUid());
        }
        return convertToResponse(robotOptional.get());
    }

    @Transactional
    @Override
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
        robot.setName(request.getName());
        robot.setNickname(request.getNickname());
        robot.setType(request.getType());
        robot.setOrgUid(request.getOrgUid());
        // robot.setKbEnabled(request.getKbEnabled()); // 后台在faq对话测试时，创建机器人时会用到
        // robot.setKbUid(request.getKbUid()); // 后台在faq对话测试时，创建机器人时会用到
        //
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

        // LLM 配置已迁移至 RobotSettings，不再从 RobotRequest 中设置
        // Set common settings
        // TODO: Settings should be managed through RobotSettingsEntity, not directly in
        // RobotRequest
        // setRobotSettings(robot, request);
        //
        RobotEntity updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }
        return convertToResponse(updatedRobot);
    }

    @Transactional
    @Override
    public RobotResponse update(RobotRequest request) {

        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        //
        RobotEntity robot = robotOptional.get();
        robot.setName(request.getName());
        robot.setNickname(request.getNickname());
        robot.setAvatar(request.getAvatar());
        robot.setDescription(request.getDescription());
        // robot.setPublished(request.getPublished());

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

        // LLM 配置已迁移至 RobotSettings，不再从 RobotRequest 中设置
        //
        // Set common settings
        // TODO: Settings should be managed through RobotSettingsEntity, not directly in
        // RobotRequest
        // setRobotSettings(robot, request);
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
        String robotName = robotProtobuf.getUid();
        if (!StringUtils.hasText(robotName)) {
            throw new RuntimeException("robotUid is required");
        }
        Optional<RobotEntity> robotOptional = findByNameAndOrgUidAndDeletedFalse(robotName, owner.getOrgUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + robotName + " not found");
        }
        String robotUid = robotOptional.get().getUid();
        //
        String topic = null;
        if (RobotConsts.ROBOT_NAME_AGENT_ASSISTANT.equals(robotUid)) {
            // org/robot/robotUid/userUid
            topic = TopicUtils.formatOrgRobotThreadTopic(robotUid, owner.getUid());
        } else {
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
        //
        RobotEntity robotEntity = robotOptional.get();
        robotEntity.setAvatar(AvatarConsts.getLlmThreadDefaultAvatar());
        String user = ConvertAiUtils.convertToUserProtobufString(robotEntity);
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

    // update avatar
    @Transactional
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
    public void delete(RobotRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RobotResponse convertToResponse(RobotEntity entity) {
        return modelMapper.map(entity, RobotResponse.class);
    }

    // 初始化
    public void initDefaultRobot(String orgUid, String uid) {
        // 为每个组织创建一个机器人
        createDefaultRobot(orgUid, uid);
        // 为每个组织创建一个空白智能体，已经在 initRobotJson 中创建
        // createDefaultPromptRobot(orgUid, Utils.formatUid(orgUid,
        // RobotConsts.ROBOT_NAME_VOID_AGENT));
    }

    // 创建一个机器人
    @Transactional
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
        // TODO: Service settings should be managed through RobotSettingsEntity
        // robotRequest.getServiceSettings().setShowFaqs(true);
        // robotRequest.getServiceSettings().setShowQuickFaqs(true);
        // robotRequest.getServiceSettings().setShowGuessFaqs(true);
        // robotRequest.getServiceSettings().setShowHotFaqs(true);
        // robotRequest.getServiceSettings().setShowShortcutFaqs(true);
        //
        // 写入 faq uid 到 welcomeFaqUids
        // if (orgUid.equals(BytedeskConsts.DEFAULT_ORGANIZATION_UID)) {
        // // 将 faq_001 ~ faq_005 写入到 welcomeFaqUids
        // for (int i = 1; i <= 5; i++) {
        // String faqUid = Utils.formatUid(orgUid, "faq_00" + i);
        // robotRequest.getServiceSettings().getWelcomeFaqUids().add(faqUid);
        // }
        // }
        //
        return create(robotRequest);
    }

    // 创建一个空白智能体
    @Transactional
    public RobotResponse createDefaultPromptRobot(String orgUid, String robotUid) {
        // 判断uid是否已经存在
        if (StringUtils.hasText(robotUid) && existsByUid(robotUid)) {
            return convertToResponse(findByUid(robotUid).get());
        }
        // 使用组织默认 Settings（若不存在则创建）并绑定
        RobotSettingsEntity persistedSettings = robotSettingsRestService.getOrCreateDefault(orgUid);
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
            // 使用name代替uid，方便查询和创建机器人
            String name = robotJson.getName();
            Optional<RobotEntity> robotOptional = findByNameAndOrgUidAndDeletedFalse(name, orgUid);
            if (!robotOptional.isPresent()) {
                String categoryUid = null;
                Optional<CategoryEntity> categoryOptional = categoryRestService
                        .findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(
                                robotJson.getCategory(), CategoryTypeEnum.ROBOT.name(), orgUid, level,
                                BytedeskConsts.PLATFORM_BYTEDESK);
                if (!categoryOptional.isPresent()) {
                    CategoryRequest categoryRequest = CategoryRequest.builder()
                            .name(robotJson.getCategory())
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
                // Get locale data (default to zh_cn if available, fallback to en)
                RobotJsonLoader.LocaleData localeData = robotJson.getI18n().getZh_cn() != null
                        ? robotJson.getI18n().getZh_cn()
                        : robotJson.getI18n().getEn();

                // Use organization default settings (ignore per-robot custom prompt)
                RobotSettingsEntity persistedSettings = robotSettingsRestService.getOrCreateDefault(orgUid);
                // Create or reuse published LLM settings and ensure uid is assigned
                RobotLlmEntity llmSettings = persistedSettings.getLlm() != null
                        ? persistedSettings.getLlm()
                        : RobotLlmEntity.builder().uid(uidUtils.getUid()).build();
                if (llmSettings.getUid() == null || llmSettings.getUid().isEmpty()) {
                    llmSettings.setUid(uidUtils.getUid());
                }
                llmSettings.setPrompt(localeData.getPrompt());
                persistedSettings.setLlm(llmSettings);
                // Create or reuse draft LLM settings and ensure uid is assigned
                RobotLlmEntity draftLlmSettings = persistedSettings.getDraftLlm() != null
                        ? persistedSettings.getDraftLlm()
                        : RobotLlmEntity.builder().uid(uidUtils.getUid()).build();
                if (draftLlmSettings.getUid() == null || draftLlmSettings.getUid().isEmpty()) {
                    draftLlmSettings.setUid(uidUtils.getUid());
                }
                draftLlmSettings.setPrompt(localeData.getPrompt());
                persistedSettings.setDraftLlm(draftLlmSettings);
                // Persist settings with valid nested LLM entities
                robotSettingsRestService.save(persistedSettings);

                // Create robot entity
                RobotEntity robot = RobotEntity.builder()
                        .uid(uidUtils.getUid())
                        .name(robotJson.getName())
                        .nickname(localeData.getNickname())
                        .avatar(AvatarConsts.getDefaultRobotAvatar())
                        .description(localeData.getDescription())
                        .type(robotJson.getType())
                        .categoryUid(categoryUid)
                        .level(level)
                        .orgUid(orgUid)
                        .settings(persistedSettings)
                        .system(true)
                        .build();
                //
                save(robot);
            }
        }
    }

    // 创建智能体机器人（LLM 配置请通过 RobotSettings 进行）
    @Transactional
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

    // 知识库更新请通过 RobotSettings 接口更新 kbEnabled/kbUid
    @Transactional
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
        return convertToResponse(robotOptional.get());
    }

    // public void initDemoBytedesk() {
    // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
    // // 首先redis中是否已经初始化此数据，如果没有，继续执行演示数据初始化
    // String isInit =
    // stringRedisTemplate.opsForValue().get(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY);

    // if (isInit == null) {
    // String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID);
    // // 默认使用演示文档内容，填充且只填充超级管理员演示机器人
    // List<FileContent> files = springAIBytedeskService.getAllFiles();
    // // 计数器
    // final int[] count = { 0 };
    // final int MAX_CALLS = 1;

    // // 写入到redis vector 中
    // for (FileContent file : files) {
    // // springAIVectorService.ifPresent(service -> {
    // // // service.readTextDemo(file.getFilename(), file.getContent(), kbUid,
    // orgUid);
    // // });
    // // 只在前两次调用zhipuaiChatService
    // if (count[0] < MAX_CALLS) {
    // // springAIZhipuaiChatService.ifPresent(service -> {
    // // String qaPairs = service.generateFaqPairsAsync(file.getContent());
    // // log.info("zhipuaiChatService generateFaqPairsAsync qaPairs {}", qaPairs);
    // // faqRestService.saveFaqPairs(qaPairs, kbUid, orgUid, "");
    // // count[0]++;
    // // });
    // }
    // }
    // // 设置redis key 为已初始化
    // stringRedisTemplate.opsForValue().set(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY,
    // "true");
    // // 删除 redis key
    // // redisTemplate.delete(RobotConsts.ROBOT_INIT_DEMO_KEY);
    // } else {
    // log.info("initDemoBytedesk already initialized");
    // }
    // }

    @Override
    public RobotExcel convertToExcel(RobotEntity entity) {
        RobotExcel robotExcel = modelMapper.map(entity, RobotExcel.class);
        try {
            if (entity.getSettings() != null && entity.getSettings().getLlm() != null) {
                robotExcel.setPrompt(entity.getSettings().getLlm().getPrompt());
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
        // 知识库关联迁移到 RobotSettings，此处不再设置
        // TODO: Settings should be managed through RobotSettingsEntity
        // 设置默认的服务设置
        // ServiceSettings serviceSettings = ServiceSettings.builder()
        // .showFaqs(true)
        // .showQuickFaqs(true)
        // .showGuessFaqs(true)
        // .showHotFaqs(true)
        // .showShortcutFaqs(true)
        // .build();
        // robot.setServiceSettings(serviceSettings);
        return robot;
    }

}
