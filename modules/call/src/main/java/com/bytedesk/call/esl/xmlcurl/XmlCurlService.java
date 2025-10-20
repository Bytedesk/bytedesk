package com.bytedesk.call.esl.xmlcurl;

import org.springframework.stereotype.Service;

/**
 * 最小可用的 mod_xml_curl XML 生成服务。
 * 注意：当前为演示/占位实现，实际项目应从数据库/配置中心读取用户、网关、路由等信息。
 */
@Service
public class XmlCurlService {

    /**
     * 生成 Directory 用户 XML。
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

    private static String xml(String s) {
        return s.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
