package com.bytedesk.call.xml_curl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bytedesk.call.call_settings.CallSettingsEntity;
import com.bytedesk.call.call_settings.CallSettingsRepository;

class DatabaseDirectoryXmlCurlProviderTest {

    @Test
    void provideDirectoryXmlBuildsXmlFromCallSettingsTarget() {
        CallSettingsRepository repository = Mockito.mock(CallSettingsRepository.class);
        CallSettingsEntity entity = CallSettingsEntity.builder()
            .enabled(true)
            .target("1008")
            .displayName("Alice")
            .number("4008001234")
            .build();
        entity.setUid("cs_001");
        when(repository.findAllByTargetInAndEnabledTrueAndDeletedFalse(Mockito.anyCollection()))
            .thenReturn(List.of(entity));

        DatabaseDirectoryXmlCurlProvider provider = new DatabaseDirectoryXmlCurlProvider(repository);

        var result = provider.provideDirectoryXml("1008", "pbx.local", Collections.emptyMap());

        assertTrue(result.isPresent());
        assertTrue(result.get().contains("<domain name=\"pbx.local\">"));
        assertTrue(result.get().contains("<user id=\"1008\">"));
        assertTrue(result.get().contains("<param name=\"vm-password\" value=\"1008\"/>"));
        assertTrue(result.get().contains("<param name=\"max-registrations-per-extension\" value=\"5\"/>"));
        assertTrue(result.get().contains("<param name=\"dial-string\""));
        assertTrue(result.get().contains("${sofia_contact(*/${dialed_user}@${dialed_domain})}"));
        assertTrue(result.get().contains("${verto_contact(${dialed_user}@${dialed_domain})}"));
        assertTrue(result.get().contains("effective_caller_id_name"));
        assertTrue(result.get().contains("value=\"Alice\""));
        assertTrue(result.get().contains("value=\"4008001234\""));
        assertTrue(result.get().contains("name=\"toll_allow\" value=\"domestic,international,local\""));
        assertTrue(result.get().contains("name=\"default_areacode\" value=\"$${default_areacode}\""));
        assertTrue(result.get().contains("name=\"default_gateway\" value=\"$${default_provider}\""));
        assertTrue(result.get().contains("call_settings_uid"));
    }

    @Test
    void provideDirectoryXmlReturnsEmptyWhenNoCallSettingsFound() {
        CallSettingsRepository repository = Mockito.mock(CallSettingsRepository.class);
        when(repository.findAllByTargetInAndEnabledTrueAndDeletedFalse(Mockito.anyCollection()))
            .thenReturn(List.of());

        DatabaseDirectoryXmlCurlProvider provider = new DatabaseDirectoryXmlCurlProvider(repository);

        var result = provider.provideDirectoryXml("1999", "pbx.local", Collections.emptyMap());

        assertFalse(result.isPresent());
    }
}