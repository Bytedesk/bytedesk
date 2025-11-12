/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.service.workgroup.event.WorkgroupCreateEvent;
import com.bytedesk.ticket.ticket_settings.binding.TicketSettingsBindingEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TicketSettingsEventListener {

	private final TicketSettingsRestService ticketSettingsRestService;
	private final com.bytedesk.ticket.ticket_settings.binding.TicketSettingsBindingRepository bindingRepository;
	private final com.bytedesk.core.uid.UidUtils uidUtils;

	@EventListener
	@Transactional
	public void handleWorkgroupCreate(WorkgroupCreateEvent event) {
		if (event == null || event.getWorkgroup() == null) {
			return;
		}
		String orgUid = event.getWorkgroup().getOrgUid();
		String workgroupUid = event.getWorkgroup().getUid();
		if (orgUid == null || workgroupUid == null) {
			return;
		}
		try {
			// 获取或创建默认 TicketSettings
			TicketSettingsEntity defaultSettings = ticketSettingsRestService.getOrCreateDefault(orgUid);
			// 若该工作组尚未绑定则绑定
			bindingRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid)
				.orElseGet(() -> {
					TicketSettingsBindingEntity binding = TicketSettingsBindingEntity.builder()
						.uid(uidUtils.getUid())
						.orgUid(orgUid)
						.workgroupUid(workgroupUid)
						.ticketSettingsUid(defaultSettings.getUid())
						.build();
					return bindingRepository.save(binding);
				});
			log.info("Workgroup {} auto bound to default TicketSettings {}", workgroupUid, defaultSettings.getUid());
		} catch (Exception e) {
			log.error("Auto-bind default TicketSettings failed for workgroup {}: {}", workgroupUid, e.getMessage(), e);
		}
	}
}

