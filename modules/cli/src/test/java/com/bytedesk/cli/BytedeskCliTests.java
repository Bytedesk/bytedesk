package com.bytedesk.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bytedesk.cli.core.BytedeskCli;
import com.bytedesk.cli.core.CliConfigStore;
import com.bytedesk.cli.core.HttpApiClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class BytedeskCliTests {

	@Test
	void shouldPrintHelpWhenNoArgs() {
		TestRuntime runtime = new TestRuntime();
		BytedeskCli cli = runtime.createCli();

		int exitCode = cli.run(new String[0]);

		assertEquals(0, exitCode);
		assertTrue(runtime.stdout().contains("Usage: bytedesk"));
		assertTrue(runtime.stdout().contains("auth"));
	}

	@Test
	void shouldPersistLoginConfiguration() {
		TestRuntime runtime = new TestRuntime();
		BytedeskCli cli = runtime.createCli();

		int exitCode = cli.run(new String[] { "auth", "login", "--server", "https://api.bytedesk.com", "--token", "demo-token" });

		assertEquals(0, exitCode);
		assertEquals(Optional.of("https://api.bytedesk.com"), runtime.configStore.get("server.base-url"));
		assertEquals(Optional.of("demo-token"), runtime.configStore.get("auth.token"));
	}

	@Test
	void shouldRenderJsonOutput() {
		TestRuntime runtime = new TestRuntime();
		BytedeskCli cli = runtime.createCli();

		int exitCode = cli.run(new String[] { "--format=json", "version" });

		assertEquals(0, exitCode);
		assertTrue(runtime.stdout().contains("\"cli\":\"bytedesk\""));
		assertTrue(runtime.stdout().contains("\"success\":true"));
	}

	@Test
	void shouldResolveVersionFromApplicationVersionSystemProperty() {
		String originalVersion = System.getProperty("application.version");
		System.setProperty("application.version", "9.9.9-test");
		try {
			assertEquals("9.9.9-test", BytedeskCli.resolveVersion(BytedeskCli.class));
		} finally {
			restoreSystemProperty("application.version", originalVersion);
		}
	}

	@Test
	void shouldLoginViaApiAndStoreToken() throws Exception {
		try (FakeApiServer server = new FakeApiServer()) {
			server.add("/auth/v1/login", exchange -> server.json(exchange, "{\"message\":\"success\",\"code\":200,\"data\":{\"accessToken\":\"token-123\",\"user\":{\"username\":\"admin\",\"currentOrganization\":{\"uid\":\"org-1\",\"name\":\"Acme\"}}}}"));
			TestRuntime runtime = new TestRuntime();
			BytedeskCli cli = runtime.createCli();

			int exitCode = cli.run(new String[] { "auth", "login", "--server", server.baseUrl(), "--username", "admin", "--password", "secret" });

			assertEquals(0, exitCode);
			assertEquals(Optional.of("token-123"), runtime.configStore.get(HttpApiClient.TOKEN_KEY));
			assertEquals(Optional.of("org-1"), runtime.configStore.get(HttpApiClient.CURRENT_ORG_UID_KEY));
		}
	}

	@Test
	void shouldListOrganizationsViaApi() throws Exception {
		try (FakeApiServer server = new FakeApiServer()) {
			server.add("/api/v1/user/organizations", exchange -> server.json(exchange, "{\"message\":\"success\",\"code\":200,\"data\":[{\"uid\":\"org-1\",\"name\":\"Acme\",\"code\":\"ACME\"},{\"uid\":\"org-2\",\"name\":\"Beta\",\"code\":\"BETA\"}]}"));
			TestRuntime runtime = new TestRuntime();
			runtime.configStore.put(HttpApiClient.SERVER_KEY, server.baseUrl());
			runtime.configStore.put(HttpApiClient.TOKEN_KEY, "token-123");
			runtime.configStore.put(HttpApiClient.CURRENT_ORG_UID_KEY, "org-1");
			BytedeskCli cli = runtime.createCli();

			int exitCode = cli.run(new String[] { "org", "list" });

			assertEquals(0, exitCode);
			assertTrue(runtime.stdout().contains("Acme"));
			assertTrue(runtime.stdout().contains("Beta"));
		}
	}

	@Test
	void shouldListTicketsViaApi() throws Exception {
		try (FakeApiServer server = new FakeApiServer()) {
			server.add("/api/v1/ticket/query", exchange -> server.json(exchange, "{\"message\":\"success\",\"code\":200,\"data\":{\"content\":[{\"uid\":\"ticket-1\",\"title\":\"Login failed\",\"status\":\"OPEN\",\"priority\":\"HIGH\",\"createdAt\":\"2026-04-12 08:00:00\"}],\"totalElements\":1}}"));
			TestRuntime runtime = new TestRuntime();
			runtime.configStore.put(HttpApiClient.SERVER_KEY, server.baseUrl());
			runtime.configStore.put(HttpApiClient.TOKEN_KEY, "token-123");
			BytedeskCli cli = runtime.createCli();

			int exitCode = cli.run(new String[] { "ticket", "list" });

			assertEquals(0, exitCode);
			assertTrue(runtime.stdout().contains("Login failed"));
			assertTrue(runtime.stdout().contains("OPEN"));
		}
	}

	private static final class TestRuntime {

		private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		private final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		private final InMemoryConfigStore configStore = new InMemoryConfigStore();

		private BytedeskCli createCli() {
			return new BytedeskCli("bytedesk", BytedeskCli.resolveVersion(BytedeskCli.class), configStore, new PrintStream(stdout), new PrintStream(stderr));
		}

		private String stdout() {
			return stdout.toString();
		}
	}

	private static void restoreSystemProperty(String key, String value) {
		if (value == null) {
			System.clearProperty(key);
			return;
		}
		System.setProperty(key, value);
	}

	private static final class InMemoryConfigStore implements CliConfigStore {

		private final Map<String, String> values = new LinkedHashMap<>();

		@Override
		public Optional<String> get(String key) {
			return Optional.ofNullable(values.get(key));
		}

		@Override
		public Map<String, String> list() {
			return Map.copyOf(values);
		}

		@Override
		public void put(String key, String value) {
			values.put(key, value);
		}

		@Override
		public void remove(String key) {
			values.remove(key);
		}
	}

	private static final class FakeApiServer implements AutoCloseable {

		private final HttpServer server;

		private FakeApiServer() throws Exception {
			server = HttpServer.create(new InetSocketAddress(0), 0);
			server.start();
		}

		private void add(String path, HttpHandler handler) {
			server.createContext(path, handler);
		}

		private String baseUrl() {
			return "http://127.0.0.1:" + server.getAddress().getPort();
		}

		private void json(HttpExchange exchange, String body) throws java.io.IOException {
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, body.getBytes().length);
			try (OutputStream outputStream = exchange.getResponseBody()) {
				outputStream.write(body.getBytes());
			}
		}

		@Override
		public void close() {
			server.stop(0);
		}
	}

}