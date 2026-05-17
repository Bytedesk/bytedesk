package com.bytedesk.call.xml_curl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.call.config.CallConstants;
import com.bytedesk.call.config.CallFreeswitchProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Order(100)
@RequiredArgsConstructor
public class LocalDirectoryXmlCurlProvider implements XmlCurlDirectoryProvider {

    private static final Pattern USER_PATTERN = Pattern.compile("(?s)<user\\b.*?</user>");
    private static final Pattern VARIABLES_PATTERN = Pattern.compile("(?s)<variables>.*?</variables>");
    private static final String DEFAULT_PASSWORD = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_DEFAULT_PASSWORD,
        CallConstants.DEFAULT_FREESWITCH_PASSWORD);
    private static final String OUTBOUND_CALLER_NAME = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_OUTBOUND_CALLER_NAME,
        CallConstants.DEFAULT_OUTBOUND_CALLER_NAME);
    private static final String OUTBOUND_CALLER_ID = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_OUTBOUND_CALLER_ID,
        CallConstants.DEFAULT_OUTBOUND_CALLER_ID);
    private static final String DEFAULT_TOLL_ALLOW = "domestic,international,local";

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

                String userXml = ensureDefaultDialingVariables(expandKnownVars(matcher.group()));
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

        Path defaultRoot = directoryRoot.resolve(CallConstants.DIRECTORY_DOMAIN_DEFAULT);
        if (!Files.isDirectory(defaultRoot)) {
            return null;
        }

        List<Path> scopedMatches = new ArrayList<>();
        try (var stream = Files.list(defaultRoot)) {
            stream.filter(Files::isDirectory)
                    .map(path -> path.resolve(user + ".xml"))
                    .filter(Files::exists)
                    .forEach(scopedMatches::add);
        } catch (IOException ex) {
            log.warn("Scan local xmlcurl scoped directory files failed: {}", defaultRoot, ex);
            return null;
        }

        if (scopedMatches.size() == 1) {
            return scopedMatches.get(0);
        }
        if (scopedMatches.size() > 1) {
            log.warn("Local xmlcurl scoped directory file is ambiguous for user {}: {}", user, scopedMatches);
        }
        return null;
    }

    private static String expandKnownVars(String xml) {
        return xml
                .replace(CallConstants.FREESWITCH_VAR_DEFAULT_PASSWORD, xmlEscape(DEFAULT_PASSWORD))
                .replace("$${outbound_caller_name}", xmlEscape(OUTBOUND_CALLER_NAME))
                .replace("$${outbound_caller_id}", xmlEscape(OUTBOUND_CALLER_ID));
    }

    private static String ensureDefaultDialingVariables(String userXml) {
        String variablesBlock = buildMissingDialingVariables(userXml);
        if (variablesBlock.isEmpty()) {
            return userXml;
        }

        Matcher matcher = VARIABLES_PATTERN.matcher(userXml);
        if (matcher.find()) {
            String variables = matcher.group();
            String updatedVariables = variables.replace("</variables>", variablesBlock + "\n      </variables>");
            return matcher.replaceFirst(Matcher.quoteReplacement(updatedVariables));
        }

        String insertion = "    <variables>\n" + variablesBlock + "\n    </variables>\n";
        int paramsEnd = userXml.indexOf("</params>");
        if (paramsEnd >= 0) {
            int insertPos = paramsEnd + "</params>".length();
            return userXml.substring(0, insertPos) + "\n" + insertion + userXml.substring(insertPos);
        }
        return userXml.replace("</user>", insertion + "</user>");
    }

    private static String buildMissingDialingVariables(String userXml) {
        List<String> missingVariables = new ArrayList<>();
        if (!userXml.contains("name=\"toll_allow\"")) {
            missingVariables.add("      <variable name=\"toll_allow\" value=\"" + DEFAULT_TOLL_ALLOW + "\"/>");
        }
        if (!userXml.contains("name=\"default_areacode\"")) {
            missingVariables.add("      <variable name=\"default_areacode\" value=\"" + xmlEscape(CallConstants.FREESWITCH_VAR_DEFAULT_AREACODE) + "\"/>");
        }
        if (!userXml.contains("name=\"default_gateway\"")) {
            missingVariables.add("      <variable name=\"default_gateway\" value=\"" + xmlEscape(CallConstants.FREESWITCH_VAR_DEFAULT_PROVIDER) + "\"/>");
        }
        return String.join("\n", missingVariables);
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