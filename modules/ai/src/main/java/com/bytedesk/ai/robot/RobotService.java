/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-25 12:45:36
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

import com.bytedesk.ai.kb.Kb;
import com.bytedesk.ai.kb.KbService;
import com.bytedesk.ai.settings.RobotServiceSettings;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.faq.Faq;
import com.bytedesk.core.faq.FaqService;
import com.bytedesk.core.quick_button.QuickButton;
import com.bytedesk.core.quick_button.QuickButtonService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RobotService extends BaseService<Robot, RobotRequest, RobotResponse> {

    private final RobotRepository robotRepository;

    private final KbService kbService;

    private final QuickButtonService quickButtonService;

    private final FaqService faqService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<RobotResponse> queryByOrg(RobotRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");
        
        Specification<Robot> specification = RobotSpecification.search(request);
        Page<Robot> page = robotRepository.findAll(specification, pageable);
        // Page<Robot> page = robotRepository.findByOrgUidAndDeleted(request.getOrgUid(), false, pageable);

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
        Kb kb = kbService.getKb(request.getNickname(), request.getOrgUid());
        RobotLlm llm = RobotLlm.builder().build();
        // 
        Robot robot = Robot.builder()
                .nickname(request.getNickname())
                .type(RobotTypeEnum.fromString(request.getType()))
                // .orgUid(request.getOrgUid())
                .kb(kb)
                .llm(llm)
                .build();
        robot.setUid(uidUtils.getCacheSerialUid());
        robot.setOrgUid(request.getOrgUid());
        // 
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getQuickButtonUids() != null
                && request.getServiceSettings().getQuickButtonUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getQuickButtonUids().iterator();
            while (iterator.hasNext()) {
                String quickButtonUid = iterator.next();
                Optional<QuickButton> quickButtonOptional = quickButtonService.findByUid(quickButtonUid);
                if (quickButtonOptional.isPresent()) {
                    QuickButton quickButtonEntity = quickButtonOptional.get();
                    log.info("quickButtonUid added: {}", quickButtonUid);

                    robot.getServiceSettings().getQuickButtons().add(quickButtonEntity);
                } else {
                    throw new RuntimeException("quickButtonUid " + quickButtonUid + " not found");
                }
            }
        } else {
            log.info("robot quickButtonUid is null");
        }
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

        Robot updatedRobot = save(robot);
        if (updatedRobot == null) {
            throw new RuntimeException("save robot failed");
        }
        
        return convertToResponse(updatedRobot);
    }

    @Override
    public RobotResponse update(RobotRequest robotRequest) {

        Optional<Robot> robotOptional = findByUid(robotRequest.getUid());
        if (!robotOptional.isPresent()) {
            throw new RuntimeException("robot " + robotRequest.getUid() + " not found");
        }
        //
        Robot robot = robotOptional.get();
        robot.setNickname(robotRequest.getNickname());
        robot.setAvatar(robotRequest.getAvatar());
        robot.setDescription(robotRequest.getDescription());
        robot.setPublished(robotRequest.getPublished());
        //
        // robot.setServiceSettings(request.getServiceSettings());
        RobotServiceSettings serviceSettings = modelMapper.map(
                robotRequest.getServiceSettings(), RobotServiceSettings.class);
        if (robotRequest.getServiceSettings().getQuickButtonUids() != null
                && robotRequest.getServiceSettings().getQuickButtonUids().size() > 0) {
            Iterator<String> iterator = robotRequest.getServiceSettings().getQuickButtonUids().iterator();
            while (iterator.hasNext()) {
                String quickButtonUid = iterator.next();
                Optional<QuickButton> quickButtonOptional = quickButtonService.findByUid(quickButtonUid);
                if (quickButtonOptional.isPresent()) {
                    QuickButton quickButtonEntity = quickButtonOptional.get();
                    log.info("quickButtonUid added: {}", quickButtonUid);

                    serviceSettings.getQuickButtons().add(quickButtonEntity);
                } else {
                    throw new RuntimeException("quickButtonUid " + quickButtonUid + " not found");
                }
            }
        } else {
            log.info("robot quickButtonUid is null");
        }
        //
        if (robotRequest.getServiceSettings().getFaqUids() != null
                && robotRequest.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = robotRequest.getServiceSettings().getFaqUids().iterator();
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
        } else {
            log.info("robot faquids is null");
        }
        robot.setServiceSettings(serviceSettings);
        // 
        robot.setLlm(robotRequest.getLlm());
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
        String orgUid = UserConsts.DEFAULT_ORGANIZATION_UID;
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        List<String> quickButtonUids = Arrays.asList(
                orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_2);
        // 

        Kb kb = kbService.getKb(I18Consts.I18N_ROBOT_NICKNAME, UserConsts.DEFAULT_ORGANIZATION_UID);
        RobotLlm llm = RobotLlm.builder().build();

        Robot robot = Robot.builder()
                // .nickname(I18Consts.I18N_ROBOT_NICKNAME)
                .description(I18Consts.I18N_ROBOT_DESCRIPTION)
                .type(RobotTypeEnum.SERVICE)
                // .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .kb(kb)
                .llm(llm)
                .build();
        robot.setUid(UserConsts.DEFAULT_ROBOT_UID);
        robot.setOrgUid(UserConsts.DEFAULT_ORGANIZATION_UID);
        robot.setNickname(I18Consts.I18N_ROBOT_NICKNAME);
        robot.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        //
        save(robot);

    }

}
