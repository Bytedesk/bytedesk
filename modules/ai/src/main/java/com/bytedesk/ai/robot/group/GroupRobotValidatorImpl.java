package com.bytedesk.ai.robot.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRepository;
import com.bytedesk.core.group.GroupRobotValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ai 模块实现的群机器人校验 SPI。
 * 
 * 校验规则（fail-closed）：
 * - 机器人存在且未删除
 * - 组织归属：机器人 orgUid 为空视为系统共享机器人（允许），非空则必须与群 orgUid 一致
 * - 具备可执行的 LLM 配置（textProvider），否则群内 @ 无法应答，拒绝加入
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupRobotValidatorImpl implements GroupRobotValidator {

    private final RobotRepository robotRepository;

    @Override
    public List<String> findInvalidRobotUids(List<String> robotUids, String orgUid, String userUid) {
        List<String> invalid = new ArrayList<>();
        if (robotUids == null || robotUids.isEmpty()) {
            return invalid;
        }
        //
        List<RobotEntity> found = robotRepository.findByUidInAndDeletedFalse(robotUids);
        for (String robotUid : robotUids) {
            RobotEntity robot = found.stream()
                    .filter(item -> robotUid.equals(item.getUid()))
                    .findFirst()
                    .orElse(null);
            if (robot == null) {
                invalid.add(robotUid);
                continue;
            }
            // 组织归属：非系统机器人必须与群同租户
            if (StringUtils.hasText(robot.getOrgUid()) && !robot.getOrgUid().equals(orgUid)) {
                invalid.add(robotUid);
                continue;
            }
            // 必须具备可执行 LLM 配置，否则群内无法应答
            if (robot.getSettings() == null || robot.getLlm() == null
                    || !StringUtils.hasText(robot.getLlm().getTextProvider())) {
                invalid.add(robotUid);
            }
        }
        if (!invalid.isEmpty()) {
            log.info("GroupRobotValidatorImpl invalid robotUids: {}, orgUid: {}", invalid, orgUid);
        }
        return invalid;
    }
}
