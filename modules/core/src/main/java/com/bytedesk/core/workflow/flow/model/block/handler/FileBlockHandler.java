package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.FileBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.StorageConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private static final long DEFAULT_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    
    public FileBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.FILE.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        FileBlockOptions options = objectMapper.convertValue(block.getOptions(), FileBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            // 验证存储配置
            validateStorageConfig(options.getStorageConfig());
            
            // 构建上传配置
            Map<String, Object> uploadConfig = buildUploadConfig(options, context);
            result.putAll(uploadConfig);
            
            // 处理已上传的文件（如果有）
            if (context.containsKey("files")) {
                handleUploadedFiles(context.get("files"), options, result);
            }
            
            result.put("success", true);
            result.put("blockType", "file");
            
        } catch (Exception e) {
            log.error("File block processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            FileBlockOptions options = objectMapper.convertValue(block.getOptions(), FileBlockOptions.class);
            return options.getStorageConfig() != null && 
                   options.getStorageConfig().getProvider() != null &&
                   validateStorageProvider(options.getStorageConfig().getProvider());
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> buildUploadConfig(FileBlockOptions options, Map<String, Object> context) {
        Map<String, Object> config = new HashMap<>();
        
        // 基本配置
        config.put("buttonLabel", processTemplate(options.getButtonLabel(), context));
        config.put("mimeTypes", options.getMimeTypes());
        config.put("maxSize", options.getMaxSize() != null ? options.getMaxSize() : DEFAULT_MAX_SIZE);
        config.put("isMultiple", options.isMultiple());
        
        // 存储配置
        StorageConfig storage = options.getStorageConfig();
        Map<String, Object> storageConfig = new HashMap<>();
        storageConfig.put("provider", storage.getProvider());
        storageConfig.put("path", storage.getPath());
        storageConfig.put("bucket", storage.getBucket());
        
        config.put("storage", storageConfig);
        return config;
    }

    private void handleUploadedFiles(Object filesObj, FileBlockOptions options, Map<String, Object> result) {
        List<MultipartFile> files = convertToFileList(filesObj);
        List<Map<String, Object>> fileInfos = files.stream()
            .map(file -> {
                Map<String, Object> info = new HashMap<>();
                info.put("name", file.getOriginalFilename());
                info.put("size", file.getSize());
                info.put("type", file.getContentType());
                return info;
            })
            .collect(Collectors.toList());
            
        result.put("uploadedFiles", fileInfos);
        
        if (options.getVariableName() != null) {
            result.put(options.getVariableName(), 
                options.isMultiple() ? fileInfos : fileInfos.get(0));
        }
    }

    private void validateStorageConfig(StorageConfig config) {
        if (config == null || config.getProvider() == null) {
            throw new IllegalArgumentException("Storage configuration is required");
        }
        
        if (config.getBucket() == null) {
            throw new IllegalArgumentException("Storage bucket is required");
        }
        
        if (config.getPath() == null) {
            throw new IllegalArgumentException("Storage path is required");
        }
    }

    private boolean validateStorageProvider(String provider) {
        return List.of("S3", "OSS", "LOCAL").contains(provider.toUpperCase());
    }

    private String generateUploadToken(StorageConfig config) {
        // TODO: 实现具体的上传令牌生成逻辑，可能需要根据不同的存储提供商生成不同格式的令牌
        return UUID.randomUUID().toString();
    }

    private String generateUploadUrl(StorageConfig config) {
        // TODO: 根据存储提供商生成上传URL
        String provider = config.getProvider().toUpperCase();
        switch (provider) {
            // case "S3":
            //     return generateS3UploadUrl(config);
            // case "OSS":
            //     return generateOssUploadUrl(config);
            case "LOCAL":
                return "/api/upload/local";  // 本地上传端点
            default:
                throw new IllegalArgumentException("Unsupported storage provider: " + provider);
        }
    }

    // private String generateS3UploadUrl(StorageConfig config) {
    //     return String.format("https://%s.s3.%s.amazonaws.com/", 
    //         config.getBucket(), config.getRegion());
    // }

    // private String generateOssUploadUrl(StorageConfig config) {
    //     return String.format("https://%s.%s/", 
    //         config.getBucket(), config.getEndpoint());
    // }

    private List<MultipartFile> convertToFileList(Object filesObj) {
        if (filesObj instanceof MultipartFile) {
            return Collections.singletonList((MultipartFile) filesObj);
        } else if (filesObj instanceof List) {
            return (List<MultipartFile>) filesObj;
        }
        throw new IllegalArgumentException("Invalid files input type");
    }

    private String processTemplate(String template, Map<String, Object> context) {
        if (template == null) return null;
        
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (template.contains(placeholder)) {
                template = template.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return template;
    }
} 
