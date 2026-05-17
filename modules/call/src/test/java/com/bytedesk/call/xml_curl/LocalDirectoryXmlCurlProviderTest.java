package com.bytedesk.call.xml_curl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.bytedesk.call.config.CallFreeswitchProperties;

class LocalDirectoryXmlCurlProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void provideDirectoryXmlFallsBackToDefaultDirectoryAndExpandsKnownVars() throws IOException {
        Path confDir = tempDir.resolve("conf");
        Path userFile = confDir.resolve("directory").resolve("default").resolve("1003.xml");
        Files.createDirectories(userFile.getParent());
        Files.writeString(userFile, """
                <include>
                  <user id="1003">
                    <params>
                      <param name="password" value="$${default_password}"/>
                    </params>
                    <variables>
                      <variable name="user_context" value="default"/>
                      <variable name="outbound_caller_id_name" value="$${outbound_caller_name}"/>
                    </variables>
                  </user>
                </include>
                """);

        CallFreeswitchProperties properties = new CallFreeswitchProperties();
        properties.setConfDir(confDir.toString());

        LocalDirectoryXmlCurlProvider provider = new LocalDirectoryXmlCurlProvider(properties);

        var result = provider.provideDirectoryXml("1003", "localhost", Collections.emptyMap());

        assertTrue(result.isPresent());
        assertTrue(result.get().contains("<section name=\"directory\">"));
        assertTrue(result.get().contains("<domain name=\"localhost\">"));
        assertTrue(result.get().contains("<user id=\"1003\">"));
        assertTrue(result.get().contains("value=\"12345679\""));
        assertTrue(result.get().contains("value=\"FreeSWITCH\""));
        assertTrue(result.get().contains("name=\"toll_allow\" value=\"domestic,international,local\""));
        assertTrue(result.get().contains("name=\"default_areacode\" value=\"$${default_areacode}\""));
        assertTrue(result.get().contains("name=\"default_gateway\" value=\"$${default_provider}\""));
    }

    @Test
    void provideDirectoryXmlReturnsEmptyWhenUserFileMissing() {
        CallFreeswitchProperties properties = new CallFreeswitchProperties();
        properties.setConfDir(tempDir.resolve("conf").toString());

        LocalDirectoryXmlCurlProvider provider = new LocalDirectoryXmlCurlProvider(properties);

        var result = provider.provideDirectoryXml("9999", "localhost", Collections.emptyMap());

        assertFalse(result.isPresent());
    }
}