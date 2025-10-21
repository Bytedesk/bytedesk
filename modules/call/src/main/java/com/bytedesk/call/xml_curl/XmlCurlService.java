package com.bytedesk.call.xml_curl;

import org.springframework.stereotype.Service;

/**
 * 最小可用的 mod_xml_curl XML 生成服务。
 * 注意：当前为演示/占位实现，实际项目应从数据库/配置中心读取用户、网关、路由等信息。
 */
@Service
public class XmlCurlService {

    /**
     * 生成 Directory 用户 XML。
     * 
     * @param domain 域名
     * @param user   用户/分机号
     * @return freeswitch xml 文档
     */
    public String buildDirectoryUser(String domain, String user) {
        String safeDomain = (domain == null || domain.isBlank()) ? "default" : domain;
        String safeUser = (user == null || user.isBlank()) ? "1000" : user;
        // 注意：password 为示例，生产需从安全存储读取并加固
        String password = "1234";
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"directory\">\n" +
                "    <domain name=\"" + xml(safeDomain) + "\">\n" +
                "      <user id=\"" + xml(safeUser) + "\">\n" +
                "        <params>\n" +
                "          <param name=\"password\" value=\"" + xml(password) + "\"/>\n" +
                "        </params>\n" +
                "        <variables>\n" +
                "          <variable name=\"effective_caller_id_name\" value=\"" + xml(safeUser) + "\"/>\n" +
                "          <variable name=\"effective_caller_id_number\" value=\"" + xml(safeUser) + "\"/>\n" +
                "          <variable name=\"user_context\" value=\"default\"/>\n" +
                "        </variables>\n" +
                "      </user>\n" +
                "    </domain>\n" +
                "  </section>\n" +
                "</document>\n";
    }

    /**
     * 生成 Directory 用户 XML，支持可选覆盖项。
     */
    public String buildDirectoryUser(String domain, String user, DirectoryOptions options) {
        String safeDomain = (domain == null || domain.isBlank()) ? "default" : domain;
        String safeUser = (user == null || user.isBlank()) ? "1000" : user;
        String password = options != null && options.getPassword() != null && !options.getPassword().isBlank()
                ? options.getPassword()
                : "1234";
        String cidName = options != null && options.getCallerIdName() != null && !options.getCallerIdName().isBlank()
                ? options.getCallerIdName()
                : safeUser;
        String cidNumber = options != null && options.getCallerIdNumber() != null
                && !options.getCallerIdNumber().isBlank() ? options.getCallerIdNumber() : safeUser;
        String userCtx = options != null && options.getUserContext() != null && !options.getUserContext().isBlank()
                ? options.getUserContext()
                : "default";
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"directory\">\n" +
                "    <domain name=\"" + xml(safeDomain) + "\">\n" +
                "      <user id=\"" + xml(safeUser) + "\">\n" +
                "        <params>\n" +
                "          <param name=\"password\" value=\"" + xml(password) + "\"/>\n" +
                "        </params>\n" +
                "        <variables>\n" +
                "          <variable name=\"effective_caller_id_name\" value=\"" + xml(cidName) + "\"/>\n" +
                "          <variable name=\"effective_caller_id_number\" value=\"" + xml(cidNumber) + "\"/>\n" +
                "          <variable name=\"user_context\" value=\"" + xml(userCtx) + "\"/>\n" +
                "        </variables>\n" +
                "      </user>\n" +
                "    </domain>\n" +
                "  </section>\n" +
                "</document>\n";
    }

    /**
     * 生成 Dialplan XML（示例：简单应答并挂断；可据 context/destination_number 定制）。
     */
    public String buildDialplan(String context, String destinationNumber) {
        String ctx = (context == null || context.isBlank()) ? "default" : context;
        String dest = (destinationNumber == null || destinationNumber.isBlank()) ? "(.*)" : destinationNumber;
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"dialplan\" description=\"Bytedesk XML Curl Dialplan\">\n" +
                "    <context name=\"" + xml(ctx) + "\">\n" +
                "      <extension name=\"xmlcurl_demo\">\n" +
                "        <condition field=\"destination_number\" expression=\"^" + xml(dest) + "$\">\n" +
                "          <action application=\"answer\"/>\n" +
                "          <action application=\"sleep\" data=\"500\"/>\n" +
                "          <action application=\"hangup\"/>\n" +
                "        </condition>\n" +
                "      </extension>\n" +
                "    </context>\n" +
                "  </section>\n" +
                "</document>\n";
    }

    /**
     * 生成 Dialplan XML，支持可选自定义动作（bridge/playback/tts/noAnswer/sleep）。
     */
    public String buildDialplan(String context, String destinationNumber, DialplanOptions options) {
        String ctx = (context == null || context.isBlank()) ? "default" : context;
        String dest = (destinationNumber == null || destinationNumber.isBlank()) ? "(.*)" : destinationNumber;
        StringBuilder actions = new StringBuilder();
        boolean noAnswer = options != null && Boolean.TRUE.equals(options.getNoAnswer());
        Integer sleepMs = options != null ? options.getSleepMs() : null;
        String playback = options != null ? options.getPlaybackFile() : null;
        String ttsEngine = options != null ? options.getTtsEngine() : null;
        String ttsText = options != null ? options.getTtsText() : null;
        String bridge = options != null ? options.getBridgeEndpoint() : null;
        String ivrMenu = options != null ? options.getIvrMenu() : null;
        String queueName = options != null ? options.getQueueName() : null;
        String recordFile = options != null ? options.getRecordFile() : null;

        if (!noAnswer) {
            actions.append("          <action application=\"answer\"/>\n");
        }
        if (recordFile != null && !recordFile.isBlank()) {
            actions.append("          <action application=\"record_session\" data=\"" + xml(recordFile) + "\"/>\n");
        }
        if (sleepMs != null && sleepMs > 0) {
            actions.append("          <action application=\"sleep\" data=\"" + sleepMs + "\"/>\n");
        }
        if (ttsEngine != null && !ttsEngine.isBlank() && ttsText != null && !ttsText.isBlank()) {
            actions.append("          <action application=\"tts_commandline\" data=\"" + xml(ttsEngine) + "|"
                    + xml(ttsText) + "\"/>\n");
        }
        if (playback != null && !playback.isBlank()) {
            actions.append("          <action application=\"playback\" data=\"" + xml(playback) + "\"/>\n");
        }
        if (ivrMenu != null && !ivrMenu.isBlank()) {
            actions.append("          <action application=\"ivr\" data=\"" + xml(ivrMenu) + "\"/>\n");
        }
        if (queueName != null && !queueName.isBlank()) {
            actions.append("          <action application=\"callcenter\" data=\"" + xml(queueName) + "\"/>\n");
        }
        if (bridge != null && !bridge.isBlank()) {
            actions.append("          <action application=\"bridge\" data=\"" + xml(bridge) + "\"/>\n");
        } else {
            // 默认动作
            actions.append("          <action application=\"sleep\" data=\"500\"/>\n");
            actions.append("          <action application=\"hangup\"/>\n");
        }

        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"dialplan\" description=\"Bytedesk XML Curl Dialplan\">\n" +
                "    <context name=\"" + xml(ctx) + "\">\n" +
                "      <extension name=\"xmlcurl_dynamic\">\n" +
                "        <condition field=\"destination_number\" expression=\"^" + xml(dest) + "$\">\n" +
                actions.toString() +
                "        </condition>\n" +
                "      </extension>\n" +
                "    </context>\n" +
                "  </section>\n" +
                "</document>\n";
    }

    /**
     * 标准错误 XML。
     */
    public String buildError(String code, String message) {
        String c = (code == null || code.isBlank()) ? "error" : code;
        String m = (message == null || message.isBlank()) ? "unknown" : message;
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"result\">\n" +
                "    <result status=\"not found\" code=\"" + xml(c) + "\" text=\"" + xml(m) + "\"/>\n" +
                "  </section>\n" +
                "</document>\n";
    }

    /**
     * 未找到/无匹配结果 XML。
     */
    public String buildNotFound() {
        return buildError("not_found", "no matching document");
    }

    /**
     * phrases 示例，用于 mod_phrases（基础示例）。
     */
    public String buildPhrases(String lang) {
        String l = (lang == null || lang.isBlank()) ? "en" : lang;
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"phrases\">\n" +
                "    <macros language=\"" + xml(l) + "\">\n" +
                "      <macro name=\"welcome\">\n" +
                "        <input pattern=\"(.*)\">\n" +
                "          <match>\n" +
                "            <action function=\"play-file\" data=\"ivr/ivr-welcome_to_freeswitch.wav\"/>\n" +
                "          </match>\n" +
                "        </input>\n" +
                "      </macro>\n" +
                "    </macros>\n" +
                "  </section>\n" +
                "</document>\n";
    }

    /**
     * configuration 示例（非常简化，仅占位）。
     */
    public String buildConfiguration(String name) {
        String n = (name == null || name.isBlank()) ? "example" : name;
        // 动态下发 ivr.conf（最小可用演示），便于通过 <action application="ivr" data="main_menu"/> 启动菜单
        if ("ivr".equalsIgnoreCase(n) || "ivr.conf".equalsIgnoreCase(n)) {
            return buildIvrConf();
        }
        // 动态下发 callcenter.conf（最小可用演示）
        if ("callcenter".equalsIgnoreCase(n) || "callcenter.conf".equalsIgnoreCase(n)) {
            return buildCallcenterConf(null);
        }
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"configuration\">\n" +
                "    <configuration name=\"" + xml(n) + ".conf\" description=\"Bytedesk dynamic config\">\n" +
                "      <settings>\n" +
                "        <param name=\"dummy\" value=\"true\"/>\n" +
                "      </settings>\n" +
                "    </configuration>\n" +
                "  </section>\n" +
                "</document>\n";
    }

        public String buildConfiguration(String name, CallcenterOptions cc) {
            String n = (name == null || name.isBlank()) ? "example" : name;
            if ("callcenter".equalsIgnoreCase(n) || "callcenter.conf".equalsIgnoreCase(n)) {
                return buildCallcenterConf(cc);
            }
            return buildConfiguration(n);
        }

    /**
     * 生成最小可用的 ivr.conf，用于演示/默认 IVR 菜单。
     * 菜单名：main_menu
     * - 1 转 1000（XML default）
     * - 2 转 2000（XML default）
     * - 0 转 1000（人工）
     * - * 退出
     */
    private String buildIvrConf() {
        return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"configuration\">\n" +
                "    <configuration name=\"ivr.conf\" description=\"Bytedesk dynamic IVR\">\n" +
                "      <menus>\n" +
                "        <menu name=\"main_menu\"\n" +
                "              greet-long=\"ivr/ivr-welcome_to_freeswitch.wav\"\n" +
                "              greet-short=\"ivr/ivr-welcome_to_freeswitch.wav\"\n" +
                "              invalid-sound=\"ivr/ivr-that_was_an_invalid_entry.wav\"\n" +
                "              exit-sound=\"voicemail/vm-goodbye.wav\"\n" +
                "              timeout=\"5000\" max-failures=\"3\" digit-len=\"1\">\n" +
                "          <entry action=\"menu-exit\" digits=\"*\"/>\n" +
                "          <entry action=\"transfer\" digits=\"1\" data=\"1000 XML default\"/>\n" +
                "          <entry action=\"transfer\" digits=\"2\" data=\"2000 XML default\"/>\n" +
                "          <entry action=\"transfer\" digits=\"0\" data=\"1000 XML default\"/>\n" +
                "        </menu>\n" +
                "      </menus>\n" +
                "    </configuration>\n" +
                "  </section>\n" +
                "</document>\n";
    }

        /**
         * 生成最小可用的 callcenter.conf。
         * 优先使用 ODBC DSN；未提供时仅输出基础 settings。
         */
        private String buildCallcenterConf(CallcenterOptions opt) {
            String odbc = opt != null && opt.getOdbcDsn() != null && !opt.getOdbcDsn().isBlank() ? opt.getOdbcDsn() : null;
            String clientAddr = opt != null && opt.getClientAddress() != null && !opt.getClientAddress().isBlank() ? opt.getClientAddress() : "127.0.0.1";
            String debug = opt != null && opt.getDebug() != null && !opt.getDebug().isBlank() ? opt.getDebug() : "0";
            String cdr = opt != null && opt.getCdrLogDir() != null && !opt.getCdrLogDir().isBlank() ? opt.getCdrLogDir() : null;
            String create = opt != null && opt.getCreateTables() != null && !opt.getCreateTables().isBlank() ? opt.getCreateTables() : null;

            StringBuilder settings = new StringBuilder();
            settings.append("      <settings>\n");
            if (odbc != null) {
                settings.append("        <param name=\"odbc-dsn\" value=\"" + xml(odbc) + "\"/>\n");
            }
            settings.append("        <param name=\"cc-client-address\" value=\"" + xml(clientAddr) + "\"/>\n");
            settings.append("        <param name=\"debug-level\" value=\"" + xml(debug) + "\"/>\n");
            if (cdr != null) {
                settings.append("        <param name=\"queue-cdr-log-dir\" value=\"" + xml(cdr) + "\"/>\n");
            }
            if (create != null) {
                settings.append("        <param name=\"create-tables\" value=\"" + xml(create) + "\"/>\n");
            }
            settings.append("      </settings>\n");

            return "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document type=\"freeswitch/xml\">\n" +
                "  <section name=\"configuration\">\n" +
                "    <configuration name=\"callcenter.conf\" description=\"Bytedesk dynamic Callcenter\">\n" +
                     settings.toString() +
                "      <queues>\n" +
                "        <!-- 按需通过 ESL 或 DB 初始化队列/agent/binding；也可在此静态下发 -->\n" +
                "      </queues>\n" +
                "    </configuration>\n" +
                "  </section>\n" +
                "</document>\n";
        }

    private static String xml(String s) {
        return s.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
