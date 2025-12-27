package com.bytedesk.ai.robot_settings;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RobotSettingsInitializer implements SmartInitializingSingleton {

    private final RobotSettingsRestService robotSettingsRestService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        robotSettingsRestService.getOrCreateDefault(orgUid);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = RobotSettingsPermissions.ROBOT_SETTINGS_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
}
