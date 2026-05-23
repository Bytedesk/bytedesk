package com.bytedesk.call.xml_curl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;

import com.bytedesk.call.call_settings.CallSettingsEntity;
import com.bytedesk.call.call_settings.CallSettingsRepository;
import com.bytedesk.call.ip_blacklist.CallIpBlacklistService;

class XmlCurlServiceTest {

    @Test
    void handleDialplanShouldUseProviderEvenWhenDemoDialplanFlagIsDisabled() {
        XmlCurlDialplanProvider provider = (context, destinationNumber, params) -> Optional.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document type=\"freeswitch/xml\"><section name=\"dialplan\"/></document>");

        CallSettingsRepository repository = Mockito.mock(CallSettingsRepository.class);
        CallIpBlacklistService blacklistService = Mockito.mock(CallIpBlacklistService.class);
        when(repository.findAllByTargetInAndEnabledTrueAndDeletedFalse(anyCollection())).thenReturn(List.of());
        when(blacklistService.isBlacklisted(anyString(), anyString())).thenReturn(false);

        XmlCurlService service = new XmlCurlService(
            staticProvider(provider),
            emptyProvider(),
            emptyProvider(),
            repository,
            blacklistService);

        byte[] result = service.handleDialplan(Map.of(
            "Caller-Context", "default",
            "Caller-Destination-Number", "013311156272"));

        String xml = new String(result, StandardCharsets.UTF_8);
        assertTrue(xml.contains("<section name=\"dialplan\"/>"));
    }

    @Test
    void handleDialplanShouldReturnNotFoundWhenNoProviderMatchesAndDemoDialplanDisabled() {
        CallSettingsRepository repository = Mockito.mock(CallSettingsRepository.class);
        CallIpBlacklistService blacklistService = Mockito.mock(CallIpBlacklistService.class);
        when(repository.findAllByTargetInAndEnabledTrueAndDeletedFalse(anyCollection())).thenReturn(List.of());
        when(blacklistService.isBlacklisted(anyString(), anyString())).thenReturn(false);

        XmlCurlService service = new XmlCurlService(
            emptyProvider(),
            emptyProvider(),
            emptyProvider(),
            repository,
            blacklistService);

        byte[] result = service.handleDialplan(Map.of(
            "Caller-Context", "default",
            "Caller-Destination-Number", "5003"));

        String xml = new String(result, StandardCharsets.UTF_8);
        assertTrue(xml.contains("status=\"not found\""));
    }

    @Test
    void handleDialplanShouldReturnNotFoundWhenSourceIpIsBlacklisted() {
        CallSettingsRepository repository = Mockito.mock(CallSettingsRepository.class);
        CallIpBlacklistService blacklistService = Mockito.mock(CallIpBlacklistService.class);
        CallSettingsEntity entity = CallSettingsEntity.builder()
            .enabled(true)
            .target("1008")
            .build();
        entity.setOrgUid("org_001");
        when(repository.findAllByTargetInAndEnabledTrueAndDeletedFalse(anyCollection())).thenReturn(List.of(entity));
        when(blacklistService.isBlacklisted("org_001", "87.106.78.3")).thenReturn(true);

        XmlCurlDialplanProvider provider = (context, destinationNumber, params) -> Optional.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document type=\"freeswitch/xml\"><section name=\"dialplan\"/></document>");

        XmlCurlService service = new XmlCurlService(
            staticProvider(provider),
            emptyProvider(),
            emptyProvider(),
            repository,
            blacklistService);

        byte[] result = service.handleDialplan(Map.of(
            "Caller-Context", "default",
            "Caller-Destination-Number", "5003",
            "Caller-Username", "1008",
            "sip_auth_network_ip", "87.106.78.3"));

        String xml = new String(result, StandardCharsets.UTF_8);
        assertTrue(xml.contains("status=\"not found\""));
    }

    private static <T> ObjectProvider<T> emptyProvider() {
        return new SimpleObjectProvider<>();
    }

    private static <T> ObjectProvider<T> staticProvider(T bean) {
        return new SimpleObjectProvider<>(bean);
    }

    private static final class SimpleObjectProvider<T> implements ObjectProvider<T> {

        private final T bean;

        private SimpleObjectProvider() {
            this.bean = null;
        }

        private SimpleObjectProvider(T bean) {
            this.bean = bean;
        }

        @Override
        public T getObject(Object... args) {
            return bean;
        }

        @Override
        public T getIfAvailable() {
            return bean;
        }

        @Override
        public T getIfUnique() {
            return bean;
        }

        @Override
        public T getObject() {
            return bean;
        }

        @Override
        public java.util.stream.Stream<T> orderedStream() {
            return bean == null ? java.util.stream.Stream.empty() : java.util.stream.Stream.of(bean);
        }
    }
}