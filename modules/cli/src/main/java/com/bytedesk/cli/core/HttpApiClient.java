package com.bytedesk.cli.core;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpApiClient {

	public static final String SERVER_KEY = "server.base-url";
	public static final String TOKEN_KEY = "auth.token";
	public static final String PLATFORM_KEY = "auth.platform";
	public static final String CHANNEL_KEY = "auth.channel";
	public static final String CURRENT_ORG_UID_KEY = "auth.current-org-uid";
	public static final String CURRENT_ORG_NAME_KEY = "auth.current-org-name";
	public static final String CURRENT_USER_UID_KEY = "auth.current-user-uid";
	public static final String CURRENT_USER_NICKNAME_KEY = "auth.current-user-nickname";

	private final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.build();

	public Map<String, Object> get(CliContext context, String path, Map<String, String> query, boolean authorized) {
		return send(context, "GET", path, query, null, authorized);
	}

	public Map<String, Object> post(CliContext context, String path, Map<String, Object> body, boolean authorized) {
		return send(context, "POST", path, Map.of(), body, authorized);
	}

	public Map<String, Object> send(CliContext context, String method, String path, Map<String, String> query, Map<String, Object> body, boolean authorized) {
		String server = context.configStore().get(SERVER_KEY)
			.orElseThrow(() -> new IllegalStateException("Missing server.base-url. Run config set or auth login first."));
		String url = buildUrl(server, path, query);
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.timeout(Duration.ofSeconds(20))
			.header("Accept", "application/json");

		if (authorized) {
			String token = context.configStore().get(TOKEN_KEY)
				.orElseThrow(() -> new IllegalStateException("Missing auth token. Run auth login first."));
			builder.header("Authorization", "Bearer " + token);
		}

		if ("POST".equals(method)) {
			String payload = JsonFormatter.toJson(body == null ? Map.of() : body);
			builder.header("Content-Type", "application/json");
			builder.POST(HttpRequest.BodyPublishers.ofString(payload));
		} else {
			builder.GET();
		}

		try {
			HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
			String responseBody = response.body() == null ? "" : response.body();
			if (response.statusCode() >= 400) {
				throw new IllegalStateException("HTTP " + response.statusCode() + ": " + responseBody);
			}
			Object parsed = JsonParser.parse(responseBody);
			Map<String, Object> json = Jsons.object(parsed);
			int code = Jsons.integer(json, "code", 500);
			if (code >= 400 || Boolean.FALSE.equals(json.get("data")) && code != 200) {
				throw new IllegalStateException(Jsons.string(json, "message"));
			}
			return json;
		} catch (IOException | InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("API request failed: " + exception.getMessage(), exception);
		}
	}

	public static Map<String, Object> jsonBody(Object... keyValues) {
		Map<String, Object> body = new LinkedHashMap<>();
		for (int index = 0; index < keyValues.length; index += 2) {
			Object value = keyValues[index + 1];
			if (value != null) {
				body.put(String.valueOf(keyValues[index]), value);
			}
		}
		return body;
	}

	private String buildUrl(String server, String path, Map<String, String> query) {
		StringBuilder builder = new StringBuilder();
		builder.append(server.endsWith("/") ? server.substring(0, server.length() - 1) : server);
		if (!path.startsWith("/")) {
			builder.append('/');
		}
		builder.append(path);
		if (!query.isEmpty()) {
			builder.append('?');
			builder.append(query.entrySet().stream()
				.filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
				.map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
					+ "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&")));
		}
		return builder.toString();
	}

}