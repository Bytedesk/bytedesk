/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-27 11:47:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotJsonLoader.RobotJson;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStateEnum;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.faq.FaqConsts;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqService;
import com.bytedesk.kbase.service_settings.ServiceSettings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotRestService extends BaseRestService<RobotEntity, RobotRequest, RobotResponse> {

    private final RobotRepository robotRepository;

    private final AuthService authService;

    private final FaqService faqService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRestService threadService;

    private final RobotJsonLoader robotJsonService;

    private final CategoryRestService categoryService;

    @Override
    public Page<RobotResponse> queryByOrg(RobotRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");
        Specification<RobotEntity> specification = RobotSpecification.search(request);
        Page<RobotEntity> page = robotRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<RobotResponse> queryByUser(RobotRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    public RobotResponse queryByUid(String uid) {
        Optional<RobotEntity> robotOptional = robotRepository.findByUid(uid);
        if (robotOptional.isPresent()) {
            return convertToResponse(robotOptional.get());
        } else {
            throw new RuntimeException("robot not found");
        }
    }

    @Override
    public RobotResponse create(RobotRequest request) {
        //
        RobotLlm llm = RobotLlm.builder().build();
        // Robot robot = modelMapper.map(request, Robot.class);
        RobotEntity robot = RobotEntity.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            robot.setUid(request.getUid());
        } else {
            robot.setUid(uidUtils.getCacheSerialUid());
        }
        robot.setNickname(request.getNickname());
        robot.setType(request.getType());
        robot.setOrgUid(request.getOrgUid());
        robot.setLlm(llm);
        //
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<FaqEntity> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    FaqEntity faqEntity = faqOptional.get();
                    log.info("faqUid added {}", faqUid);

                    robot.getServiceSettings().getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        } else {
            log.info("robot faq uids is null");
        }

        if (request.getServiceSettings() != null
                && request.getServiceSettings().getQuickFaqUids() != null
                && request.getServiceSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getQuickFaqUids().iterator();
            while (iterator.hasNext()) {
                String quickFaqUid = iterator.next();
                Optional<FaqEntity> quickFaqOptional = faqService.findByUid(quickFaqUid);
                if (quickFaqOptional.isPresent()) {
                    FaqEntity quickFaqEntity = quickFaqOptional.get();
                    log.info("quickFaqUid added {}", quickFaqUid);
                    robot.getServiceSettings().getQuickFaqs().add(quickFaqEntity);
                } else {
                    throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
                }
            }
        }

        RobotEntity updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }

        return convertToResponse(updatedRobot);
    }

    public ThreadResponse createThread(ThreadRequest request) {
        //
        UserEntity owner = authService.getUser();
        String topic = request.getTopic();
        //
        Optional<ThreadEntity> threadOptional = threadService.findFirstByTopicAndOwner(topic, owner);
        if (threadOptional.isPresent()) {
            return threadService.convertToResponse(threadOptional.get());
        }
        //
        ThreadEntity thread = modelMapper.map(request, ThreadEntity.class);
        thread.setUid(uidUtils.getUid());
        thread.setState(ThreadStateEnum.STARTED.name());
        //
        String user = JSON.toJSONString(request.getUser());
        // log.info("request {}, user {}", request.toString(), user);
        thread.setUser(user);
        //
        String[] splits = topic.split("/");
        if (splits.length < 4) {
            throw new RuntimeException("robot topic format error");
        }
        // org/robot/robotUid/userUid
        String robotUid = splits[2];
        Optional<RobotEntity> robotOptional = findByUid(robotUid);
        if (robotOptional.isPresent()) {
            RobotEntity robot = robotOptional.get();
            robot.setAvatar(AvatarConsts.LLM_THREAD_DEFAULT_AVATAR);
            // 更新机器人配置+大模型相关信息
            thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        }
        thread.setOwner(owner);
        thread.setOrgUid(owner.getOrgUid());
        //
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
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
        thread.setAgent(request.getAgent());
        //
        ThreadEntity savedThread = threadService.save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return threadService.convertToResponse(savedThread);
    }

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
        robot.setKbUid(request.getKbUid());
        //
        ServiceSettings serviceSettings = modelMapper.map(
                request.getServiceSettings(), ServiceSettings.class);
        //
        if (request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
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
        //
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getQuickFaqUids() != null
                && request.getServiceSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getQuickFaqUids().iterator();
            while (iterator.hasNext()) {
                String quickFaqUid = iterator.next();
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
        //
        if (request.getServiceSettings().getGuessFaqUids() != null
                && request.getServiceSettings().getGuessFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getGuessFaqUids().iterator();
            while (iterator.hasNext()) {
                String guessFaqUid = iterator.next();
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
        //
        if (request.getServiceSettings().getHotFaqUids() != null
                && request.getServiceSettings().getHotFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getHotFaqUids().iterator();
            while (iterator.hasNext()) {
                String hotFaqUid = iterator.next();
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
        //
        if (request.getServiceSettings().getShortcutFaqUids() != null
                && request.getServiceSettings().getShortcutFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getShortcutFaqUids().iterator();
            while (iterator.hasNext()) {
                String shortcutFaqUid = iterator.next();
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
        //
        robot.setServiceSettings(serviceSettings);
        //
        robot.setLlm(request.getLlm());
        //
        RobotEntity updateRobot = save(robot);
        if (updateRobot == null) {
            throw new RuntimeException("update robot failed");
        }

        // TODO: 更新当前进行中会话的agent字段？

        return convertToResponse(updateRobot);
    }

    @Cacheable(value = "robot", key = "#uid", unless = "#result == null")
    @Override
    public Optional<RobotEntity> findByUid(String uid) {
        return robotRepository.findByUid(uid);
    }

    // public Boolean existsByNicknameAndPlatform(String name) {
    // return robotRepository.existsByNicknameAndLevel(name,
    // LevelEnum.PLATFORM.name());
    // }

    public Boolean existsByUid(String uid) {
        return robotRepository.existsByUidAndDeleted(uid, false);
    }

    @Override
    public RobotEntity save(RobotEntity entity) {
        try {
            return robotRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RobotEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public RobotResponse convertToResponse(RobotEntity entity) {
        return modelMapper.map(entity, RobotResponse.class);
    }

    // 为每个组织创建一个机器人
    public RobotResponse createDefaultRobot(String orgUid, String uid) {
        List<String> faqUids = Arrays.asList(
                orgUid + FaqConsts.FAQ_DEMO_UID_1,
                orgUid + FaqConsts.FAQ_DEMO_UID_2);
        //
        RobotRequest robotRequest = RobotRequest.builder()
                .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                .build();
        robotRequest.setUid(uid);
        robotRequest.setType(RobotTypeEnum.SERVICE.name());
        robotRequest.setOrgUid(orgUid);
        //
        robotRequest.getServiceSettings().setFaqUids(faqUids);
        robotRequest.getServiceSettings().setQuickFaqUids(faqUids);
        //
        return create(robotRequest);
    }

    // 为每一个组织创建一个客服助手
    public RobotResponse createDefaultAgentAssistantRobot(String orgUid, String uid) {
        //
        RobotRequest robotRequest = RobotRequest.builder()
                .nickname(I18Consts.I18N_ROBOT_AGENT_ASSISTANT_NICKNAME)
                .build();
        robotRequest.setUid(uid);
        robotRequest.setType(RobotTypeEnum.LLM.name());
        robotRequest.setOrgUid(orgUid);
        //
        return create(robotRequest);
    }

    public void initRobotJson(String orgUid, String level) {
        //
        List<RobotJson> robotJsons = robotJsonService.loadRobots();
        for (RobotJson robotJson : robotJsons) {
            String uid = orgUid + "_" + robotJson.getUid();
            if (!existsByUid(uid)) {
                String categoryUid = null;
                Optional<CategoryEntity> categoryOptional = categoryService
                        .findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(robotJson.getCategory(),
                                CategoryTypeEnum.ROBOT.name(), orgUid, level, BytedeskConsts.PLATFORM_BYTEDESK);
                if (!categoryOptional.isPresent()) {
                    CategoryRequest categoryRequest = CategoryRequest.builder()
                            .name(robotJson.getCategory())
                            .orderNo(0)
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
    public RobotResponse createPromptRobotFromJson(String orgUid, RobotJson robotJson, String categoryUid,
            String level) {
        log.info("robotJson {}", robotJson.getNickname());
        String uid = orgUid + "_" + robotJson.getUid();
        RobotLlm llm = RobotLlm.builder().prompt(robotJson.getPrompt()).build();
        //
        RobotEntity robot = RobotEntity.builder()
                .nickname(robotJson.getNickname())
                .avatar(AvatarConsts.DEFAULT_ROBOT_AVATAR)
                .description(robotJson.getDescription())
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

}
