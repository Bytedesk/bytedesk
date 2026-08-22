package com.bytedesk.call.call_ip_blacklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bytedesk.core.uid.UidUtils;

class CallIpBlacklistServiceTest {

    @Test
    void isBlacklistedFallsBackToGlobalIpWhenOrgScopedEntryMissing() {
        CallIpBlacklistRepository repository = Mockito.mock(CallIpBlacklistRepository.class);
        UidUtils uidUtils = Mockito.mock(UidUtils.class);
        when(repository.findAllByOrgUidAndIpAddressAndDeletedFalseOrderByIdAsc("org_001", "87.106.78.3"))
            .thenReturn(List.of());
        when(repository.findAllByIpAddressAndDeletedFalseOrderByIdAsc("87.106.78.3"))
            .thenReturn(List.of(CallIpBlacklistEntity.builder().ipAddress("87.106.78.3").build()));

        CallIpBlacklistService service = new CallIpBlacklistService(repository, uidUtils);

        assertTrue(service.isBlacklisted("org_001", "87.106.78.3"));
        verify(repository).findAllByOrgUidAndIpAddressAndDeletedFalseOrderByIdAsc("org_001", "87.106.78.3");
        verify(repository).findAllByIpAddressAndDeletedFalseOrderByIdAsc("87.106.78.3");
    }

    @Test
    void blacklistSourceIpToleratesDuplicateRowsAndReusesEarliest() {
        CallIpBlacklistRepository repository = Mockito.mock(CallIpBlacklistRepository.class);
        UidUtils uidUtils = Mockito.mock(UidUtils.class);
        CallIpBlacklistEntity earliest = CallIpBlacklistEntity.builder().ipAddress("45.143.198.70").build();
        CallIpBlacklistEntity duplicate = CallIpBlacklistEntity.builder().ipAddress("45.143.198.70").build();
        when(repository.findAllByIpAddressAndDeletedFalseOrderByIdAsc("45.143.198.70"))
            .thenReturn(List.of(earliest, duplicate));

        CallIpBlacklistService service = new CallIpBlacklistService(repository, uidUtils);

        CallIpBlacklistEntity result = service.blacklistSourceIp(
                null, "45.143.198.70", "CUSTOM", "2350844", null, "Auto-blocked from CUSTOM sofia::wrong_call_state");

        assertEquals(earliest, result);
        verify(repository, Mockito.never()).save(Mockito.any());
    }
}