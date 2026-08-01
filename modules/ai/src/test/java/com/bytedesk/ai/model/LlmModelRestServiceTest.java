package com.bytedesk.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class LlmModelRestServiceTest {

    @Mock
    private LlmModelRepository llmModelRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    private LlmModelRestService llmModelRestService;

    @BeforeEach
    void setUp() {
        llmModelRestService = new LlmModelRestService(
                llmModelRepository,
                new ModelMapper(),
                uidUtils,
                authService);
    }

    @Test
    void createReturnsExistingModelWhenConcurrentInsertCausesOptimisticLocking() {
        LlmModelRequest request = LlmModelRequest.builder()
                .name("moonshot-v1")
                .providerUid("provider-1")
                .build();

        LlmModelEntity existingEntity = LlmModelEntity.builder()
                .id(130L)
                .uid("existing-uid")
                .name("moonshot-v1")
                .providerUid("provider-1")
                .build();

        when(uidUtils.getUid()).thenReturn("new-uid");
        when(authService.getCurrentUser()).thenReturn(null);
        when(llmModelRepository.findByNameAndProviderUid("moonshot-v1", "provider-1"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingEntity));
        when(llmModelRepository.save(any(LlmModelEntity.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(LlmModelEntity.class, 130L));
        when(llmModelRepository.findByUid("new-uid")).thenReturn(Optional.empty());

        LlmModelResponse response = llmModelRestService.create(request);

        assertNotNull(response);
        assertEquals("existing-uid", response.getUid());
        assertEquals("moonshot-v1", response.getName());
        assertEquals("provider-1", response.getProviderUid());
    }
}