package com.bytedesk.kbase.llm_faq;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.translation.KbaseTranslationSyncService;

class FaqResponseMappingTests {

    @Test
    void shouldMapRouteToRobotFlagToFaqResponses() {
        ModelMapper modelMapper = new ModelMapper();
        FaqEntity faq = FaqEntity.builder()
                .question("如何查询我的预订信息？")
                .answer("请提供预订号")
                .routeToRobot(true)
                .build();

        FaqResponse faqResponse = modelMapper.map(faq, FaqResponse.class);
        FaqResponseVisitor visitorResponse = modelMapper.map(faq, FaqResponseVisitor.class);

        assertTrue(Boolean.TRUE.equals(faqResponse.getRouteToRobot()));
        assertTrue(Boolean.TRUE.equals(visitorResponse.getRouteToRobot()));
    }

    @Test
    void shouldPersistRouteToRobotWhenUpdatingFaq() {
        FaqRepository faqRepository = mock(FaqRepository.class);
        FaqRestService faqRestService = new FaqRestService(
                faqRepository,
                new ModelMapper(),
                mock(UidUtils.class),
                mock(CategoryRestService.class),
                mock(FaqJsonLoader.class),
                mock(KbaseRestService.class),
                mock(ThreadRestService.class),
                mock(MessageRestService.class),
                mock(BytedeskEventPublisher.class),
                mock(KbaseTranslationSyncService.class));

        FaqEntity existing = FaqEntity.builder()
                .uid("faq-1")
                .question("如何查询我的预订信息？")
                .answer("请提供预订号")
                .routeToRobot(false)
                .build();

        FaqRequest request = FaqRequest.builder()
                .uid("faq-1")
                .question("如何查询我的预订信息？")
                .answer("请提供预订号")
                .routeToRobot(true)
                .build();

        when(faqRepository.findByUid("faq-1")).thenReturn(java.util.Optional.of(existing));
        when(faqRepository.save(any(FaqEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FaqResponse response = faqRestService.update(request);

        assertTrue(Boolean.TRUE.equals(response.getRouteToRobot()));
        verify(faqRepository).save(any(FaqEntity.class));
    }
}