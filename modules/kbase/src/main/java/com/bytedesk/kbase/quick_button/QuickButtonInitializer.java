package com.bytedesk.kbase.quick_button;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QuickButtonInitializer implements SmartInitializingSingleton {
    
    private final AuthorityRestService authorityRestService;
    
    @Override
    public void afterSingletonsInstantiated() {
        // 
        initPermissions();
    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = QuickButtonPermissions.QUICKBUTTON_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
    
}
    