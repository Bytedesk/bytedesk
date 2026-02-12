package com.bytedesk.core.message.preview;

import java.io.InputStream;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UrlPreviewService {

    private static final int MAX_BYTES = 1024 * 1024; // 1MB
    private static final Duration TIMEOUT = Duration.ofSeconds(6);

    private static final Pattern TITLE_PATTERN = Pattern.compile("<title[^>]*>(.*?)</title>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern META_TAG_PATTERN = Pattern.compile("<meta\\s+[^>]*>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern ATTR_PATTERN = Pattern.compile("([a-zA-Z_:][-a-zA-Z0-9_:.]*)\\s*=\\s*(['\"])(.*?)\\2",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * Normalize and validate URL, returning canonical string form.
     *
     * Used by preview fetch and server-side dedupe for write-back caching.
     */
    public String normalizeUrl(String rawUrl) {
        return normalizeAndValidate(rawUrl).toString();
    }

    public UrlPreviewResponse preview(String rawUrl) {
        URI uri = normalizeAndValidate(rawUrl);

        String html = fetchHtml(uri);
        if (!StringUtils.hasText(html)) {
            return UrlPreviewResponse.builder().url(uri.toString()).build();
        }

        Map<String, String> meta = extractMeta(html);

        String title = firstNonBlank(
                meta.get("og:title"),
                meta.get("twitter:title"),
                extractTitle(html));

        String description = firstNonBlank(
                meta.get("og:description"),
                meta.get("twitter:description"),
                meta.get("description"));

        String imageUrl = firstNonBlank(
                meta.get("og:image"),
                meta.get("twitter:image"));

        String siteName = firstNonBlank(meta.get("og:site_name"));

        return UrlPreviewResponse.builder()
                .url(uri.toString())
                .title(truncate(cleanText(title), 200))
                .description(truncate(cleanText(description), 500))
                .imageUrl(truncate(cleanText(imageUrl), 500))
                .siteName(truncate(cleanText(siteName), 100))
                .build();
    }

    private URI normalizeAndValidate(String rawUrl) {
        if (!StringUtils.hasText(rawUrl)) {
            throw new IllegalArgumentException("url required");
        }

        String trimmed = rawUrl.trim();
        if (trimmed.startsWith("www.")) {
            trimmed = "https://" + trimmed;
        }

        URI uri;
        try {
            uri = URI.create(trimmed);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid url");
        }

        String scheme = Optional.ofNullable(uri.getScheme()).orElse("").toLowerCase(Locale.ROOT);
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new IllegalArgumentException("only http/https supported");
        }
        if (StringUtils.hasText(uri.getUserInfo())) {
            throw new IllegalArgumentException("userinfo not allowed");
        }

        String host = uri.getHost();
        if (!StringUtils.hasText(host)) {
            throw new IllegalArgumentException("host required");
        }

        host = IDN.toASCII(host);
        if (host.equalsIgnoreCase("localhost") || host.endsWith(".local")) {
            throw new IllegalArgumentException("local host not allowed");
        }

        ensureNotPrivateNetwork(host);

        return uri;
    }

    private void ensureNotPrivateNetwork(String host) {
        try {
            InetAddress[] addrs = InetAddress.getAllByName(host);
            for (InetAddress addr : addrs) {
                if (addr.isAnyLocalAddress() || addr.isLoopbackAddress() || addr.isLinkLocalAddress()
                        || addr.isSiteLocalAddress() || addr.isMulticastAddress()) {
                    throw new IllegalArgumentException("private network not allowed");
                }
                // 0.0.0.0/8
                if (addr.getHostAddress() != null && addr.getHostAddress().startsWith("0.")) {
                    throw new IllegalArgumentException("private network not allowed");
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("dns resolve failed");
        }
    }

    private String fetchHtml(URI uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(TIMEOUT)
                    .header("User-Agent", "BytedeskUrlPreview/1.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                log.debug("UrlPreview fetch non-2xx status={} url={}", status, uri);
                return "";
            }

            Charset charset = parseCharset(response.headers().firstValue("content-type").orElse(null));

            try (InputStream in = response.body()) {
                byte[] buf = in.readNBytes(MAX_BYTES + 1);
                if (buf.length > MAX_BYTES) {
                    log.debug("UrlPreview response too large url={}", uri);
                    return "";
                }
                return new String(buf, charset);
            }
        } catch (Exception e) {
            log.debug("UrlPreview fetch failed url={} err={}", uri, e.getMessage());
            return "";
        }
    }

    private Charset parseCharset(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return StandardCharsets.UTF_8;
        }
        String lower = contentType.toLowerCase(Locale.ROOT);
        int idx = lower.indexOf("charset=");
        if (idx < 0) {
            return StandardCharsets.UTF_8;
        }
        String cs = lower.substring(idx + "charset=".length()).trim();
        int semi = cs.indexOf(';');
        if (semi >= 0) {
            cs = cs.substring(0, semi).trim();
        }
        cs = cs.replace("\"", "").replace("'", "").trim();
        try {
            return Charset.forName(cs);
        } catch (Exception ignore) {
            return StandardCharsets.UTF_8;
        }
    }

    private Map<String, String> extractMeta(String html) {
        Map<String, String> map = new HashMap<>();
        Matcher tagMatcher = META_TAG_PATTERN.matcher(html);
        while (tagMatcher.find()) {
            String tag = tagMatcher.group();
            Map<String, String> attrs = parseAttributes(tag);
            String key = firstNonBlank(attrs.get("property"), attrs.get("name"));
            String content = attrs.get("content");
            if (!StringUtils.hasText(key) || !StringUtils.hasText(content)) {
                continue;
            }
            String normalizedKey = key.trim().toLowerCase(Locale.ROOT);
            if (!map.containsKey(normalizedKey)) {
                map.put(normalizedKey, content.trim());
            }
        }
        return map;
    }

    private Map<String, String> parseAttributes(String tag) {
        Map<String, String> attrs = new HashMap<>();
        Matcher m = ATTR_PATTERN.matcher(tag);
        while (m.find()) {
            String name = m.group(1);
            String value = m.group(3);
            if (name == null || value == null) continue;
            attrs.put(name.trim().toLowerCase(Locale.ROOT), value);
        }
        return attrs;
    }

    private String extractTitle(String html) {
        Matcher m = TITLE_PATTERN.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private String cleanText(String s) {
        if (!StringUtils.hasText(s)) return "";
        return s.replaceAll("\\s+", " ").trim();
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        String t = s.trim();
        if (t.length() <= max) return t;
        return t.substring(0, max);
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (StringUtils.hasText(v)) return v;
        }
        return null;
    }
}
