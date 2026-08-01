package com.bytedesk.call.call_ip_blacklist;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bytedesk.core.uid.UidUtils;

class CallIpBlacklistServiceTest {

    @Test
    void isBlacklistedFallsBackToGlobalIpWhenOrgScopedEntryMissing() {
        CallIpBlacklistRepository repository = Mockito.mock(CallIpBlacklistRepository.class);
        UidUtils uidUtils = Mockito.mock(UidUtils.class);
        when(repository.findByOrgUidAndIpAddressAndDeletedFalse("org_001", "87.106.78.3"))
            .thenReturn(Optional.empty());
        when(repository.findByIpAddressAndDeletedFalse("87.106.78.3"))
            .thenReturn(Optional.of(CallIpBlacklistEntity.builder().ipAddress("87.106.78.3").build()));

        CallIpBlacklistService service = new CallIpBlacklistService(repository, uidUtils);

        assertTrue(service.isBlacklisted("org_001", "87.106.78.3"));
        verify(repository).findByOrgUidAndIpAddressAndDeletedFalse("org_001", "87.106.78.3");
        verify(repository).findByIpAddressAndDeletedFalse("87.106.78.3");
    }
}