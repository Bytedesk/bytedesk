package com.bytedesk.disk.permission;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 网盘模块初始化：注册模块权限
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiskInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        log.info("DiskInitializer initialization started...");
        initAuthority();
        log.info("DiskInitializer initialization completed");
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = DiskPermissions.DISK_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
}
