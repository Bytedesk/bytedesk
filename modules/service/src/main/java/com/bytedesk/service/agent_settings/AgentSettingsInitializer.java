package com.bytedesk.service.agent_settings;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AgentSettingsInitializer implements SmartInitializingSingleton {

    private final AgentSettingsRestService agentSettingsRestService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        agentSettingsRestService.getOrCreateDefault(orgUid);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = AgentSettingsPermissions.AGENT_SETTINGS_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
}
