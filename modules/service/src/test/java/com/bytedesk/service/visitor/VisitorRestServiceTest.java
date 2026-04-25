package com.bytedesk.service.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.routing_strategy.ThreadRoutingContext;

class VisitorRestServiceTest {

    @Test
    void createShouldRetryExistingVisitorUpdateAfterOptimisticLockConflict() {
        VisitorRepository visitorRepository = org.mockito.Mockito.mock(VisitorRepository.class);
        ModelMapper modelMapper = org.mockito.Mockito.mock(ModelMapper.class);
        UidUtils uidUtils = org.mockito.Mockito.mock(UidUtils.class);
        ThreadRoutingContext threadRoutingContext = org.mockito.Mockito.mock(ThreadRoutingContext.class);
        ApplicationEventPublisher applicationEventPublisher = org.mockito.Mockito.mock(ApplicationEventPublisher.class);
        PlatformTransactionManager transactionManager = org.mockito.Mockito.mock(PlatformTransactionManager.class);

        VisitorRestService service = new VisitorRestService(
                visitorRepository,
                modelMapper,
                uidUtils,
                threadRoutingContext,
                applicationEventPublisher,
                transactionManager) {
            @Override
            public VisitorResponse convertToResponse(VisitorEntity entity) {
                return VisitorResponse.builder()
                        .uid(entity.getUid())
                        .nickname(entity.getNickname())
                        .build();
            }
        };

        VisitorRequest request = VisitorRequest.builder()
                .visitorUid("visitor_001")
                .orgUid("df_org_uid")
                .nickname("audio echo")
                .browser("Chrome")
                .os("macOS")
                .device("Desktop")
                .build();

        VisitorEntity stale = VisitorEntity.builder()
                .visitorUid("visitor_001")
                .nickname("old")
                .deviceInfo(new VisitorDevice())
                .build();
        stale.setId(7L);
        stale.setUid("visitor-uid");
        stale.setOrgUid("df_org_uid");

        VisitorEntity latest = VisitorEntity.builder()
                .visitorUid("visitor_001")
                .nickname("old")
                .deviceInfo(new VisitorDevice())
                .build();
        latest.setId(7L);
        latest.setUid("visitor-uid");
        latest.setOrgUid("df_org_uid");

        TransactionStatus tx1 = new SimpleTransactionStatus();
        TransactionStatus tx2 = new SimpleTransactionStatus();
        when(transactionManager.getTransaction(any())).thenReturn(tx1, tx2);
        when(visitorRepository.findByVisitorUidAndOrgUidAndDeleted(anyString(), anyString(), org.mockito.ArgumentMatchers.eq(false)))
                .thenAnswer(invocation -> Optional.of(stale))
                .thenAnswer(invocation -> Optional.of(latest));
        when(visitorRepository.saveAndFlush(any(VisitorEntity.class))).thenAnswer(invocation -> {
            VisitorEntity entity = invocation.getArgument(0);
            if (entity == stale) {
                throw new ObjectOptimisticLockingFailureException(VisitorEntity.class, stale.getId());
            }
            return entity;
        });

        VisitorResponse response = service.create(request);

        assertThat(response.getUid()).isEqualTo("visitor-uid");
        assertThat(response.getNickname()).isEqualTo("audio echo");
        assertThat(latest.getNickname()).isEqualTo("audio echo");
        assertThat(latest.getDeviceInfo().getBrowser()).isEqualTo("Chrome");
        verify(visitorRepository, times(1)).saveAndFlush(same(stale));
        verify(visitorRepository, times(1)).saveAndFlush(same(latest));
        verify(transactionManager, times(1)).commit(any(TransactionStatus.class));
        verify(transactionManager, times(1)).rollback(tx1);
    }
}