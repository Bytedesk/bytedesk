package com.bytedesk.ai.mcp_client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ai.mcp.client.common.autoconfigure.NamedClientMcpTransport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.mcp_server.McpServerDirectionEnum;
import com.bytedesk.ai.mcp_server.McpServerEntity;
import com.bytedesk.ai.mcp_server.McpServerRepository;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.ai.mcp.client", name = "enabled", havingValue = "true")
public class McpClientManager {

    private static final String LEGACY_SSE_TRANSPORT_CLASS = "io.modelcontextprotocol.client.transport.HttpClientSseClientTransport";

    private static final List<String> CLIENT_DIRECTIONS = List.of(
            McpServerDirectionEnum.CLIENT.name(),
            McpServerDirectionEnum.DUAL.name());

    private final McpServerRepository mcpServerRepository;

    private final McpClientProperties mcpClientProperties;

    private final Map<String, McpClientConnectionConfig> connectionConfigs = new ConcurrentHashMap<>();

    private final Map<String, McpClientRuntime> clientRuntimes = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize() {
        refreshConnectionConfigs();
    }

    @PreDestroy
    public void destroy() {
        closeRuntimes(new ArrayList<>(clientRuntimes.values()));
        clientRuntimes.clear();
    }

    public List<McpClientConnectionConfig> listEnabledConnectionConfigs() {
        return connectionConfigs.values().stream()
                .sorted(Comparator.comparing(McpClientConnectionConfig::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<McpClientConnectionConfig> findConnectionConfig(String uid) {
        return Optional.ofNullable(connectionConfigs.get(uid));
    }

    public List<NamedClientMcpTransport> listClientTransports() {
        return clientRuntimes.values().stream()
                .sorted(Comparator.comparing(McpClientRuntime::name, String.CASE_INSENSITIVE_ORDER))
                .map(McpClientRuntime::namedTransport)
                .toList();
    }

    public List<McpSyncClient> listSyncClients() {
        return clientRuntimes.values().stream()
                .sorted(Comparator.comparing(McpClientRuntime::name, String.CASE_INSENSITIVE_ORDER))
                .map(McpClientRuntime::syncClient)
                .toList();
    }

    public Optional<McpSyncClient> findSyncClient(String uid) {
        return Optional.ofNullable(clientRuntimes.get(uid)).map(McpClientRuntime::syncClient);
    }

    public synchronized void refreshConnectionConfigs() {
        List<McpServerEntity> entities = mcpServerRepository.findByEnabledTrueAndDirectionInAndDeletedFalse(CLIENT_DIRECTIONS);
        List<McpServerEntity> limitedEntities = limitEntities(entities);

        Map<String, McpClientConnectionConfig> refreshedConfigs = new ConcurrentHashMap<>();
        Map<String, McpClientRuntime> refreshedRuntimes = new ConcurrentHashMap<>();
        for (McpServerEntity entity : limitedEntities) {
            if (entity == null || entity.getUid() == null) {
                continue;
            }
            McpClientConnectionConfig config = McpClientConnectionConfig.fromEntity(entity, mcpClientProperties);
            refreshedConfigs.put(entity.getUid(), config);
            createRuntime(config).ifPresent(runtime -> refreshedRuntimes.put(entity.getUid(), runtime));
        }

        List<McpClientRuntime> staleRuntimes = clientRuntimes.values().stream().toList();
        connectionConfigs.clear();
        connectionConfigs.putAll(refreshedConfigs);
        clientRuntimes.clear();
        clientRuntimes.putAll(refreshedRuntimes);
        closeRuntimes(staleRuntimes);
        log.info("Loaded {} external MCP connection configs", connectionConfigs.size());
        log.info("Initialized {} external MCP runtime clients", clientRuntimes.size());
    }

    private List<McpServerEntity> limitEntities(List<McpServerEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        int maxConnections = Math.max(1, mcpClientProperties.getMaxConnections());
        if (entities.size() <= maxConnections) {
            return entities;
        }
        log.warn("Configured external MCP servers {} exceed maxConnections {}, truncating in-memory config load", entities.size(), maxConnections);
        return new ArrayList<>(entities.subList(0, maxConnections));
    }

    private Optional<McpClientRuntime> createRuntime(McpClientConnectionConfig config) {
        try {
            NamedClientMcpTransport namedTransport = new NamedClientMcpTransport(config.name(), createTransport(config));
            McpSyncClient syncClient = McpClient.sync(namedTransport.transport())
                    .requestTimeout(toDuration(config.requestTimeoutMs()))
                    .initializationTimeout(toDuration(config.connectionTimeoutMs()))
                    .build();
            syncClient.initialize();
            return Optional.of(new McpClientRuntime(config.uid(), config.name(), namedTransport, syncClient));
        } catch (Exception ex) {
            log.warn("Failed to initialize external MCP client {} ({})", config.name(), config.uid(), ex);
            return Optional.empty();
        }
    }

    private io.modelcontextprotocol.spec.McpClientTransport createTransport(McpClientConnectionConfig config) {
        return switch (config.transportType()) {
            case STDIO -> createStdioTransport(config);
            case STREAMABLE_HTTP -> createStreamableHttpTransport(config);
            case SSE -> createSseCompatibleTransport(config);
        };
    }

    private io.modelcontextprotocol.spec.McpClientTransport createSseCompatibleTransport(McpClientConnectionConfig config) {
        if (usesLegacySseEndpoint(config.serverUrl())) {
            log.warn(
                    "MCP client transport type SSE for {} still targets the legacy HTTP+SSE protocol; keeping compatibility for now. Migrate this connection to STREAMABLE_HTTP and a /mcp endpoint.",
                    config.uid());
            return createLegacySseTransport(config);
        }
        log.warn("MCP client transport type SSE for {} is deprecated; treating it as STREAMABLE_HTTP", config.uid());
        return createStreamableHttpTransport(config);
    }

    private StdioClientTransport createStdioTransport(McpClientConnectionConfig config) {
        if (!StringUtils.hasText(config.stdioCommand())) {
            throw new IllegalArgumentException("stdioCommand is required for STDIO transport");
        }
        ServerParameters.Builder builder = ServerParameters.builder(config.stdioCommand());
        if (!config.stdioArgs().isEmpty()) {
            builder.args(config.stdioArgs());
        }
        if (!config.environmentVars().isEmpty()) {
            builder.env(config.environmentVars());
        }
        if (StringUtils.hasText(config.stdioWorkDir())) {
            log.warn("MCP stdio workdir is not applied yet for {} because StdioClientTransport does not expose a direct working-directory hook", config.uid());
        }
        StdioClientTransport transport = new StdioClientTransport(builder.build(), new JacksonMcpJsonMapper(JsonMapper.shared()));
        transport.setStdErrorHandler(stderr -> log.warn("MCP stdio stderr [{}]: {}", config.name(), stderr));
        return transport;
    }

    private io.modelcontextprotocol.spec.McpClientTransport createLegacySseTransport(McpClientConnectionConfig config) {
        EndpointParts endpointParts = resolveEndpoint(config.serverUrl(), "/sse");
        HttpRequest.Builder requestBuilder = createRequestBuilder(config);
        try {
            Class<?> transportClass = Class.forName(LEGACY_SSE_TRANSPORT_CLASS);
            Object builder = transportClass.getMethod("builder", String.class).invoke(null, endpointParts.baseUri());
            invokeBuilderMethod(builder, "sseEndpoint", String.class, endpointParts.endpoint());
            invokeBuilderMethod(builder, "requestBuilder", HttpRequest.Builder.class, requestBuilder);
            invokeBuilderMethod(builder, "clientBuilder", HttpClient.Builder.class,
                    HttpClient.newBuilder().connectTimeout(toDuration(config.connectionTimeoutMs())));
            invokeBuilderMethod(builder, "connectTimeout", Duration.class, toDuration(config.connectionTimeoutMs()));
            invokeBuilderMethod(builder, "jsonMapper", io.modelcontextprotocol.json.McpJsonMapper.class,
                    new JacksonMcpJsonMapper(JsonMapper.shared()));
            return (io.modelcontextprotocol.spec.McpClientTransport) invokeNoArgMethod(builder, "build");
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize legacy MCP SSE transport for " + config.uid(), ex);
        }
    }

    private HttpClientStreamableHttpTransport createStreamableHttpTransport(McpClientConnectionConfig config) {
        EndpointParts endpointParts = resolveEndpoint(config.serverUrl(), "/mcp");
        HttpRequest.Builder requestBuilder = createRequestBuilder(config);
        return HttpClientStreamableHttpTransport.builder(endpointParts.baseUri())
                .endpoint(endpointParts.endpoint())
                .requestBuilder(requestBuilder)
                .clientBuilder(HttpClient.newBuilder().connectTimeout(toDuration(config.connectionTimeoutMs())))
                .connectTimeout(toDuration(config.connectionTimeoutMs()))
                .jsonMapper(new JacksonMcpJsonMapper(JsonMapper.shared()))
                .build();
    }

    private boolean usesLegacySseEndpoint(String serverUrl) {
        if (!StringUtils.hasText(serverUrl)) {
            return false;
        }
        try {
            URI uri = new URI(serverUrl.trim());
            String path = uri.getPath();
            return StringUtils.hasText(path) && path.endsWith("/sse");
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private HttpRequest.Builder createRequestBuilder(McpClientConnectionConfig config) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .timeout(toDuration(config.requestTimeoutMs()));

        config.customHeaders().forEach(builder::header);

        String authorizationHeader = resolveAuthorizationHeader(config);
        if (StringUtils.hasText(authorizationHeader)) {
            builder.header("Authorization", authorizationHeader);
        }
        return builder;
    }

    private String resolveAuthorizationHeader(McpClientConnectionConfig config) {
        if (!StringUtils.hasText(config.authCredential())) {
            return null;
        }
        if (!StringUtils.hasText(config.authType())) {
            return config.authCredential();
        }
        String authType = config.authType().trim();
        if ("bearer".equalsIgnoreCase(authType)) {
            return "Bearer " + config.authCredential().trim();
        }
        if ("basic".equalsIgnoreCase(authType)) {
            String credential = config.authCredential().trim();
            if (credential.regionMatches(true, 0, "Basic ", 0, 6)) {
                return credential;
            }
            return "Basic " + Base64.getEncoder().encodeToString(credential.getBytes());
        }
        return config.authCredential();
    }

    private EndpointParts resolveEndpoint(String serverUrl, String defaultEndpoint) {
        if (!StringUtils.hasText(serverUrl)) {
            throw new IllegalArgumentException("serverUrl is required for HTTP-based MCP transport");
        }
        try {
            URI uri = new URI(serverUrl.trim());
            String baseUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null).toString();
            String path = StringUtils.hasText(uri.getPath()) ? uri.getPath() : defaultEndpoint;
            String endpoint = StringUtils.hasText(uri.getQuery()) ? path + "?" + uri.getQuery() : path;
            return new EndpointParts(baseUri, endpoint);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid serverUrl: " + serverUrl, ex);
        }
    }

    private Duration toDuration(int millis) {
        return Duration.ofMillis(Math.max(1000, millis));
    }

    private Object invokeBuilderMethod(Object target, String methodName, Class<?> parameterType, Object argument)
            throws ReflectiveOperationException {
        Method method = target.getClass().getMethod(methodName, parameterType);
        return method.invoke(target, argument);
    }

    private Object invokeNoArgMethod(Object target, String methodName) throws ReflectiveOperationException {
        Method method = target.getClass().getMethod(methodName);
        return method.invoke(target);
    }

    private void closeRuntimes(List<McpClientRuntime> runtimes) {
        for (McpClientRuntime runtime : runtimes) {
            if (runtime == null) {
                continue;
            }
            try {
                runtime.close();
            } catch (Exception ex) {
                log.debug("Failed to close external MCP client runtime {}", runtime.uid(), ex);
            }
        }
    }

    private record EndpointParts(String baseUri, String endpoint) {
    }

    private record McpClientRuntime(String uid, String name, NamedClientMcpTransport namedTransport, McpSyncClient syncClient)
            implements AutoCloseable {

        @Override
        public void close() {
            syncClient.close();
        }
    }
}