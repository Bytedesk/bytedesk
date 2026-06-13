package com.bytedesk.call.call_ip_blacklist;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CallIpBlacklistInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = CallIpBlacklistPermissions.CALL_IP_BLACKLIST_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
}