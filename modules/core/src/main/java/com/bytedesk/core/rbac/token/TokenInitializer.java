/*
 * @Author: jackning 270580156@qq.com
 * @Description: Token initializer
 */
package com.bytedesk.core.rbac.token;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TokenInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = TokenPermissions.TOKEN_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

}
