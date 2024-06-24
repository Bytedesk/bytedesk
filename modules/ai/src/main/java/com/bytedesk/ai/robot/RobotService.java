/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-25 00:02:45
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

import java.util.Iterator;
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
        // if (robot.getServiceSettings().getQuickButtonUids() != null
        //         && robot.getServiceSettings().getQuickButtonUids().size() > 0) {
        //     Iterator<String> iterator = agentRequest.getServiceSettings().getQuickButtonUids().iterator();
        //     while (iterator.hasNext()) {
        //         String quickButtonUid = iterator.next();
        //         Optional<QuickButton> quickButtonOptional = quickButtonService.findByUid(quickButtonUid);
        //         if (quickButtonOptional.isPresent()) {
        //             QuickButton quickButtonEntity = quickButtonOptional.get();

        //             agent.getServiceSettings().getQuickButtons().add(quickButtonEntity);
        //         }
        //     }
        // }
        // //
        // if (agentRequest.getServiceSettings().getFaqUids() != null
        //         && agentRequest.getServiceSettings().getFaqUids().size() > 0) {
        //     Iterator<String> iterator = agentRequest.getServiceSettings().getFaqUids().iterator();
        //     while (iterator.hasNext()) {
        //         String faqUid = iterator.next();
        //         Optional<Faq> faqOptional = faqService.findByUid(faqUid);
        //         if (faqOptional.isPresent()) {
        //             Faq faqEntity = faqOptional.get();

        //             agent.getServiceSettings().getFaqs().add(faqEntity);
        //         }
        //     }
        // }
        
        return convertToResponse(save(robot));
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

                    serviceSettings.getQuickButtons().add(quickButtonEntity);
                }
            }
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

                    serviceSettings.getFaqs().add(faqEntity);
                }
            }
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
