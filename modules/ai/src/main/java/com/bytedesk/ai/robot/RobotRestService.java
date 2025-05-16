/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-16 16:23:38
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

// import com.bytedesk.ai.demo.utils.FileContent;
import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotJsonLoader.Robot;
import com.bytedesk.ai.robot.RobotJsonLoader.RobotConfiguration;
import com.bytedesk.ai.utils.ConvertAiUtils;
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
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
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

    // private final SpringAIBytedeskService springAIBytedeskService;

    // private final StringRedisTemplate stringRedisTemplate;

    private final LlmProviderRestService llmProviderRestService;

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
            return convertToResponse(findByUid(request.getUid()).get());
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
        robot.setKbEnabled(request.getKbEnabled());
        robot.setKbUid(request.getKbUid());
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

    public ThreadResponse createLlmThread(ThreadRequest request) {
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw new RuntimeException("should login first");
        }
        RobotProtobuf robotProtobuf = RobotProtobuf.fromJson(request.getRobot()); 
        String robotUid = robotProtobuf.getUid();
        if (!StringUtils.hasText(robotUid)) {
            throw new RuntimeException("robotUid is required");
        }
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
            Optional<ThreadEntity> threadOptional = threadService.findFirstByTopicAndOwner(topic, owner);
            if (threadOptional.isPresent()) {
                return threadService.convertToResponse(threadOptional.get());
            }
        }
        //
        Optional<RobotEntity> robotOptional = findByUid(robotUid);
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + robotUid + " not found");
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
                .unreadCount(0)
                .hide(request.getHide())
                .user(user)
                .agent(robot)
                .robot(robot)
                .userUid(owner.getUid())
                .owner(owner)
                .orgUid(owner.getOrgUid())
                .build();
        //
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return threadService.convertToResponse(savedThread);
    }

    public ThreadResponse updateLlmThread(ThreadRequest request) {
        //
        String topic = request.getTopic();
        RobotProtobuf robotProtobuf = RobotProtobuf.fromJson(request.getAgent());
        // 
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopic(topic);
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        ThreadEntity thread = threadOptional.get();
        // 
        Optional<LlmProviderEntity> llmProviderOptional = llmProviderRestService
                .findByNameAndOrgUid(robotProtobuf.getLlm().getProvider(), thread.getOrgUid());
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
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return ConvertUtils.convertToThreadResponse(savedThread);
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
        // robot.setDefaultReply(request.getDefaultReply());
        robot.setKbEnabled(request.getKbEnabled());
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
                    // log.info("welcomeFaqUid added {}", welcomeFaqUid);
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
        // if (request == null || request.getInviteSettings() == null) {
        //     robot.setInviteSettings(InviteSettings.builder().build());
        // } else {
        //     InviteSettings inviteSettings = modelMapper.map(request.getInviteSettings(), InviteSettings.class);
        //     robot.setInviteSettings(inviteSettings);
        // }

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
    protected RobotEntity doSave(RobotEntity entity) {
        try {
            return robotRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw e; // 重新抛出异常以触发重试机制
        }
    }

    @Override
    public RobotEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RobotEntity entity) {
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
        createDefaultRobot(orgUid, uid); // Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID)
        // 为每个组织创建一个空白智能体
        createDefaultPromptRobot(orgUid, Utils.formatUid(orgUid, RobotConsts.ROBOT_NAME_VOID_AGENT));
    }

    // 创建一个机器人
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
                .kbEnabled(true)
                .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID))
                .build();
        //
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

    // 创建一个空白智能体
    public RobotResponse createDefaultPromptRobot(String orgUid, String uid) {
        // 判断uid是否已经存在
        if (StringUtils.hasText(uid) && existsByUid(uid)) {
            return convertToResponse(findByUid(uid).get());
        }
        // Create RobotLlm with prompt from locale data
        RobotLlm llm = RobotLlm.builder()
                .prompt("请回答用户提出的问题")
                .build();
        //
        RobotEntity robot = RobotEntity.builder()
                .uid(uid)
                .name(RobotConsts.ROBOT_NAME_VOID_AGENT)
                .nickname("空白智能体")
                .type(RobotTypeEnum.LLM.name())
                .llm(llm)
                .orgUid(orgUid)
                .kbEnabled(false)
                .build();
        //
        RobotEntity updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }

        return convertToResponse(updatedRobot);
    }

    public void initRobotJson() {
        //
        String level = LevelEnum.PLATFORM.name();
        RobotConfiguration config = robotJsonLoader.loadRobots();
        List<Robot> robots = config.getRobots();
        for (Robot robotJson : robots) {
            // 使用name代替uid，方便查询和创建机器人
            String uid = robotJson.getName();
            if (StringUtils.hasText(uid) && !existsByUid(uid)) {
                String categoryUid = null;
                Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndTypeAndLevelAndPlatform(
                        robotJson.getCategory(),
                        CategoryTypeEnum.ROBOT.name(), level, BytedeskConsts.PLATFORM_BYTEDESK);
                if (!categoryOptional.isPresent()) {
                    CategoryRequest categoryRequest = CategoryRequest.builder()
                            .name(robotJson.getCategory())
                            .order(0)
                            .level(level)
                            .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                            .type(CategoryTypeEnum.ROBOT.name())
                            .build();
                    CategoryResponse categoryResponse = categoryService.create(categoryRequest);
                    if (categoryResponse != null) {
                        categoryUid = categoryResponse.getUid();
                    } else {
                        throw new RuntimeException("create category failed");
                    }
                } else {
                    categoryUid = categoryOptional.get().getUid();
                }
                //
                // 判断uid是否已经存在
                // if (StringUtils.hasText(uid) && existsByUid(uid)) {
                //     return;
                // }
                // Get locale data (default to zh_cn if available, fallback to en)
                RobotJsonLoader.LocaleData localeData = robotJson.getI18n().getZh_cn() != null
                        ? robotJson.getI18n().getZh_cn()
                        : robotJson.getI18n().getEn();

                // Create RobotLlm with prompt from locale data
                RobotLlm llm = RobotLlm.builder()
                        .prompt(localeData.getPrompt())
                        .build();

                // Create RobotEntity with data from both Robot and LocaleData
                RobotEntity robot = RobotEntity.builder()
                        .uid(uid)
                        .name(robotJson.getName())
                        .nickname(localeData.getNickname())
                        .avatar(AvatarConsts.getDefaultRobotAvatar())
                        .description(localeData.getDescription())
                        .type(robotJson.getType())
                        .categoryUid(categoryUid)
                        .level(level)
                        .llm(llm)
                        // .published(true)
                        .build();

                // RobotEntity savedRobot =
                save(robot);
                // if (savedRobot == null) {
                // throw new RuntimeException("create robot failed");
                // }
            }
        }
    }

    // 创建智能体机器人
    public RobotResponse createPromptRobot(RobotRequest request) {
        //
        RobotEntity robot = modelMapper.map(request, RobotEntity.class);
        robot.setUid(uidUtils.getUid());
        robot.setType(RobotTypeEnum.LLM.name());
        // robot.setDefaultReply(I18Consts.I18N_ROBOT_DEFAULT_REPLY);
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

    // update kbUid
    public RobotResponse updateKbUid(RobotRequest request) {
        Optional<RobotEntity> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        //
        RobotEntity robot = robotOptional.get();
        robot.setKbEnabled(request.getKbEnabled());
        robot.setKbUid(request.getKbUid());
        //
        RobotEntity savedRobot = save(robot);
        if (savedRobot == null) {
            throw new RuntimeException("update robot " + request.getUid() + " failed");
        }
        return convertToResponse(savedRobot);
    }

    public void initDemoBytedesk() {
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // // 首先redis中是否已经初始化此数据，如果没有，继续执行演示数据初始化
        // String isInit = stringRedisTemplate.opsForValue().get(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY);

        // if (isInit == null) {
        //     String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID);
        //     // 默认使用演示文档内容，填充且只填充超级管理员演示机器人
        //     List<FileContent> files = springAIBytedeskService.getAllFiles();
        //     // 计数器
        //     final int[] count = { 0 };
        //     final int MAX_CALLS = 1;

        //     // 写入到redis vector 中
        //     for (FileContent file : files) {
        //         // springAIVectorService.ifPresent(service -> {
        //         //     // service.readTextDemo(file.getFilename(), file.getContent(), kbUid, orgUid);
        //         // });
        //         // 只在前两次调用zhipuaiChatService
        //         if (count[0] < MAX_CALLS) {
        //             // springAIZhipuaiChatService.ifPresent(service -> {
        //             // String qaPairs = service.generateFaqPairsAsync(file.getContent());
        //             // log.info("zhipuaiChatService generateFaqPairsAsync qaPairs {}", qaPairs);
        //             // faqRestService.saveFaqPairs(qaPairs, kbUid, orgUid, "");
        //             // count[0]++;
        //             // });
        //         }
        //     }
        //     // 设置redis key 为已初始化
        //     stringRedisTemplate.opsForValue().set(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY, "true");
        //     // 删除 redis key
        //     // redisTemplate.delete(RobotConsts.ROBOT_INIT_DEMO_KEY);
        // } else {
        //     log.info("initDemoBytedesk already initialized");
        // }
    }

    @Override
    public RobotResponse queryByUid(RobotRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
