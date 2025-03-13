/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 13:14:42
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
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotJsonLoader.Robot;
import com.bytedesk.ai.robot.RobotJsonLoader.RobotConfiguration;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.ai.utils.demo.bytedesk.SpringAIBytedeskService;
import com.bytedesk.ai.utils.demo.utils.FileContent;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStateEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.OptimisticLockingHandler;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.settings.InviteSettings;
import com.bytedesk.kbase.settings.ServiceSettings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotRestService extends BaseRestService<RobotEntity, RobotRequest, RobotResponse> {

    private final RobotRepository robotRepository;

    private final AuthService authService;

    private final FaqRestService faqService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRestService threadService;

    private final RobotJsonLoader robotJsonLoader;

    private final CategoryRestService categoryService;

    private final SpringAIBytedeskService springAIBytedeskService;

    private final StringRedisTemplate stringRedisTemplate;

    private final Optional<SpringAIVectorService> springAIVectorService;

    private final LlmProviderRestService llmProviderRestService;

    private final OptimisticLockingHandler optimisticLockingHandler;

    @PostConstruct
    public void setupModelMapper() {
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true);
            
        // 配置 ThreadRequest 到 ThreadEntity 的映射
        modelMapper.createTypeMap(ThreadRequest.class, ThreadEntity.class)
            .addMappings(mapper -> {
                mapper.skip(ThreadEntity::setUser);  // 跳过自动映射
                // 添加其他需要的映射
                mapper.map(ThreadRequest::getTopic, ThreadEntity::setTopic);
                mapper.map(ThreadRequest::getType, ThreadEntity::setType);
            });
    }

    @Override
    public Page<RobotResponse> queryByOrg(RobotRequest request) {
        Pageable pageable = request.getPageable();
        Specification<RobotEntity> specification = RobotSpecification.search(request);
        Page<RobotEntity> page = robotRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<RobotResponse> queryByUser(RobotRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
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

    public Boolean existsByUid(String uid) {
        return robotRepository.existsByUidAndDeleted(uid, false);
    }

    public RobotResponse queryByUid(String uid) {
        Optional<RobotEntity> robotOptional = robotRepository.findByUid(uid);
        if (robotOptional.isPresent()) {
            return convertToResponse(robotOptional.get());
        } else {
            throw new RuntimeException("robot not found by uid: " + uid);
        }
    }

    @Transactional
    @Override
    public RobotResponse create(RobotRequest request) {
        // 如果uid不为空，判断是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return null;
        }
        //
        // Robot robot = modelMapper.map(request, Robot.class);
        RobotEntity robot = RobotEntity.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            robot.setUid(request.getUid());
        } else {
            robot.setUid(uidUtils.getUid());
        }
        robot.setNickname(request.getNickname());
        robot.setType(request.getType());
        robot.setOrgUid(request.getOrgUid());
        // robot.setKbEnabled(request.getIsKbEnabled());
        // robot.setKbUid(request.getKbUid());
        robot.setKbEnabled(true);
        robot.setKbUid(Utils.formatUid(request.getOrgUid(), BytedeskConsts.DEFAULT_KB_LLM_UID));
        //
        // Set common settings
        setRobotSettings(robot, request);
        //
        RobotEntity updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }

        return convertToResponse(updatedRobot);
    }

    public ThreadResponse createThread(ThreadRequest request) {
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw new RuntimeException("should login first not found");
        }
        // org/robot/robotUid/userUid
        String preTopic = request.getTopic();
        String[] splits = preTopic.split("/");
        if (splits.length < 4) {
            throw new RuntimeException("robot topic format error");
        }
        // org/robot/robotUid/userUid/randomUid
        String robotUid = splits[2];
        Optional<RobotEntity> robotOptional = findByUid(robotUid);
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot not found");
        }
        // org/robot/robotUid/userUid/randomUid
        String topic = TopicUtils.formatOrgRobotMemberThreadTopic(robotUid, owner.getUid(), uidUtils.getUid());

        // 允许一个用户创建多个相同机器人的会话
        // Optional<ThreadEntity> threadOptional = threadService.findFirstByTopicAndOwner(topic, owner);
        // if (threadOptional.isPresent()) {
        //     return threadService.convertToResponse(threadOptional.get());
        // }

        // 创建新的 ThreadEntity 并手动设置属性，而不是使用 ModelMapper
        ThreadEntity thread = ThreadEntity.builder()
            .topic(topic)
            .type(ThreadTypeEnum.LLM.name())
            .state(ThreadStateEnum.STARTED.name())
            .unreadCount(0)
            .user(JSON.toJSONString(request.getUser()))
            .owner(owner)
        .build();
        thread.setUid(uidUtils.getUid());
        thread.setOrgUid(owner.getOrgUid());
        // 
        RobotEntity robot = robotOptional.get();
        robot.setAvatar(AvatarConsts.getLlmThreadDefaultAvatar());
        thread.setAgent(ConvertAiUtils.convertToRobotProtobufString(robot));
        // 
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return threadService.convertToResponse(savedThread);
    }

    public ThreadResponse updateThread(ThreadRequest request) {
        //
        String topic = request.getTopic();
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        ThreadEntity thread = threadOptional.get();
        thread.setUser(JSON.toJSONString(request.getUser()));
        // 
        RobotProtobuf robotProtobuf = JSON.parseObject(request.getAgent(), RobotProtobuf.class);
        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService.findByNameAndOrgUid(robotProtobuf.getLlm().getProvider(), thread.getOrgUid());
        if (!llmProviderOptional.isPresent()) {
            throw new RuntimeException("llm provider not found");
        }
        robotProtobuf.setAvatar(llmProviderOptional.get().getLogo());
        robotProtobuf.setNickname(llmProviderOptional.get().getNickname());
        thread.setAgent(JSON.toJSONString(robotProtobuf));
        // thread.setAgent(request.getAgent());
        //
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return threadService.convertToResponse(savedThread);
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
        // robot = modelMapper.map(request, RobotEntity.class);
        robot.setNickname(request.getNickname());
        robot.setAvatar(request.getAvatar());
        robot.setDescription(request.getDescription());
        robot.setPublished(request.getPublished());
        robot.setDefaultReply(request.getDefaultReply());
        // robot.setKbEnabled(request.getIsKbEnabled());
        robot.setKbUid(request.getKbUid());
        //
        // Set common settings
        setRobotSettings(robot, request);
        //
        RobotEntity updateRobot = save(robot);
        if (updateRobot == null) {
            throw new RuntimeException("update robot failed");
        }

        return convertToResponse(updateRobot);
    }

    /**
     * Sets common settings for both create and update operations
     * 
     * @param robot   The robot entity to update
     * @param request The request containing settings
     */
    private void setRobotSettings(RobotEntity robot, RobotRequest request) {

        ServiceSettings serviceSettings = modelMapper.map(
                request.getServiceSettings(), ServiceSettings.class);

        // Set Welcome FAQs
        if (request.getServiceSettings().getWelcomeFaqUids() != null
                && request.getServiceSettings().getWelcomeFaqUids().size() > 0) {
            for (String welcomeFaqUid : request.getServiceSettings().getWelcomeFaqUids()) {
                Optional<FaqEntity> welcomeFaqOptional = faqService.findByUid(welcomeFaqUid);
                if (welcomeFaqOptional.isPresent()) {
                    FaqEntity welcomeFaqEntity = welcomeFaqOptional.get();
                    log.info("welcomeFaqUid added {}", welcomeFaqUid);
                    serviceSettings.getWelcomeFaqs().add(welcomeFaqEntity);
                } else {
                    throw new RuntimeException("welcomeFaq " + welcomeFaqUid + " not found");
                }
            }
        }

        // Set FAQs
        if (request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            for (String faqUid : request.getServiceSettings().getFaqUids()) {
                Optional<FaqEntity> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    FaqEntity faqEntity = faqOptional.get();
                    log.info("faqUid added {}", faqUid);
                    serviceSettings.getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        }

        // Set Quick FAQs
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getQuickFaqUids() != null
                && request.getServiceSettings().getQuickFaqUids().size() > 0) {
            for (String quickFaqUid : request.getServiceSettings().getQuickFaqUids()) {
                Optional<FaqEntity> quickFaqOptional = faqService.findByUid(quickFaqUid);
                if (quickFaqOptional.isPresent()) {
                    FaqEntity quickFaqEntity = quickFaqOptional.get();
                    log.info("quickFaqUid added {}", quickFaqUid);
                    serviceSettings.getQuickFaqs().add(quickFaqEntity);
                } else {
                    throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
                }
            }
        }

        // Set Guess FAQs
        if (request.getServiceSettings().getGuessFaqUids() != null
                && request.getServiceSettings().getGuessFaqUids().size() > 0) {
            for (String guessFaqUid : request.getServiceSettings().getGuessFaqUids()) {
                Optional<FaqEntity> guessFaqOptional = faqService.findByUid(guessFaqUid);
                if (guessFaqOptional.isPresent()) {
                    FaqEntity guessFaq = guessFaqOptional.get();
                    log.info("guessFaqUid added {}", guessFaqUid);
                    serviceSettings.getGuessFaqs().add(guessFaq);
                } else {
                    throw new RuntimeException("guessFaq " + guessFaqUid + " not found");
                }
            }
        }

        // Set Hot FAQs
        if (request.getServiceSettings().getHotFaqUids() != null
                && request.getServiceSettings().getHotFaqUids().size() > 0) {
            for (String hotFaqUid : request.getServiceSettings().getHotFaqUids()) {
                Optional<FaqEntity> hotFaqOptional = faqService.findByUid(hotFaqUid);
                if (hotFaqOptional.isPresent()) {
                    FaqEntity hotFaq = hotFaqOptional.get();
                    log.info("hotFaqUid added {}", hotFaqUid);
                    serviceSettings.getHotFaqs().add(hotFaq);
                } else {
                    throw new RuntimeException("hotFaq " + hotFaqUid + " not found");
                }
            }
        }

        // Set Shortcut FAQs
        if (request.getServiceSettings().getShortcutFaqUids() != null
                && request.getServiceSettings().getShortcutFaqUids().size() > 0) {
            for (String shortcutFaqUid : request.getServiceSettings().getShortcutFaqUids()) {
                Optional<FaqEntity> shortcutFaqOptional = faqService.findByUid(shortcutFaqUid);
                if (shortcutFaqOptional.isPresent()) {
                    FaqEntity shortcutFaq = shortcutFaqOptional.get();
                    log.info("shortcutFaqUid added {}", shortcutFaqUid);
                    serviceSettings.getShortcutFaqs().add(shortcutFaq);
                } else {
                    throw new RuntimeException("shortcutFaq " + shortcutFaqUid + " not found");
                }
            }
        }
        robot.setServiceSettings(serviceSettings);

        // Set Invite Settings
        if (request == null || request.getInviteSettings() == null) {
            robot.setInviteSettings(InviteSettings.builder().build());
        } else {
            InviteSettings inviteSettings = modelMapper.map(request.getInviteSettings(), InviteSettings.class);
            robot.setInviteSettings(inviteSettings);
        }

        // Set LLM
        if (request.getLlm() == null) {
            RobotLlm robotLlm = RobotLlm.builder().build();
            robot.setLlm(robotLlm);
        } else {
            robot.setLlm(request.getLlm());
        }
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
    public RobotEntity save(RobotEntity entity) {
        try {
            // return robotRepository.save(entity);
            return optimisticLockingHandler.executeWithRetry(
                () -> robotRepository.save(entity),
                "robot",
                entity.getUid(),
                entity
            );
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Failed to save robot after retries", e);
            return null;
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<RobotEntity> robotOptional = findByUid(uid);
        robotOptional.ifPresent(robot -> {
            robot.setDeleted(true);
            save(robot);
        });
    }

    @Override
    public void delete(RobotRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public RobotResponse convertToResponse(RobotEntity entity) {
        return modelMapper.map(entity, RobotResponse.class);
    }

    // 初始化
    public void initDefaultRobot(String orgUid, String uid) {
        // 为每个组织创建一个机器人
        createDefaultRobot(orgUid, uid);
        // 为每个组织创建一个客服助手
        // createDefaultAgentAssistantRobot(orgUid, Utils.formatUid(orgUid, RobotConsts.DEFAULT_ROBOT_DEMO_UID));
        // 为每个组织自动导入智能体
        initRobotJson(orgUid, LevelEnum.ORGANIZATION.name());
    }

    // 为每个组织创建一个机器人
    public RobotResponse createDefaultRobot(String orgUid, String uid) {
        //
        RobotRequest robotRequest = RobotRequest.builder()
                .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                .build();
        robotRequest.setUid(uid);
        robotRequest.setType(RobotTypeEnum.SERVICE.name());
        robotRequest.setOrgUid(orgUid);
        //
        robotRequest.setIsKbEnabled(true);
        robotRequest.setKbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID));
        robotRequest.getServiceSettings().setShowFaqs(true);
        robotRequest.getServiceSettings().setShowQuickFaqs(true);
        robotRequest.getServiceSettings().setShowGuessFaqs(true);
        robotRequest.getServiceSettings().setShowHotFaqs(true);
        robotRequest.getServiceSettings().setShowShortcutFaqs(true);
        // 
        // 写入 faq uid 到 welcomeFaqUids
        if (orgUid.equals(BytedeskConsts.DEFAULT_ORGANIZATION_UID)) {
            // 将 faq_001 ~ faq_005 写入到 welcomeFaqUids
            for (int i = 1; i <= 5; i++) {
                String faqUid = Utils.formatUid(orgUid, "faq_00" + i);
                robotRequest.getServiceSettings().getWelcomeFaqUids().add(faqUid);
            }
        }
        //
        return create(robotRequest);
    }

    // 为每一个组织创建一个客服助手
    // public RobotResponse createDefaultAgentAssistantRobot(String orgUid, String uid) {
    //     //
    //     RobotRequest robotRequest = RobotRequest.builder()
    //             .nickname(I18Consts.I18N_ROBOT_AGENT_ASSISTANT_NICKNAME)
    //             .build();
    //     robotRequest.setUid(uid);
    //     robotRequest.setType(RobotTypeEnum.LLM.name());
    //     robotRequest.setOrgUid(orgUid);
    //     //
    //     return create(robotRequest);
    // }
    
    public void initRobotJson(String orgUid, String level) {
        //
        RobotConfiguration config = robotJsonLoader.loadRobots();
        List<Robot> robots = config.getRobots();
        for (Robot robotJson : robots) {
            String uid = Utils.formatUid(orgUid, robotJson.getUid());
            if (!existsByUid(uid)) {
                String categoryUid = null;
                Optional<CategoryEntity> categoryOptional = categoryService
                        .findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(robotJson.getCategory(),
                                CategoryTypeEnum.ROBOT.name(), orgUid, level, BytedeskConsts.PLATFORM_BYTEDESK);
                if (!categoryOptional.isPresent()) {
                    CategoryRequest categoryRequest = CategoryRequest.builder()
                            .name(robotJson.getCategory())
                            .order(0)
                            .level(level)
                            .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                            .build();
                    categoryRequest.setType(CategoryTypeEnum.ROBOT.name());
                    categoryRequest.setOrgUid(orgUid);
                    CategoryResponse categoryResponse = categoryService.create(categoryRequest);
                    if (categoryResponse != null) {
                        categoryUid = categoryResponse.getUid();
                    } else {
                        throw new RuntimeException("create category failed");
                    }
                } else {
                    categoryUid = categoryOptional.get().getUid();
                }
                createPromptRobotFromJson(orgUid, robotJson, categoryUid, level);
            }
        }
    }

    // 从json创建平台智能体
    public RobotResponse createPromptRobotFromJson(String orgUid, Robot robotJson, String categoryUid,
            String level) {
        log.info("robotJson {}", robotJson.getName());
        String uid = Utils.formatUid(orgUid, robotJson.getUid());

        // Get locale data (default to zh_cn if available, fallback to en)
        RobotJsonLoader.LocaleData localeData = robotJson.getI18n().getZh_cn() != null ? 
            robotJson.getI18n().getZh_cn() : robotJson.getI18n().getEn();

        // Create RobotLlm with prompt from locale data
        RobotLlm llm = RobotLlm.builder()
            .prompt(localeData.getPrompt())
            .build();

        // Create RobotEntity with data from both Robot and LocaleData
        RobotEntity robot = RobotEntity.builder()
                .name(robotJson.getName())
                .nickname(localeData.getNickname())
                .avatar(AvatarConsts.getDefaultRobotAvatar())
                .description(localeData.getDescription())
                .type(robotJson.getType())
                .categoryUid(categoryUid)
                .level(level)
                .llm(llm)
                .published(true)
                .build();
        robot.setUid(uid);
        robot.setOrgUid(orgUid);

        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("create robot failed");
        }
        return convertToResponse(savedRobot);
    }

    // 创建智能体机器人
    public RobotResponse createPromptRobot(RobotRequest request) {
        //
        RobotEntity robot = modelMapper.map(request, RobotEntity.class);
        robot.setUid(uidUtils.getUid());
        robot.setType(RobotTypeEnum.LLM.name());
        robot.setDefaultReply(I18Consts.I18N_ROBOT_REPLY);
        //
        RobotLlm llm = RobotLlm.builder().prompt(request.getLlm().getPrompt()).build();
        robot.setLlm(llm);
        //
        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("create robot failed");
        }
        return convertToResponse(savedRobot);
    }

    public RobotResponse updatePromptRobot(RobotRequest request) {
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        //
        RobotEntity robot = robotOptional.get();
        // robot = modelMapper.map(request, RobotEntity.class);
        robot.setNickname(request.getNickname());
        robot.setAvatar(request.getAvatar());
        robot.setDescription(request.getDescription());
        robot.setCategoryUid(request.getCategoryUid());
        //
        RobotLlm llm = robot.getLlm();
        llm.setPrompt(request.getLlm().getPrompt());
        robot.setLlm(llm);
        //
        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("update robot " + request.getUid() + " failed");
        }
        return convertToResponse(savedRobot);
    }

    public void initDemoBytedesk() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // 首先redis中是否已经初始化此数据，如果没有，继续执行演示数据初始化
        String isInit = stringRedisTemplate.opsForValue().get(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY);

        if (isInit == null) {
            String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID);
            // 默认使用演示文档内容，填充且只填充超级管理员演示机器人
            List<FileContent> files = springAIBytedeskService.getAllFiles();
            // 计数器
            final int[] count = {0};
            final int MAX_CALLS = 1;
            
            // 写入到redis vector 中
            for (FileContent file : files) {
                springAIVectorService.ifPresent(service -> {
                    service.readText(file.getFilename(), file.getContent(), kbUid, orgUid);
                });
                // 只在前两次调用zhipuaiChatService
                if (count[0] < MAX_CALLS) {
                    // springAIZhipuaiChatService.ifPresent(service -> {
                    //     String qaPairs = service.generateFaqPairsAsync(file.getContent());
                    //     log.info("zhipuaiChatService generateFaqPairsAsync qaPairs {}", qaPairs);
                    //     faqRestService.saveFaqPairs(qaPairs, kbUid, orgUid, "");
                    //     count[0]++;
                    // });
                }
            }
            // 设置redis key 为已初始化
            stringRedisTemplate.opsForValue().set(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY, "true");
            // 删除 redis key
            // redisTemplate.delete(RobotConsts.ROBOT_INIT_DEMO_KEY);
        } else {
            log.info("initDemoBytedesk already initialized");
        }
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RobotEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public RobotResponse queryByUid(RobotRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }


}
