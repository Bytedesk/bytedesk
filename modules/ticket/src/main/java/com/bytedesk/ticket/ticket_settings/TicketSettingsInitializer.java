/*
 * Initialize default TicketSettings for default organization and bind to default workgroup
 */
package com.bytedesk.ticket.ticket_settings;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.ticket.ticket.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.binding.TicketSettingsBindingEntity;
import com.bytedesk.ticket.ticket_settings.binding.TicketSettingsBindingRepository;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ticketSettingsInitializer")
@DependsOn("workgroupInitializer")
@AllArgsConstructor
public class TicketSettingsInitializer implements SmartInitializingSingleton {

    private final TicketSettingsRestService ticketSettingsRestService;
    private final TicketSettingsBindingRepository bindingRepository;
    private final UidUtils uidUtils;

    @Override
    public void afterSingletonsInstantiated() {
        try {
            initDefaults();
        } catch (Exception e) {
            log.error("TicketSettingsInitializer failed: {}", e.getMessage(), e);
        }
    }

    private void initDefaults() {
        final String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        final String defaultWorkgroupUid = BytedeskConsts.DEFAULT_WORKGROUP_UID;

        // 1) 获取或创建组织默认 TicketSettings
        TicketSettingsEntity defExternal = ticketSettingsRestService.getOrCreateDefault(orgUid, TicketTypeEnum.EXTERNAL.name());
        ticketSettingsRestService.getOrCreateDefault(orgUid, TicketTypeEnum.INTERNAL.name());
        log.info("Default TicketSettings ready for org {}: {} (external)", orgUid, defExternal.getUid());

        // 2) 绑定默认工作组（若尚未绑定）
        bindingRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, defaultWorkgroupUid)
                .orElseGet(() -> {
                    TicketSettingsBindingEntity binding = TicketSettingsBindingEntity.builder()
                            .uid(uidUtils.getUid())
                            .orgUid(orgUid)
                            .workgroupUid(defaultWorkgroupUid)
                                .ticketSettingsUid(defExternal.getUid())
                            .build();
                                log.info("Bind default workgroup {} to default external TicketSettings {}", defaultWorkgroupUid,
                                    defExternal.getUid());
                    return bindingRepository.save(binding);
                });
    }
}
