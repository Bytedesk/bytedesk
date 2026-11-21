package com.bytedesk.disk.archive;

import com.bytedesk.core.rbac.authority.AuthorityRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiskArchiveInitializer implements SmartInitializingSingleton {
    private final AuthorityRestService authorities;

    @Override
    public void afterSingletonsInstantiated() {
        DiskArchivePermissions.ALL.forEach(authorities::createForPlatform);
    }
}
