package com.bytedesk.core.rbac.role;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.event.GenericApplicationEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RoleEventListener {
    

    @EventListener
    public void onRoleCreateEvent(GenericApplicationEvent<RoleCreateEvent> event) {
        RoleCreateEvent roleCreateEvent = event.getObject();
        RoleEntity roleEntity = roleCreateEvent.getRoleEntity();
        log.info("onRoleCreateEvent: {}", roleEntity.toString());
    }

    // @EventListener
    // public void onRoleUpdateEvent(GenericApplicationEvent<RoleUpdateEvent> event) {
    //     RoleUpdateEvent roleUpdateEvent = event.getObject();
    //     RoleEntity roleEntity = roleUpdateEvent.getRoleEntity();
    //     log.info("onRoleUpdateEvent: {}", roleEntity.toString());
    // }

}
