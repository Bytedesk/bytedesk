package com.bytedesk.ai.springai.service;

import java.math.BigDecimal;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.springai.event.LlmTokenUsageEvent;
import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenUsageHelper {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Record AI token usage statistics by publishing an event
     */
    public void recordAiTokenUsage(RobotProtobuf robot, String aiProvider, String aiModelType,
            long promptTokens, long completionTokens, boolean success, long responseTime) {
        try {
            if (robot == null || robot.getOrgUid() == null || robot.getOrgUid().isBlank()) {
                log.warn("Cannot record AI token usage: robot or orgUid is null");
                return;
            }

            BigDecimal tokenUnitPrice = getTokenUnitPrice(aiProvider, aiModelType);

            publishAiTokenUsageEvent(
                    robot.getOrgUid(),
                    aiProvider,
                    aiModelType,
                    promptTokens,
                    completionTokens,
                    success,
                    responseTime,
                    tokenUnitPrice);

            log.info(
                    "Published AI token usage event: provider={}, model={}, tokens={}+{}={}, success={}, responseTime={}ms",
                    aiProvider, aiModelType, promptTokens, completionTokens, promptTokens + completionTokens, success,
                    responseTime);
        } catch (Exception e) {
            log.error("Error publishing AI token usage event", e);
        }
    }

    /**
     * Get token unit price based on AI provider and model type
     */
    public BigDecimal getTokenUnitPrice(String aiProvider, String aiModelType) {
        if (LlmProviderConstants.OPENAI.equalsIgnoreCase(aiProvider)) {
            if ("gpt-4".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.03");
            } else if ("gpt-3.5-turbo".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            }
        } else if (LlmProviderConstants.BAIDU.equalsIgnoreCase(aiProvider)) {
            if ("ernie-bot-4".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.012");
            } else if ("ernie-bot".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            }
        } else if (LlmProviderConstants.ZHIPUAI.equalsIgnoreCase(aiProvider)) {
            if ("glm-4".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.01");
            } else if ("glm-3-turbo".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.005");
            }
        } else if (LlmProviderConstants.DASHSCOPE.equalsIgnoreCase(aiProvider)) {
            if ("qwen-turbo".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            } else if ("qwen-plus".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.01");
            }
        } else if (LlmProviderConstants.DEEPSEEK.equalsIgnoreCase(aiProvider)) {
            if ("deepseek-chat".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            } else if ("deepseek-coder".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.005");
            }
        } else if (LlmProviderConstants.GITEE.equalsIgnoreCase(aiProvider)) {
            if ("gitee-chat".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.001");
            }
        } else if (LlmProviderConstants.SILICONFLOW.equalsIgnoreCase(aiProvider)) {
            if ("siliconflow-chat".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            }
        } else if (LlmProviderConstants.VOLCENGINE.equalsIgnoreCase(aiProvider)) {
            if ("volcengine-chat".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            }
        } else if (LlmProviderConstants.OPENROUTER.equalsIgnoreCase(aiProvider)) {
            if ("openrouter-chat".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            }
        } else if (LlmProviderConstants.TENCENT.equalsIgnoreCase(aiProvider)) {
            if ("hunyuan-pro".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.01");
            } else if ("hunyuan-standard".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.002");
            }
        } else if (LlmProviderConstants.OLLAMA.equalsIgnoreCase(aiProvider)) {
            if ("llama2".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.0001");
            } else if ("mistral".equalsIgnoreCase(aiModelType)) {
                return new BigDecimal("0.0001");
            }
        }

        return new BigDecimal("0.01");
    }

    /**
     * Extract token usage from ChatResponse metadata
     */
    public ChatTokenUsage extractTokenUsage(Object response) {
        try {
            if (response instanceof ChatResponse) {
                ChatResponse chatResponse = (ChatResponse) response;
                var metadata = chatResponse.getMetadata();
                log.info("TokenUsageHelper extractTokenUsage metadata {}", metadata);

                if (metadata != null) {
                    Object promptTokens = metadata.get("prompt_tokens");
                    Object completionTokens = metadata.get("completion_tokens");
                    Object totalTokens = metadata.get("total_tokens");

                    long prompt = 0, completion = 0, total = 0;

                    if (promptTokens instanceof Number) {
                        prompt = ((Number) promptTokens).longValue();
                    }
                    if (completionTokens instanceof Number) {
                        completion = ((Number) completionTokens).longValue();
                    }
                    if (totalTokens instanceof Number) {
                        total = ((Number) totalTokens).longValue();
                    }

                    if (total > 0 && (prompt == 0 || completion == 0)) {
                        prompt = (long) (total * 0.3);
                        completion = total - prompt;
                    }

                    if (total == 0) {
                        Object usage = metadata.get("usage");
                        if (usage instanceof java.util.Map) {
                            java.util.Map<?, ?> usageMap = (java.util.Map<?, ?>) usage;
                            Object usagePromptTokens = usageMap.get("prompt_tokens");
                            Object usageCompletionTokens = usageMap.get("completion_tokens");
                            Object usageTotalTokens = usageMap.get("total_tokens");

                            if (usagePromptTokens instanceof Number) {
                                prompt = ((Number) usagePromptTokens).longValue();
                            }
                            if (usageCompletionTokens instanceof Number) {
                                completion = ((Number) usageCompletionTokens).longValue();
                            }
                            if (usageTotalTokens instanceof Number) {
                                total = ((Number) usageTotalTokens).longValue();
                            }
                        }

                        if (total == 0) {
                            String[] possibleTotalTokenKeys = { "total_tokens", "tokens", "token_count",
                                    "usage_total" };
                            String[] possiblePromptTokenKeys = { "prompt_tokens", "input_tokens", "request_tokens" };
                            String[] possibleCompletionTokenKeys = { "completion_tokens", "output_tokens",
                                    "response_tokens" };

                            for (String key : possibleTotalTokenKeys) {
                                Object value = metadata.get(key);
                                if (value instanceof Number) {
                                    total = ((Number) value).longValue();
                                    break;
                                }
                            }

                            for (String key : possiblePromptTokenKeys) {
                                Object value = metadata.get(key);
                                if (value instanceof Number) {
                                    prompt = ((Number) value).longValue();
                                    break;
                                }
                            }

                            for (String key : possibleCompletionTokenKeys) {
                                Object value = metadata.get(key);
                                if (value instanceof Number) {
                                    completion = ((Number) value).longValue();
                                    break;
                                }
                            }
                        }

                        if (total == 0) {
                            log.info(
                                    "TokenUsageHelper extractTokenUsage no token info found in metadata, checking response structure");
                            for (String key : metadata.keySet()) {
                                Object value = metadata.get(key);
                                log.info("TokenUsageHelper extractTokenUsage metadata [{}]: {} (class: {})",
                                        key, value, value != null ? value.getClass().getName() : "null");
                            }

                            String metadataStr = metadata.toString();
                            log.info("TokenUsageHelper extractTokenUsage parsing metadata string: {}", metadataStr);

                            if (metadataStr.contains("DefaultUsage{") || metadataStr.contains("usage:")) {
                                int promptStart = metadataStr.indexOf("promptTokens=");
                                int promptEnd = metadataStr.indexOf(",", promptStart);
                                if (promptEnd == -1)
                                    promptEnd = metadataStr.indexOf("}", promptStart);

                                int completionStart = metadataStr.indexOf("completionTokens=");
                                int completionEnd = metadataStr.indexOf(",", completionStart);
                                if (completionEnd == -1)
                                    completionEnd = metadataStr.indexOf("}", completionStart);

                                int totalStart = metadataStr.indexOf("totalTokens=");
                                int totalEnd = metadataStr.indexOf("}", totalStart);

                                if (promptStart > 0 && promptEnd > promptStart) {
                                    try {
                                        prompt = Long.parseLong(metadataStr.substring(promptStart + 13, promptEnd));
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse promptTokens from: {}",
                                                metadataStr.substring(promptStart + 13, promptEnd));
                                    }
                                }

                                if (completionStart > 0 && completionEnd > completionStart) {
                                    try {
                                        completion = Long
                                                .parseLong(metadataStr.substring(completionStart + 17, completionEnd));
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse completionTokens from: {}",
                                                metadataStr.substring(completionStart + 17, completionEnd));
                                    }
                                }

                                if (totalStart > 0 && totalEnd > totalStart) {
                                    try {
                                        total = Long.parseLong(metadataStr.substring(totalStart + 12, totalEnd));
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse totalTokens from: {}",
                                                metadataStr.substring(totalStart + 12, totalEnd));
                                    }
                                }

                                log.info(
                                        "TokenUsageHelper extractTokenUsage parsed from string - prompt: {}, completion: {}, total: {}",
                                        prompt, completion, total);
                            }

                            if (total == 0) {
                                total = extractTokenUsageFromResponse(chatResponse);
                                if (total > 0) {
                                    prompt = (long) (total * 0.3);
                                    completion = total - prompt;
                                }
                            }
                        }
                    }

                    log.info(
                            "TokenUsageHelper extractTokenUsage extracted tokens - prompt: {}, completion: {}, total: {}",
                            prompt, completion, total);
                    return new ChatTokenUsage(prompt, completion, total);
                }
            }
        } catch (Exception e) {
            log.error("Error extracting token usage from response", e);
        }

        log.warn("TokenUsageHelper extractTokenUsage could not extract token usage, returning zeros");
        return new ChatTokenUsage(0, 0, 0);
    }

    /**
     * Extract token usage from response object itself (for cases where metadata doesn't contain token info)
     */
    private long extractTokenUsageFromResponse(ChatResponse chatResponse) {
        try {
            if (chatResponse.getResults() != null && !chatResponse.getResults().isEmpty()) {
                for (org.springframework.ai.chat.model.Generation generation : chatResponse.getResults()) {
                    var generationMetadata = generation.getMetadata();
                    if (generationMetadata != null) {
                        log.info("TokenUsageHelper extractTokenUsageFromResponse generation metadata: {}",
                                generationMetadata);
                        Object tokens = generationMetadata.get("tokens");
                        if (tokens instanceof Number) {
                            return ((Number) tokens).longValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting token usage from response object", e);
        }
        return 0;
    }

    /**
     * Publish AI token usage event
     */
    public void publishAiTokenUsageEvent(String orgUid, String aiProvider, String aiModelType,
            long promptTokens, long completionTokens, boolean success, long responseTime, BigDecimal tokenUnitPrice) {

        LlmTokenUsageEvent event = new LlmTokenUsageEvent(this, orgUid, aiProvider, aiModelType,
                promptTokens, completionTokens, success, responseTime, tokenUnitPrice);

        applicationEventPublisher.publishEvent(event);

        log.debug(
                "Published AI token usage event: provider={}, model={}, tokens={}+{}={}, success={}, responseTime={}ms",
                aiProvider, aiModelType, promptTokens, completionTokens, promptTokens + completionTokens, success,
                responseTime);
    }
}
