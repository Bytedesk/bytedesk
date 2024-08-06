/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-02 22:16:34
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

import com.bytedesk.ai.settings.RobotServiceSettings;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.I18Consts;
// import com.bytedesk.core.quick_button.QuickButton;
// import com.bytedesk.core.quick_button.QuickButtonService;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.faq.Faq;
import com.bytedesk.kbase.faq.FaqService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotService extends BaseService<Robot, RobotRequest, RobotResponse> {

    private final RobotRepository robotRepository;

    // private final KbService kbService;
    // private final QuickButtonService quickButtonService;

    private final FaqService faqService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<RobotResponse> queryByOrg(RobotRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");

        Specification<Robot> specification = RobotSpecification.search(request);

        Page<Robot> page = robotRepository.findAll(specification, pageable);

        return page.map(robot -> modelMapper.map(robot, RobotResponse.class));
    }

    @Override
    public Page<RobotResponse> queryByUser(RobotRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public RobotResponse create(RobotRequest request) {

        if (existsByNicknameAndOrgUidAndDeleted(request.getNickname(), request.getOrgUid())) {
            throw new RuntimeException("robot name already exists, please find another name");
        }
        //
        // Kb kb = kbService.getKb(request.getNickname(), request.getOrgUid());
        RobotLlm llm = RobotLlm.builder().build();
        //
        // Robot robot = modelMapper.map(request, Robot.class);
        Robot robot = Robot.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            robot.setUid(request.getUid());
        } else {
            robot.setUid(uidUtils.getCacheSerialUid());
        }
        robot.setNickname(request.getNickname());
        robot.setType(RobotTypeEnum.fromValue(request.getType()));
        robot.setOrgUid(request.getOrgUid());
        robot.setLlm(llm);
        //
        // if (request.getServiceSettings() != null
        // && request.getServiceSettings().getQuickButtonUids() != null
        // && request.getServiceSettings().getQuickButtonUids().size() > 0) {
        // Iterator<String> iterator =
        // request.getServiceSettings().getQuickButtonUids().iterator();
        // while (iterator.hasNext()) {
        // String quickButtonUid = iterator.next();
        // Optional<QuickButton> quickButtonOptional =
        // quickButtonService.findByUid(quickButtonUid);
        // if (quickButtonOptional.isPresent()) {
        // QuickButton quickButtonEntity = quickButtonOptional.get();
        // log.info("quickButtonUid added: {}", quickButtonUid);

        // robot.getServiceSettings().getQuickButtons().add(quickButtonEntity);
        // } else {
        // throw new RuntimeException("quickButtonUid " + quickButtonUid + " not
        // found");
        // }
        // }
        // } else {
        // log.info("robot quickButtonUid is null");
        // }
        //
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<Faq> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    Faq faqEntity = faqOptional.get();
                    log.info("faqUid added {}", faqUid);

                    robot.getServiceSettings().getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        } else {
            log.info("robot faquids is null");
        }

        if (request.getServiceSettings() != null
                && request.getServiceSettings().getQuickFaqUids() != null
                && request.getServiceSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getQuickFaqUids().iterator();
            while (iterator.hasNext()) {
                String quickFaqUid = iterator.next();
                Optional<Faq> quickFaqOptional = faqService.findByUid(quickFaqUid);
                if (quickFaqOptional.isPresent()) {
                    Faq quickFaqEntity = quickFaqOptional.get();
                    log.info("quickFaqUid added {}", quickFaqUid);
                    robot.getServiceSettings().getQuickFaqs().add(quickFaqEntity);
                } else {
                    throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
                }
            }
        }

        Robot updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }

        return convertToResponse(updatedRobot);
    }

    @Override
    public RobotResponse update(RobotRequest request) {

        Optional<Robot> robotOptional = findByUid(request.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + request.getUid() + " not found");
        }
        //
        Robot robot = robotOptional.get();
        robot.setNickname(request.getNickname());
        robot.setAvatar(request.getAvatar());
        robot.setDescription(request.getDescription());
        robot.setPublished(request.getPublished());
        robot.setDefaultReply(request.getDefaultReply());
        robot.setKbUid(request.getKbUid());
        //
        RobotServiceSettings serviceSettings = modelMapper.map(
                request.getServiceSettings(), RobotServiceSettings.class);
        //
        if (request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<Faq> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    Faq faqEntity = faqOptional.get();
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
                Optional<Faq> quickFaqOptional = faqService.findByUid(quickFaqUid);
                if (quickFaqOptional.isPresent()) {
                    Faq quickFaqEntity = quickFaqOptional.get();
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
                Optional<Faq> guessFaqOptional = faqService.findByUid(guessFaqUid);
                if (guessFaqOptional.isPresent()) {
                    Faq guessFaq = guessFaqOptional.get();
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
                Optional<Faq> hotFaqOptional = faqService.findByUid(hotFaqUid);
                if (hotFaqOptional.isPresent()) {
                    Faq hotFaq = hotFaqOptional.get();
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
                Optional<Faq> shortcutFaqOptional = faqService.findByUid(shortcutFaqUid);
                if (shortcutFaqOptional.isPresent()) {
                    Faq shortcutFaq = shortcutFaqOptional.get();
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
        Robot updateRobot = save(robot);
        if (updateRobot == null) {
            throw new RuntimeException("update robot failed");
        }

        // TODO: 更新当前进行中会话的agent字段？

        return convertToResponse(updateRobot);
    }

    @Cacheable(value = "robot", key = "#uid", unless = "#result == null")
    @Override
    public Optional<Robot> findByUid(String uid) {
        return robotRepository.findByUid(uid);
    }

    @Override
    public Robot save(Robot entity) {
        try {
            return robotRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Robot> robotOptional = findByUid(uid);
        robotOptional.ifPresent(robot -> {
            robot.setDeleted(true);
            save(robot);
        });
    }

    @Override
    public void delete(Robot entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Robot entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public RobotResponse convertToResponse(Robot entity) {
        return modelMapper.map(entity, RobotResponse.class);
    }

    private Boolean existsByNicknameAndOrgUidAndDeleted(String name, String orgUid) {
        return robotRepository.existsByNicknameAndOrgUidAndDeleted(name, orgUid, false);
    }

    public void initData() {

        if (robotRepository.count() > 0) {
            return;
        }
        //
        String orgUid = BdConstants.DEFAULT_ORGANIZATION_UID;
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        // Kb kb = kbService.getKb(I18Consts.I18N_ROBOT_NICKNAME,
        // BdConstants.DEFAULT_ORGANIZATION_UID);
        // RobotLlm llm = RobotLlm.builder().build();
        RobotRequest robotRequest = RobotRequest.builder()
                .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                // .description(I18Consts.I18N_ROBOT_DESCRIPTION)
                // .kb(kb)
                // .llm(llm)
                .build();
        robotRequest.setUid(BdConstants.DEFAULT_ROBOT_UID);
        robotRequest.setType(RobotTypeEnum.SERVICE.name());
        robotRequest.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        //
        robotRequest.getServiceSettings().setFaqUids(faqUids);
        robotRequest.getServiceSettings().setQuickFaqUids(faqUids);
        //
        create(robotRequest);
        // save(robotRequest);

    }

}
