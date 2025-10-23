package com.bytedesk.call.httapi;


public class HttapiXml {
    private final StringBuilder sb = new StringBuilder();
    private boolean opened = false;

    public HttapiXml() {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<document type=\"xml/freeswitch-httapi\">\n");
        sb.append("  <params/>\n");
        sb.append("  <variables/>\n");
        sb.append("  <work>\n");
        opened = true;
    }

    public void execute(String application, String data) {
        sb.append("    <execute application=\"")
          .append(xmlEscape(application))
          .append("\"");
        if (data != null) {
            sb.append(" data=\"").append(xmlEscape(data)).append("\"");
        }
        sb.append("/>\n");
    }

    public void speakSsml(String engine, String ssml) {
        sb.append("    <speak engine=\"")
          .append(xmlEscape(engine))
          .append("\">")
          .append(xmlEscape(ssml))
          .append("</speak>\n");
    }

    public void breakTag() {
        sb.append("    <break/>\n");
    }

    public String build() {
        if (opened) {
            sb.append("  </work>\n");
            sb.append("</document>\n");
            opened = false;
        }
        return sb.toString();
    }

    public static String xmlEscape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static String nlsmlToText(String nlsml) {
        if (nlsml == null || nlsml.isBlank()) return null;
        try {
            // very small best-effort extraction for <input>text</input>
            String lower = nlsml.toLowerCase();
            int i1 = lower.indexOf("<input");
            if (i1 >= 0) {
                int c1 = lower.indexOf('>', i1);
                int i2 = lower.indexOf("</input>", c1 + 1);
                if (c1 > 0 && i2 > c1) {
                    String mid = nlsml.substring(c1 + 1, i2).trim();
                    return mid.replaceAll("^\\[[^]]+]", "");
                }
            }
            // fallback: <speech-to-text>xxx</speech-to-text>
            int s1 = lower.indexOf("<speech-to-text>");
            if (s1 >= 0) {
                int c1 = s1 + "<speech-to-text>".length();
                int s2 = lower.indexOf("</speech-to-text>", c1);
                if (s2 > c1) {
                    String mid = nlsml.substring(c1, s2).trim();
                    return mid.replaceAll("^\\[[^]]+]", "");
                }
            }
        } catch (Exception ignore) {}
        return null;
    }
}
