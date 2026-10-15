package com.bytedesk.service.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.push.service.AgentPushGateService;
import com.bytedesk.core.push.service.PushMobileService;
import com.bytedesk.core.push.service.PushReachProperties;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.event.ThreadTransferToAgentEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 会话转接/邀请协助移动端推送监听器（规划 2026-10-13 §4.2 B-5）
 *
 * <p>现存触发点：工单分配发布 {@link ThreadTransferToAgentEvent}（TicketThreadRoutingStrategy），
 * 以及后续补齐的坐席间转接/邀请发起方法（同事件复用）。
 *
 * <p>推送策略：目标坐席离线才推（在线已实时下发）；转接/邀请为低频强通知，不走节流。
 */
@Slf4j
@Component
@AllArgsConstructor
public class ThreadTransferPushEventListener {

    private final ObjectProvider<PushMobileService> mobilePushProvider;

    private final AgentRestService agentRestService;

    private final AgentPushGateService agentPushGateService;

    private final PushReachProperties pushReachProperties;

    @EventListener
    public void onThreadTransferToAgentEvent(ThreadTransferToAgentEvent event) {
        if (!pushReachProperties.getTransferInvite().isEnabled()) {
            return;
        }
        try {
            ThreadEntity thread = event.getThread();
            if (thread == null || !StringUtils.hasText(thread.getUid())) {
                return;
            }
            UserProtobuf agentProtobuf = thread.getAgentProtobuf();
            if (agentProtobuf == null || !StringUtils.hasText(agentProtobuf.getUid())) {
                log.debug("Skip transfer push because thread.agent is empty, threadUid={}", thread.getUid());
                return;
            }

            Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentProtobuf.getUid());
            if (agentOptional.isEmpty()) {
                log.info("Skip transfer push because agent entity not found, threadUid={}, agentUid={}",
                        thread.getUid(), agentProtobuf.getUid());
                return;
            }
            MemberEntity member = agentOptional.get().getMember();
            UserEntity agentUser = member != null ? member.getUser() : null;
            if (agentUser == null || !StringUtils.hasText(agentUser.getUid())) {
                log.info("Skip transfer push because agent.member.user is empty, threadUid={}, agentUid={}",
                        thread.getUid(), agentOptional.get().getUid());
                return;
            }

            // 离线才推；转接/邀请为强通知不节流（throttleKey=null）
            if (!agentPushGateService.shouldPush(agentUser.getUid(), null)) {
                return;
            }

            PushMobileService mobilePush = mobilePushProvider.getIfAvailable();
            if (mobilePush == null) {
                log.debug("Skip transfer push: no PushMobileService bean (community edition)");
                return;
            }

            String visitorNickname = resolveVisitorNickname(thread);
            Map<String, String> extras = new HashMap<>();
            extras.put("type", "thread_transfer");
            extras.put("threadUid", thread.getUid());
            extras.put("threadTopic", thread.getTopic());
            extras.put("orgUid", thread.getOrgUid());
            extras.put("serverTimestamp", String.valueOf(System.currentTimeMillis()));

            mobilePush.pushNotificationToUser(agentUser.getUid(), "会话转接",
                    String.format("访客 %s 的会话已转接给您，请及时处理", visitorNickname),
                    thread.getUid(), "thread_transfer", extras);

            log.info("Transfer push sent, threadUid={}, agentUid={}, agentUserUid={}",
                    thread.getUid(), agentOptional.get().getUid(), agentUser.getUid());
        } catch (Exception e) {
            log.error("Failed to process transfer push event", e);
        }
    }

    private String resolveVisitorNickname(ThreadEntity thread) {
        if (thread == null || !StringUtils.hasText(thread.getUser())) {
            return "访客";
        }
        try {
            UserProtobuf user = UserProtobuf.fromJson(thread.getUser());
            return user != null && StringUtils.hasText(user.getNickname()) ? user.getNickname() : "访客";
        } catch (Exception e) {
            return "访客";
        }
    }
}
