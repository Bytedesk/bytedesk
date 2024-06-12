/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-03 14:05:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  member: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

// import com.bytedesk.core.auth.AuthService;
// import com.bytedesk.core.rbac.user.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 仅能够监听spring-data-rest接口
 * 
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * https://docs.spring.io/spring-data/rest/reference/events.html
 */
@Slf4j
@Component
@AllArgsConstructor
@RepositoryEventHandler(Member.class)
public class MemberRestEventHandler {

	// private AuthService authService;

	@HandleBeforeCreate
	public void beforeCreate(Member member) {
		log.debug("beforeCreate");
		// User user = authService.getCurrentUser();
		// member.setOid(Utils.getUid());
		// member.setUser(user);
	}

	@HandleAfterCreate
	public void afterCreate(Member member) {
		log.debug("afterCreate");

	}

	@HandleBeforeSave
	public void beforeSave(Member member) {
		log.debug("beforeSave");

	}

	@HandleAfterSave
	public void afterSave(Member member) {
		log.debug("afterSave");

	}

	@HandleBeforeLinkSave
	public void beforeLinkSave(Member member) {
		log.debug("beforeLinkSave");

	}

	@HandleAfterLinkSave
	public void afterLinkSave(Member member) {
		log.debug("afterLinkSave");

	}

	@HandleBeforeDelete
	public void beforeDelete(Member member) {
		log.debug("beforeDelete");

	}

	@HandleAfterDelete
	public void afterDelete(Member member) {
		log.debug("afterDelete");

	}

	// private final SimpMessagingTemplate websocket;

	// private final EntityLinks entityLinks;

	// static final String MESSAGE_PREFIX = "/topic"; // <3>

	// @Autowired
	// public MemberEventHandler(SimpMessagingTemplate websocket, EntityLinks
	// entityLinks) {
	// this.websocket = websocket;
	// this.entityLinks = entityLinks;
	// }

	// @HandleAfterCreate // <3>
	// public void newMember(Member member) {
	// this.websocket.convertAndSend(
	// MESSAGE_PREFIX + "/newMember", getPath(member));
	// }

	// @HandleAfterDelete // <3>
	// public void deleteMember(Member member) {
	// this.websocket.convertAndSend(
	// MESSAGE_PREFIX + "/deleteMember", getPath(member));
	// }

	// @HandleAfterSave // <3>
	// public void updateMember(Member member) {
	// this.websocket.convertAndSend(
	// MESSAGE_PREFIX + "/updateMember", getPath(member));
	// }

	// /**
	// * Take an {@link Member} and get the URI using Spring Data REST's
	// * {@link EntityLinks}.
	// *
	// * @param member
	// */
	// private String getPath(Member member) {
	// return this.entityLinks.linkForItemResource(member.getClass(),
	// member.getId()).toUri().getPath();
	// }

	// /**
	// * The @HandleBeforeCreate annotation gives you a chance to alter the incoming
	// * Employee record before it gets written to the database.
	// *
	// * @param member
	// */
	// @HandleBeforeCreate
	// @HandleBeforeSave
	// public void applyUserInformationUsingSecurityContext(Member member) {
	// String name =
	// SecurityContextHolder.getContext().getAuthentication().getName();
	// log.debug("applyUserInformationUsingSecurityContext {}", name);
	// // Manager manager = this.managerRepository.findByName(name);
	// // if (manager == null) {
	// // Manager newManager = new Manager();
	// // newManager.setName(name);
	// // newManager.setRoles(new String[] { "ROLE_MANAGER" });
	// // manager = this.managerRepository.save(newManager);
	// // }
	// // member.setManager(manager);
	// }

}
