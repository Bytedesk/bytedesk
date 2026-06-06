package com.bytedesk.call.xml_curl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.call.config.CallConstants;
import com.bytedesk.call.call_settings.CallSettingsEntity;
import com.bytedesk.call.call_settings.CallSettingsRepository;
import com.bytedesk.call.ip_blacklist.CallIpBlacklistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Order(10)
@RequiredArgsConstructor
public class DatabaseDirectoryXmlCurlProvider implements XmlCurlDirectoryProvider {

    private static final String DEFAULT_TOLL_ALLOW = "domestic,international,local";

    private static final String DEFAULT_PASSWORD = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_DEFAULT_PASSWORD,
        CallConstants.DEFAULT_FREESWITCH_PASSWORD);
    private static final String OUTBOUND_CALLER_NAME = System.getenv().getOrDefault(
        CallConstants.ENV_FREESWITCH_OUTBOUND_CALLER_NAME,
        CallConstants.DEFAULT_OUTBOUND_CALLER_NAME);

    private final CallSettingsRepository callSettingsRepository;
    private final CallIpBlacklistService callIpBlacklistService;

    @Override
    public Optional<String> provideDirectoryXml(String user, String domain, Map<String, String> params) {
        String normalizedUser = normalize(user);
        if (!StringUtils.hasText(normalizedUser)) {
            return Optional.empty();
        }

        String normalizedDomain = StringUtils.hasText(domain) ? domain.trim() : CallConstants.DIRECTORY_DOMAIN_DEFAULT;
        List<CallSettingsEntity> settings = callSettingsRepository.findAllByTargetInAndEnabledTrueAndDeletedFalse(
            buildTargetCandidates(normalizedUser, normalizedDomain));
        if (!settings.isEmpty()) {
            CallSettingsEntity matched = settings.get(0);
            String sourceIp = firstNonBlank(params,
                "sip_auth_network_ip",
                "variable_sip_auth_network_ip",
                "Caller-Network-Addr",
                "network_addr",
                "network_ip",
                "variable_network_addr",
                "variable_sip_network_ip",
                "sip_network_ip",
                "variable_sip_received_ip",
                "sip_received_ip",
                "variable_sip_via_host",
                "sip_via_host");
            if (callIpBlacklistService.isBlacklisted(matched.getOrgUid(), sourceIp)) {
                log.warn("xmlcurl.directory blocked blacklisted sourceIp='{}' user='{}' domain='{}' orgUid='{}' callSettingsUid='{}'",
                    sourceIp,
                    normalizedUser,
                    normalizedDomain,
                    matched.getOrgUid(),
                    matched.getUid());
                return Optional.empty();
            }
            if (log.isDebugEnabled()) {
                log.debug("xmlcurl.directory db hit user='{}' domain='{}' callSettingsUid='{}' target='{}'",
                    normalizedUser, normalizedDomain, matched.getUid(), matched.getTarget());
            }

            String callerName = StringUtils.hasText(matched.getDisplayName())
                ? matched.getDisplayName().trim()
                : OUTBOUND_CALLER_NAME;
            String callerNumber = StringUtils.hasText(matched.getNumber())
                ? matched.getNumber().trim()
                : normalizedUser;

            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<document type=\"freeswitch/xml\">\n"
                + "  <section name=\"directory\">\n"
                + "    <domain name=\"" + xmlEscape(normalizedDomain) + "\">\n"
                + "      <user id=\"" + xmlEscape(normalizedUser) + "\">\n"
                + "        <params>\n"
                + "          <param name=\"password\" value=\"" + xmlEscape(DEFAULT_PASSWORD) + "\"/>\n"
                + "          <param name=\"vm-password\" value=\"" + xmlEscape(normalizedUser) + "\"/>\n"
                + "          <param name=\"max-registrations-per-extension\" value=\"" + CallConstants.DEFAULT_MAX_REGISTRATIONS_PER_EXTENSION + "\"/>\n"
                + "          <param name=\"dial-string\" value=\"" + xmlEscape(CallConstants.DEFAULT_DIRECTORY_DIAL_STRING) + "\"/>\n"
                + "        </params>\n"
                + "        <variables>\n"
                + "          <variable name=\"toll_allow\" value=\"" + DEFAULT_TOLL_ALLOW + "\"/>\n"
                + "          <variable name=\"user_context\" value=\"" + CallConstants.USER_CONTEXT_DEFAULT + "\"/>\n"
                + "          <variable name=\"default_areacode\" value=\"" + CallConstants.FREESWITCH_VAR_DEFAULT_AREACODE + "\"/>\n"
                + "          <variable name=\"default_gateway\" value=\"" + CallConstants.FREESWITCH_VAR_DEFAULT_PROVIDER + "\"/>\n"
                + "          <variable name=\"effective_caller_id_name\" value=\"" + xmlEscape(callerName) + "\"/>\n"
                + "          <variable name=\"effective_caller_id_number\" value=\"" + xmlEscape(callerNumber) + "\"/>\n"
                + "          <variable name=\"outbound_caller_id_name\" value=\"" + xmlEscape(callerName) + "\"/>\n"
                + "          <variable name=\"outbound_caller_id_number\" value=\"" + xmlEscape(callerNumber) + "\"/>\n"
                + "          <variable name=\"call_settings_uid\" value=\"" + xmlEscape(matched.getUid()) + "\"/>\n"
                + "        </variables>\n"
                + "      </user>\n"
                + "    </domain>\n"
                + "  </section>\n"
                + "</document>\n";
            return Optional.of(xml);
        }

        return Optional.empty();
    }

    private static Set<String> buildTargetCandidates(String user, String domain) {
        Set<String> targets = new LinkedHashSet<>();
        targets.add(user);
        targets.add(user + "@" + domain);
        targets.add("sip:" + user + "@" + domain);
        targets.add("sip:" + user + "@" + CallConstants.DIRECTORY_DOMAIN_DEFAULT);
        return targets;
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private static String firstNonBlank(Map<String, String> params, String... keys) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
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