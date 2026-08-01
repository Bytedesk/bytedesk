/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-12 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-06-12 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.embedding_settings;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import com.bytedesk.ai.springai.providers.dashscope.BytedeskDashScopeEmbeddingModel;
import com.bytedesk.ai.springai.providers.dashscope.BytedeskDashScopeEmbeddingOptions;
import com.bytedesk.ai.springai.providers.openai.OpenAiCompatibleModelFactory;
import com.bytedesk.core.llm.LlmDefaults;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.vector.KbaseVectorStoreResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
public class EmbeddingSettingsKbaseVectorStoreResolver implements KbaseVectorStoreResolver {

    private final ObjectProvider<ElasticsearchVectorStore> elasticsearchVectorStoreProvider;

    private final EmbeddingSettingsRestService embeddingSettingsRestService;

    private final KbaseRestService kbaseRestService;

    private final Environment environment;

    public EmbeddingSettingsKbaseVectorStoreResolver(
            ObjectProvider<ElasticsearchVectorStore> elasticsearchVectorStoreProvider,
            EmbeddingSettingsRestService embeddingSettingsRestService,
            KbaseRestService kbaseRestService,
            Environment environment) {
        this.elasticsearchVectorStoreProvider = elasticsearchVectorStoreProvider;
        this.embeddingSettingsRestService = embeddingSettingsRestService;
        this.kbaseRestService = kbaseRestService;
        this.environment = environment;
    }

    private ElasticsearchVectorStore getElasticsearchVectorStore() {
        ElasticsearchVectorStore store = elasticsearchVectorStoreProvider.getIfAvailable();
        if (store == null) {
            throw new IllegalStateException("ElasticsearchVectorStore is not available");
        }
        return store;
    }

    private Rest5Client getRestClient() {
        String uris = environment.getProperty("spring.elasticsearch.uris");
        if (!StringUtils.hasText(uris)) {
            throw new IllegalStateException("spring.elasticsearch.uris is not configured");
        }
        var builder = Rest5Client.builder(Arrays.stream(StringUtils.commaDelimitedListToStringArray(uris))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(URI::create)
                .toArray(URI[]::new));
        String username = environment.getProperty("spring.elasticsearch.username");
        String password = environment.getProperty("spring.elasticsearch.password");
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider credentialsProvider = new org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider();
            credentialsProvider.setCredentials(new org.apache.hc.client5.http.auth.AuthScope((String) null, -1),
                    new org.apache.hc.client5.http.auth.UsernamePasswordCredentials(username, password.toCharArray()));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return builder.build();
    }

    @Override
    public VectorStore resolveByKbase(KbaseEntity kbase) {
        return resolveSettings(kbase)
                .map(this::buildVectorStore)
                .orElse(getElasticsearchVectorStore());
    }

    @Override
    public VectorStore resolveByKbUid(String kbUid) {
        if (!StringUtils.hasText(kbUid)) {
            return getElasticsearchVectorStore();
        }
        return kbaseRestService.findByUid(kbUid)
                .map(this::resolveByKbase)
                .orElse(getElasticsearchVectorStore());
    }

    @Override
    public VectorStore resolveDefault() {
        return getElasticsearchVectorStore();
    }

    /**
     * 使用 DB 中的默认 EmbeddingSettings 构建 VectorStore。
     * 如果 DB 中没有默认配置，返回 null（调用方应 fallback 到 Spring 托管的 store）。
     */
    public VectorStore resolveDefaultWithDbSettings() {
        return embeddingSettingsRestService
                .findDefaultByLevelAndType(LevelEnum.PLATFORM.name(), EmbeddingSettingsTypeEnum.KBASE.name())
                .filter(settings -> !settings.isDeleted())
                .filter(settings -> Boolean.TRUE.equals(settings.getEnabled()))
                .map(this::buildVectorStore)
                .orElse(null);
    }

    @Override
    public EmbeddingInfo getEmbeddingInfo(KbaseEntity kbase) {
        return resolveSettings(kbase)
                .map(settings -> new EmbeddingInfo(
                        resolveProvider(settings),
                        resolveModel(settings, null),
                        resolveModelDimensions(settings)))
                .orElse(null);
    }

    private Optional<EmbeddingSettingsEntity> resolveSettings(KbaseEntity kbase) {
        return embeddingSettingsRestService.findDefaultByLevelAndType(LevelEnum.PLATFORM.name(), EmbeddingSettingsTypeEnum.KBASE.name())
                .filter(settings -> !settings.isDeleted())
                .filter(settings -> Boolean.TRUE.equals(settings.getEnabled()));
    }

    private VectorStore buildVectorStore(EmbeddingSettingsEntity settings) {
        EmbeddingModel embeddingModel = buildEmbeddingModel(settings);
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        options.setIndexName(resolveIndexName(settings));
        options.setDimensions(resolveVectorDimensions(settings));
        return ElasticsearchVectorStore.builder(getRestClient(), embeddingModel)
                .options(options)
                .initializeSchema(true)
                .build();
    }

    EmbeddingModel buildEmbeddingModel(EmbeddingSettingsEntity settings) {
        String provider = resolveProvider(settings);
        if (LlmProviderConstants.DASHSCOPE.equals(provider)) {
            return buildDashscopeEmbeddingModel(settings);
        }
        if (LlmProviderConstants.ZHIPUAI.equals(provider)) {
            return buildZhipuaiEmbeddingModel(settings);
        }
        if (LlmProviderConstants.OLLAMA.equals(provider)) {
            return buildOllamaEmbeddingModel(settings);
        }
        return buildOpenAiCompatibleEmbeddingModel(settings, provider);
    }

    private EmbeddingModel buildDashscopeEmbeddingModel(EmbeddingSettingsEntity settings) {
        BytedeskDashScopeEmbeddingOptions.BytedeskDashScopeEmbeddingOptionsBuilder optionsBuilder = BytedeskDashScopeEmbeddingOptions.builder()
            .model(resolveModel(settings, "text-embedding-v4"));
        Integer dimensions = resolveModelDimensions(settings);
        if (dimensions != null && dimensions > 0) {
            optionsBuilder.dimensions(dimensions);
        }
        return new BytedeskDashScopeEmbeddingModel(
            resolveBaseUrl(settings, "https://dashscope.aliyuncs.com"),
            resolveApiKey(settings),
            optionsBuilder.build());
    }

    private EmbeddingModel buildZhipuaiEmbeddingModel(EmbeddingSettingsEntity settings) {
        ZhiPuAiApi api = new ZhiPuAiApi(resolveBaseUrl(settings, "https://open.bigmodel.cn/api/paas"), resolveApiKey(settings));
        ZhiPuAiEmbeddingOptions options = ZhiPuAiEmbeddingOptions.builder()
                .model(resolveModel(settings, "embedding-2"))
                .build();
        return new ZhiPuAiEmbeddingModel(api, MetadataMode.EMBED, options);
    }

    private EmbeddingModel buildOllamaEmbeddingModel(EmbeddingSettingsEntity settings) {
        OllamaApi api = OllamaApi.builder()
                .baseUrl(resolveBaseUrl(settings, "http://host.docker.internal:11434"))
                .build();
        OllamaEmbeddingOptions options = OllamaEmbeddingOptions.builder()
                .model(resolveModel(settings, "bge-m3:latest"))
                .build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(api)
            .options(options)
                .build();
    }

    private EmbeddingModel buildOpenAiCompatibleEmbeddingModel(EmbeddingSettingsEntity settings, String provider) {
        OpenAiEmbeddingOptions.Builder optionsBuilder = OpenAiEmbeddingOptions.builder()
                .model(resolveModel(settings, "text-embedding-3-small"));
        Integer dimensions = resolveModelDimensions(settings);
        if (dimensions != null && dimensions > 0) {
            optionsBuilder.dimensions(dimensions);
        }
        OpenAiEmbeddingOptions options = OpenAiCompatibleModelFactory.withConnection(optionsBuilder.build(),
            resolveBaseUrl(settings, resolveProviderBaseUrl(provider)), resolveApiKey(settings));
        return OpenAiCompatibleModelFactory.embeddingModel(options, MetadataMode.EMBED);
    }

    private String resolveProvider(EmbeddingSettingsEntity settings) {
        String provider = settings.getProvider();
        if (!StringUtils.hasText(provider)) {
            provider = LlmDefaults.DEFAULT_EMBEDDING_PROVIDER;
        }
        return provider.toLowerCase(Locale.ROOT);
    }

    private String resolveModel(EmbeddingSettingsEntity settings, String defaultModel) {
        if (StringUtils.hasText(settings.getModel())) {
            return settings.getModel();
        }
        String provider = resolveProvider(settings);
        return environment.getProperty("spring.ai." + provider + ".embedding.options.model", defaultModel);
    }

    private String resolveApiKey(EmbeddingSettingsEntity settings) {
        if (StringUtils.hasText(settings.getApiKey())) {
            return settings.getApiKey();
        }
        String provider = resolveProvider(settings);
        String embeddingApiKey = environment.getProperty("spring.ai." + provider + ".embedding.api-key");
        if (StringUtils.hasText(embeddingApiKey)) {
            return embeddingApiKey;
        }
        return environment.getProperty("spring.ai." + provider + ".api-key", "");
    }

    private String resolveBaseUrl(EmbeddingSettingsEntity settings, String defaultBaseUrl) {
        if (StringUtils.hasText(settings.getBaseUrl())) {
            return settings.getBaseUrl();
        }
        String provider = resolveProvider(settings);
        return environment.getProperty("spring.ai." + provider + ".base-url", defaultBaseUrl);
    }

    private String resolveProviderBaseUrl(String provider) {
        String baseUrl = environment.getProperty("spring.ai." + provider + ".base-url");
        if (StringUtils.hasText(baseUrl)) {
            return baseUrl;
        }
        if (LlmProviderConstants.SILICONFLOW.equals(provider)) {
            return "https://api.siliconflow.cn";
        }
        return "https://api.openai.com";
    }

    private String resolveIndexName(EmbeddingSettingsEntity settings) {
        if (StringUtils.hasText(settings.getVectorStoreIndexName())) {
            return settings.getVectorStoreIndexName();
        }
        return environment.getProperty("spring.ai.vectorstore.elasticsearch.index-name", "bytedesk_vs_index");
    }

    private Integer resolveVectorDimensions(EmbeddingSettingsEntity settings) {
        if (settings.getVectorStoreDimensions() != null && settings.getVectorStoreDimensions() > 0) {
            return settings.getVectorStoreDimensions();
        }
        Integer dimensions = resolveModelDimensions(settings);
        if (dimensions != null && dimensions > 0) {
            return dimensions;
        }
        return environment.getProperty("spring.ai.vectorstore.elasticsearch.dimensions", Integer.class, 1024);
    }

    private Integer resolveModelDimensions(EmbeddingSettingsEntity settings) {
        if (settings.getDimensions() != null && settings.getDimensions() > 0) {
            return settings.getDimensions();
        }
        String provider = resolveProvider(settings);
        return environment.getProperty("spring.ai." + provider + ".embedding.options.dimensions", Integer.class);
    }
}