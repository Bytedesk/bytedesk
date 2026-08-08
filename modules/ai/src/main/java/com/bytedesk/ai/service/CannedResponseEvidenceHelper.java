/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-07 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-07 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadExtra;

/**
 * Builds evidence field maps for canned response resolution,
 * flattening JSON structures from messages and threads into flat key-value pairs.
 * <p>
 * Extracted from BaseSpringAIService to reduce class size.
 */
@Component
public class CannedResponseEvidenceHelper {

    /**
     * Builds a flat evidence map from query, message, and thread context
     * for canned response matching.
     */
    public Map<String, String> buildEvidenceFields(String query, MessageProtobuf messageProtobufQuery,
            String defaultAnswer) {
        Map<String, String> evidence = new LinkedHashMap<>();
        putEvidence(evidence, "query", query);
        putEvidence(evidence, "defaultAnswer", defaultAnswer);
        if (messageProtobufQuery == null) {
            return evidence;
        }

        putEvidence(evidence, "messageContent", messageProtobufQuery.getContent());
        putEvidence(evidence, "messageExtra", messageProtobufQuery.getExtra());
        flattenJsonString(evidence, messageProtobufQuery.getContent(), "message");
        flattenJsonString(evidence, messageProtobufQuery.getExtra(), "messageExtra");

        if (messageProtobufQuery.getThread() != null) {
            putEvidence(evidence, "threadUid", messageProtobufQuery.getThread().getUid());
            putEvidence(evidence, "threadTopic", messageProtobufQuery.getThread().getTopic());
            putEvidence(evidence, "threadExtra", messageProtobufQuery.getThread().getExtra());
            flattenJsonString(evidence, messageProtobufQuery.getThread().getExtra(), "threadExtra");

            ThreadExtra threadExtra = ThreadExtra.fromJson(messageProtobufQuery.getThread().getExtra());
            if (threadExtra != null) {
                putEvidence(evidence, "workflowQuestionVariable", threadExtra.getWorkflowQuestionVariable());
                putEvidence(evidence, "workflowQuestionAnswer", threadExtra.getWorkflowQuestionAnswer());
                putEvidence(evidence, "workflowSelectedOptionValue", threadExtra.getWorkflowSelectedOptionValue());
                putEvidence(evidence, "workflowFormResponseData", threadExtra.getWorkflowFormResponseData());
                if (StringUtils.hasText(threadExtra.getWorkflowQuestionVariable())
                        && StringUtils.hasText(threadExtra.getWorkflowQuestionAnswer())) {
                    putEvidence(evidence, threadExtra.getWorkflowQuestionVariable(),
                            threadExtra.getWorkflowQuestionAnswer());
                }
                flattenJsonString(evidence, threadExtra.getWorkflowFormResponseData(), "form");
            }
        }
        return evidence;
    }

    // ──────────────────── JSON flattening helpers ────────────────────────────

    private void flattenJsonString(Map<String, String> evidence, String rawJson, String prefix) {
        if (!StringUtils.hasText(rawJson)) {
            return;
        }
        try {
            Object parsed = JSON.parse(rawJson);
            if (parsed instanceof JSONObject jsonObject) {
                flattenJsonObject(evidence, prefix, jsonObject);
            }
        } catch (Exception ignore) {
            // ignore non-JSON content
        }
    }

    private void flattenJsonObject(Map<String, String> evidence, String prefix, JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            String compoundKey = StringUtils.hasText(prefix) ? prefix + "." + key : key;
            if (value instanceof JSONObject nestedObject) {
                flattenJsonObject(evidence, compoundKey, nestedObject);
            } else if (value instanceof JSONArray jsonArray) {
                flattenJsonArray(evidence, compoundKey, jsonArray);
            } else if (value != null) {
                putEvidence(evidence, compoundKey, String.valueOf(value));
                putEvidence(evidence, key, String.valueOf(value));
            }
        }
    }

    private void flattenJsonArray(Map<String, String> evidence, String key, JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return;
        }
        List<String> scalarValues = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Object item = jsonArray.get(i);
            if (item instanceof JSONObject nestedObject) {
                flattenJsonObject(evidence, key + "[" + i + "]", nestedObject);
            } else if (item != null) {
                scalarValues.add(String.valueOf(item));
            }
        }
        if (!scalarValues.isEmpty()) {
            putEvidence(evidence, key, String.join(",", scalarValues));
        }
    }

    private void putEvidence(Map<String, String> evidence, String key, String value) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(value)) {
            return;
        }
        evidence.putIfAbsent(key, value.trim());
    }
}
