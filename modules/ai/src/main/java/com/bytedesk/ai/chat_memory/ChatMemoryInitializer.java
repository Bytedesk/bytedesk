/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   ChatMemory 权限初始化器。参考 RagRewriteInitializer 实现。
 */
package com.bytedesk.ai.chat_memory;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ChatMemoryInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
    }

    /**
     * 初始化 ChatMemory 平台级权限（CHAT_MEMORY_READ/CREATE/UPDATE/DELETE/EXPORT）。
     */
    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = ChatMemoryPermissions.CHAT_MEMORY_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
}
