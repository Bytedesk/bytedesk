package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.VideoBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.VideoProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class VideoBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    // private static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed/";
    // private static final String VIMEO_EMBED_URL = "https://player.vimeo.com/video/";
    
    public VideoBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.VIDEO.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        VideoBlockOptions options = objectMapper.convertValue(block.getOptions(), VideoBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            switch (options.getSourceType().toUpperCase()) {
                case "URL":
                    handleUrlVideo(options, result);
                    break;
                case "UPLOAD":
                    handleUploadedVideo(options, result);
                    break;
                case "YOUTUBE":
                case "VIMEO":
                    handleEmbeddedVideo(options, result);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported video source type: " + options.getSourceType());
            }
            
            // 添加通用视频配置
            result.put("autoplay", options.isAutoplay());
            result.put("loop", options.isLoop());
            result.put("muted", options.isMuted());
            
            if (options.getPosterUrl() != null) {
                result.put("posterUrl", options.getPosterUrl());
            }
            
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), true);
            }
            
            result.put("success", true);
            result.put("blockType", "video");
            
        } catch (Exception e) {
            log.error("Video block processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            VideoBlockOptions options = objectMapper.convertValue(block.getOptions(), VideoBlockOptions.class);
            return options.getSourceType() != null && 
                   (("URL".equalsIgnoreCase(options.getSourceType()) && options.getUrl() != null) ||
                    ("UPLOAD".equalsIgnoreCase(options.getSourceType()) && options.getContent() != null) ||
                    (("YOUTUBE".equalsIgnoreCase(options.getSourceType()) || 
                      "VIMEO".equalsIgnoreCase(options.getSourceType())) && 
                     options.getProvider() != null && 
                     options.getProvider().getVideoId() != null));
        } catch (Exception e) {
            return false;
        }
    }

    private void handleUrlVideo(VideoBlockOptions options, Map<String, Object> result) {
        result.put("videoUrl", options.getUrl());
        result.put("mimeType", options.getMimeType());
    }

    private void handleUploadedVideo(VideoBlockOptions options, Map<String, Object> result) {
        result.put("videoContent", options.getContent());
        result.put("mimeType", options.getMimeType());
    }

    private void handleEmbeddedVideo(VideoBlockOptions options, Map<String, Object> result) {
        VideoProvider provider = options.getProvider();
        String embedUrl = buildEmbedUrl(provider);
        
        result.put("embedUrl", embedUrl);
        result.put("videoId", provider.getVideoId());
        // result.put("providerType", provider.getType());
        
        // if (provider.getStartTime() != null) {
        //     result.put("startTime", provider.getStartTime());
        // }
        
        // if (provider.getEndTime() != null) {
        //     result.put("endTime", provider.getEndTime());
        // }
        
        if (provider.getSettings() != null) {
            result.put("providerSettings", provider.getSettings());
        }
    }

    private String buildEmbedUrl(VideoProvider provider) {
        StringBuilder url = new StringBuilder();
        
        // switch (provider.getType().toUpperCase()) {
        //     case "YOUTUBE":
        //         url.append(YOUTUBE_EMBED_URL)
        //            .append(provider.getVideoId());
                
        //         if (provider.getStartTime() != null || provider.getEndTime() != null) {
        //             url.append("?");
        //             if (provider.getStartTime() != null) {
        //                 url.append("start=").append(provider.getStartTime());
        //             }
        //             if (provider.getEndTime() != null) {
        //                 url.append(provider.getStartTime() != null ? "&" : "")
        //                    .append("end=").append(provider.getEndTime());
        //             }
        //         }
        //         break;
                
        //     case "VIMEO":
        //         url.append(VIMEO_EMBED_URL)
        //            .append(provider.getVideoId());
                
        //         if (provider.getStartTime() != null) {
        //             url.append("#t=").append(provider.getStartTime());
        //         }
        //         break;
                
        //     default:
        //         throw new IllegalArgumentException("Unsupported video provider: " + provider.getType());
        // }
        
        return url.toString();
    }
} 
