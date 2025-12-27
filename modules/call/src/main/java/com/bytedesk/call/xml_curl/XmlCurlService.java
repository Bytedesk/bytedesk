package com.bytedesk.call.xml_curl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 最小可用的 mod_xml_curl XML 生成服务。
 * 注意：当前为演示/占位实现，实际项目应从数据库/配置中心读取用户、网关、路由等信息。
 */
@Slf4j
@Service
public class XmlCurlService {

    // 开关：默认全部关闭，仅在联调或按需开启对应 section 的动态返回
    private static final boolean ENABLE_DIALPLAN       = boolEnv("XMLCURL_ENABLE_DIALPLAN", false);
    private static final boolean ENABLE_DIRECTORY      = boolEnv("XMLCURL_ENABLE_DIRECTORY", false);
    private static final boolean ENABLE_CONFIGURATION  = boolEnv("XMLCURL_ENABLE_CONFIGURATION", false);
    private static final boolean ENABLE_PHRASES        = boolEnv("XMLCURL_ENABLE_PHRASES", false);

    // directory 用户清单（user@domain:password），未写域名默认 default
    private static final Map<String, String> DIRECTORY_USERS = usersEnv("XMLCURL_DIRECTORY_USERS", "1000@default:1234");
    // configuration 白名单（示例："ivr.conf,acl.conf"），默认空：不返回任何配置，由本地接管
    private static final Set<String> CONFIG_WHITELIST = csvEnv("XMLCURL_CONFIG_WHITELIST", "");

    public byte[] handleDialplan(Map<String, String> p) {
        if (!ENABLE_DIALPLAN) {
            logDisabled("dialplan", "XMLCURL_ENABLE_DIALPLAN");
            return resultNotFound();
        }
        String context = pick(p, "Caller-Context", "context", "variable_context");
        if (context == null || context.isBlank()) context = "default";
        String dest = pick(p, "Caller-Destination-Number", "destination_number", "variable_destination_number");
        if (dest == null) {
            logNotFound("dialplan", "missing destination_number");
            return resultNotFound();
        }
        if (log.isDebugEnabled()) {
            log.debug("xmlcurl.dialplan context='{}' dest='{}'", context, dest);
        }
        // 示例：仅对 9297 返回一个最小拨号计划片段（长音播放），其余走本地
        if (!dest.equals("9297")) {
            logNotFound("dialplan", "dest not match demo: " + dest);
            return resultNotFound();
        }
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"dialplan\">\n" +
                "    <context name=\"" + xmlEscape(context) + "\">\n" +
                "      <extension name=\"xmlcurl-9297-demo\">\n" +
                "        <condition field=\"destination_number\" expression=\"^9297$\">\n" +
                "          <action application=\"answer\"/>\n" +
                "          <action application=\"playback\" data=\"tone_stream://%(30000,0,440)\"/>\n" +
                "          <action application=\"hangup\"/>\n" +
                "        </condition>\n" +
                "      </extension>\n" +
                "    </context>\n" +
                "  </section>\n" +
                "</document>\n";
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] handleDirectory(Map<String, String> p) {
        if (!ENABLE_DIRECTORY) {
            logDisabled("directory", "XMLCURL_ENABLE_DIRECTORY");
            return resultNotFound();
        }
        String user = pick(p, "user", "User", "login", "variable_user_name", "Caller-Username");
        String domain = pick(p, "domain", "Domain", "variable_domain_name", "sip_from_host");
        if (domain == null || domain.isBlank()) domain = "default";
        if (user == null || user.isBlank()) {
            logNotFound("directory", "missing user");
            return resultNotFound();
        }
        String key = (user + "@" + domain).toLowerCase(Locale.ROOT);
        String pwd = DIRECTORY_USERS.get(key);
        if (pwd == null) {
            // 兼容仅配置 user:pwd 的情况（默认 domain=default）
            pwd = DIRECTORY_USERS.get((user + "@default").toLowerCase(Locale.ROOT));
        }
        if (pwd == null) {
            logNotFound("directory", "user not in DIRECTORY_USERS: " + key);
            return resultNotFound();
        }
        if (log.isDebugEnabled()) {
            log.debug("xmlcurl.directory user='{}' domain='{}' found=true", user, domain);
        }
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"directory\">\n" +
                "    <domain name=\"" + xmlEscape(domain) + "\">\n" +
                "      <user id=\"" + xmlEscape(user) + "\">\n" +
                "        <params>\n" +
                "          <param name=\"password\" value=\"" + xmlEscape(pwd) + "\"/>\n" +
                "        </params>\n" +
                "        <variables>\n" +
                "          <variable name=\"user_context\" value=\"default\"/>\n" +
                "        </variables>\n" +
                "      </user>\n" +
                "    </domain>\n" +
                "  </section>\n" +
                "</document>\n";
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] handleConfiguration(Map<String, String> p) {
        if (!ENABLE_CONFIGURATION) {
            logDisabled("configuration", "XMLCURL_ENABLE_CONFIGURATION");
            return resultNotFound();
        }
        String cfgName = pick(p, "key_value", "Configuration-Name", "configuration", "name");
        if (cfgName == null || cfgName.isBlank()) {
            logNotFound("configuration", "missing configuration name");
            return resultNotFound();
        }
        cfgName = cfgName.trim();
        if (!CONFIG_WHITELIST.contains(cfgName)) {
            logNotFound("configuration", "name not whitelisted: " + cfgName);
            return resultNotFound();
        }
        if (log.isDebugEnabled()) {
            log.debug("xmlcurl.configuration name='{}'", cfgName);
        }
        if (cfgName.equals("ivr.conf")) {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<document type=\"freeswitch/xml\">\n" +
                    "  <section name=\"configuration\">\n" +
                    "    <configuration name=\"ivr.conf\" description=\"xml_curl demo\">\n" +
                    "      <menus/>\n" +
                    "      <phrases/>\n" +
                    "    </configuration>\n" +
                    "  </section>\n" +
                    "</document>\n";
            return xml.getBytes(StandardCharsets.UTF_8);
        }
        return resultNotFound();
    }

    public byte[] handlePhrases(Map<String, String> p) {
        if (!ENABLE_PHRASES) {
            logDisabled("phrases", "XMLCURL_ENABLE_PHRASES");
            return resultNotFound();
        }
        if (log.isDebugEnabled()) {
            log.debug("xmlcurl.phrases request");
        }
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"phrases\">\n" +
                "    <macros>\n" +
                "      <macro name=\"xmlcurl-demo\">\n" +
                "        <input pattern=\"(.*)\"/>\n" +
                "        <match>\n" +
                "          <action function=\"speak-text\" data=\"unimrcp:default:你好，这里是 xml_curl 短语演示。\"/>\n" +
                "        </match>\n" +
                "      </macro>\n" +
                "    </macros>\n" +
                "  </section>\n" +
                "</document>\n";
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] resultNotFound() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"result\">\n" +
                "    <result status=\"not found\"/>\n" +
                "  </section>\n" +
                "</document>\n";
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    // --- helpers ---
    private static void logDisabled(String section, String envKey) {
        // 用 info 便于默认日志级别下也能观察到未启用原因
        log.info("xmlcurl.{} disabled (enable by env {})", section, envKey);
    }

    private static void logNotFound(String section, String reason) {
        if (log.isDebugEnabled()) {
            log.debug("xmlcurl.{} not-found: {}", section, reason);
        }
    }
    private static String pick(Map<String, String> m, String... keys) {
        for (String k : keys) {
            String v = m.get(k);
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String xmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }

    private static boolean boolEnv(String key, boolean def) {
        String v = System.getenv(key);
        if (v == null) return def;
        v = v.trim().toLowerCase(Locale.ROOT);
        return v.equals("1") || v.equals("true") || v.equals("yes");
    }

    private static Map<String, String> usersEnv(String key, String def) {
        String v = Optional.ofNullable(System.getenv(key)).filter(s -> !s.isBlank()).orElse(def);
        Map<String, String> m = new HashMap<>();
        for (String item : v.split(",")) {
            String t = item.trim();
            if (t.isEmpty()) continue;
            int i = t.indexOf(':');
            if (i <= 0) continue;
            String left = t.substring(0, i).trim();
            String pwd = t.substring(i + 1).trim();
            String user;
            String dom;
            int j = left.indexOf('@');
            if (j > 0) { user = left.substring(0, j); dom = left.substring(j + 1); }
            else { user = left; dom = "default"; }
            if (!user.isEmpty()) {
                m.put((user + "@" + dom).toLowerCase(Locale.ROOT), pwd);
            }
        }
        return m;
    }

    private static Set<String> csvEnv(String key, String def) {
        String v = Optional.ofNullable(System.getenv(key)).filter(s -> !s.isBlank()).orElse(def);
        Set<String> set = new HashSet<>();
        for (String p : v.split(",")) {
            String token = p.trim();
            if (token.isEmpty()) continue;
            // 支持数字范围：如 1000-1019
            if (token.matches("\\d+\\-\\d+")) {
                try {
                    String[] parts = token.split("-");
                    int start = Integer.parseInt(parts[0]);
                    int end = Integer.parseInt(parts[1]);
                    if (start <= end && (end - start) <= 10000) {
                        for (int i = start; i <= end; i++) {
                            set.add(String.valueOf(i));
                        }
                        continue;
                    }
                } catch (NumberFormatException ignore) {
                    // fallthrough
                }
            }
            set.add(token);
        }
        return set;
    }
}
