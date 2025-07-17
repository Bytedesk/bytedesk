package com.bytedesk.ai.springai.rag;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmbeddingModelInfoServiceTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private EmbeddingModel mockEmbeddingModel;

    @InjectMocks
    private EmbeddingModelInfoService embeddingModelInfoService;

    @Test
    void testGetAllEmbeddingModelsInfo_WithPrimaryModel() {
        // Given
        when(applicationContext.getBean(EmbeddingModel.class)).thenReturn(mockEmbeddingModel);
        when(mockEmbeddingModel.embed(any(String.class))).thenReturn(new float[1024]);

        // When
        Map<String, Object> result = embeddingModelInfoService.getAllEmbeddingModelsInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("embeddingModels"));
        assertTrue(result.containsKey("primaryProvider"));
        assertTrue(result.containsKey("timestamp"));
        
        @SuppressWarnings("unchecked")
        var embeddingModels = (java.util.List<Map<String, Object>>) result.get("embeddingModels");
        assertNotNull(embeddingModels);
    }

    @Test
    void testGetPrimaryEmbeddingModelInfo_WithPrimaryModel() {
        // Given
        when(applicationContext.getBean(EmbeddingModel.class)).thenReturn(mockEmbeddingModel);
        when(mockEmbeddingModel.embed(any(String.class))).thenReturn(new float[1024]);

        // When
        Map<String, Object> result = embeddingModelInfoService.getPrimaryEmbeddingModelInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("primaryModel"));
        assertTrue(result.containsKey("timestamp"));
        
        @SuppressWarnings("unchecked")
        var primaryModel = (Map<String, Object>) result.get("primaryModel");
        assertNotNull(primaryModel);
        assertTrue((Boolean) primaryModel.get("isPrimary"));
    }

    @Test
    void testGetVectorStoreEmbeddingModelInfo_WithPrimaryModel() {
        // Given
        when(applicationContext.getBean(EmbeddingModel.class)).thenReturn(mockEmbeddingModel);
        when(mockEmbeddingModel.embed(any(String.class))).thenReturn(new float[1024]);

        // When
        Map<String, Object> result = embeddingModelInfoService.getVectorStoreEmbeddingModelInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("vectorStoreModel"));
        assertTrue(result.containsKey("timestamp"));
        
        @SuppressWarnings("unchecked")
        var vectorStoreModel = (Map<String, Object>) result.get("vectorStoreModel");
        assertNotNull(vectorStoreModel);
        assertEquals("VectorStore", vectorStoreModel.get("usedBy"));
    }

    @Test
    void testGetAllEmbeddingModelsInfo_WithoutPrimaryModel() {
        // Given
        when(applicationContext.getBean(EmbeddingModel.class)).thenThrow(new RuntimeException("No bean found"));

        // When
        Map<String, Object> result = embeddingModelInfoService.getAllEmbeddingModelsInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("embeddingModels"));
        assertTrue(result.containsKey("primaryProvider"));
        assertTrue(result.containsKey("timestamp"));
        
        @SuppressWarnings("unchecked")
        var embeddingModels = (java.util.List<Map<String, Object>>) result.get("embeddingModels");
        assertNotNull(embeddingModels);
    }

    @Test
    void testGetPrimaryEmbeddingModelInfo_WithoutPrimaryModel() {
        // Given
        when(applicationContext.getBean(EmbeddingModel.class)).thenThrow(new RuntimeException("No bean found"));

        // When
        Map<String, Object> result = embeddingModelInfoService.getPrimaryEmbeddingModelInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertTrue(result.containsKey("status"));
        assertEquals("Not Configured", result.get("status"));
    }
} 