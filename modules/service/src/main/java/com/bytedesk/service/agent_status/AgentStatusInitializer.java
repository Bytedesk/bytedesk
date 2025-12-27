package com.bytedesk.service.agent_status;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AgentStatusInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
        initAuthority();
    }

    private void init() {
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = AgentStatusPermissions.AGENT_STATUS_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

}
