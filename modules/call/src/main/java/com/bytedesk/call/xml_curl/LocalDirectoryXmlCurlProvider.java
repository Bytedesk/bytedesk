package com.bytedesk.call.xml_curl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.call.config.CallConstants;
import com.bytedesk.call.config.CallFreeswitchProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(XmlCurlDirectoryProvider.class)
public class LocalDirectoryXmlCurlProvider implements XmlCurlDirectoryProvider {

    private static final Pattern USER_PATTERN = Pattern.compile("(?s)<user\\b.*?</user>");
    private static final String DEFAULT_PASSWORD = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_DEFAULT_PASSWORD,
        CallConstants.DEFAULT_FREESWITCH_PASSWORD);
    private static final String OUTBOUND_CALLER_NAME = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_OUTBOUND_CALLER_NAME,
        CallConstants.DEFAULT_OUTBOUND_CALLER_NAME);
    private static final String OUTBOUND_CALLER_ID = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_OUTBOUND_CALLER_ID,
        CallConstants.DEFAULT_OUTBOUND_CALLER_ID);

    private final CallFreeswitchProperties callFreeswitchProperties;

    @Override
    public Optional<String> provideDirectoryXml(String user, String domain, Map<String, String> params) {
        String normalizedUser = normalize(user, "");
        if (!StringUtils.hasText(normalizedUser)) {
            return Optional.empty();
        }

        String normalizedDomain = normalize(domain, CallConstants.DIRECTORY_DOMAIN_DEFAULT);
        Path userFile = resolveUserFile(normalizedUser, normalizedDomain);
        if (userFile == null) {
            return Optional.empty();
        }

        try {
            String fileContent = Files.readString(userFile);
            Matcher matcher = USER_PATTERN.matcher(fileContent);
            if (!matcher.find()) {
                log.debug("Local xmlcurl directory file missing <user> node: {}", userFile);
                return Optional.empty();
            }

            String userXml = expandKnownVars(matcher.group());
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<document type=\"freeswitch/xml\">\n"
                    + "  <section name=\"directory\">\n"
                    + "    <domain name=\"" + xmlEscape(normalizedDomain) + "\">\n"
                    + indent(userXml.trim(), 6) + "\n"
                    + "    </domain>\n"
                    + "  </section>\n"
                    + "</document>\n";
            return Optional.of(xml);
        } catch (IOException ex) {
            log.warn("Read local xmlcurl directory file failed: {}", userFile, ex);
            return Optional.empty();
        }
    }

    private Path resolveUserFile(String user, String domain) {
        Path directoryRoot = callFreeswitchProperties.resolveConfDirPath().resolve(CallConstants.DIRECTORY_NAME);

        Path domainFile = directoryRoot.resolve(domain).resolve(user + ".xml");
        if (Files.exists(domainFile)) {
            return domainFile;
        }

        Path defaultFile = directoryRoot.resolve(CallConstants.DIRECTORY_DOMAIN_DEFAULT).resolve(user + ".xml");
        if (Files.exists(defaultFile)) {
            return defaultFile;
        }
        return null;
    }

    private static String expandKnownVars(String xml) {
        return xml
                .replace(CallConstants.FREESWITCH_VAR_DEFAULT_PASSWORD, xmlEscape(DEFAULT_PASSWORD))
                .replace("$${outbound_caller_name}", xmlEscape(OUTBOUND_CALLER_NAME))
                .replace("$${outbound_caller_id}", xmlEscape(OUTBOUND_CALLER_ID));
    }

    private static String normalize(String value, String fallback) {
        if (!StringUtils.hasText(value)) {
            return fallback;
        }
        return value.trim();
    }

    private static String indent(String text, int spaces) {
        String prefix = " ".repeat(spaces);
        return prefix + text.replace("\n", "\n" + prefix);
    }

    private static String xmlEscape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}